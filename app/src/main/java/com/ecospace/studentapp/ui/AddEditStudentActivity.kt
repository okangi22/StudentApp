package com.ecospace.studentapp.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ecospace.studentapp.R
import com.ecospace.studentapp.database.DatabaseHelper
import com.ecospace.studentapp.model.Student
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

/**
 * Activity for adding or editing student records
 * Written in Kotlin for modern Android development
 */
class AddEditStudentActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    
    private lateinit var tilStudentId: TextInputLayout
    private lateinit var tilName: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPhone: TextInputLayout
    private lateinit var tilCourse: TextInputLayout
    private lateinit var tilEnrollmentDate: TextInputLayout
    private lateinit var tilGpa: TextInputLayout
    
    private lateinit var etStudentId: TextInputEditText
    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var actvCourse: AutoCompleteTextView
    private lateinit var etEnrollmentDate: TextInputEditText
    private lateinit var etGpa: TextInputEditText
    
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton
    
    private var studentId: Long = -1
    private var isEditMode = false
    
    private val courses = arrayOf(
        "Computer Science",
        "Information Technology",
        "Software Engineering",
        "Data Science",
        "Cybersecurity",
        "Business Administration",
        "Electrical Engineering",
        "Mechanical Engineering",
        "Civil Engineering",
        "Mathematics"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_student)
        
        dbHelper = DatabaseHelper.getInstance(this)
        
        initViews()
        setupCourseDropdown()
        setupDatePicker()
        checkEditMode()
        setupClickListeners()
    }

    private fun initViews() {
        tilStudentId = findViewById(R.id.tilStudentId)
        tilName = findViewById(R.id.tilName)
        tilEmail = findViewById(R.id.tilEmail)
        tilPhone = findViewById(R.id.tilPhone)
        tilCourse = findViewById(R.id.tilCourse)
        tilEnrollmentDate = findViewById(R.id.tilEnrollmentDate)
        tilGpa = findViewById(R.id.tilGpa)
        
        etStudentId = findViewById(R.id.etStudentId)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        actvCourse = findViewById(R.id.actvCourse)
        etEnrollmentDate = findViewById(R.id.etEnrollmentDate)
        etGpa = findViewById(R.id.etGpa)
        
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        
        // Set default enrollment date to today
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        etEnrollmentDate.setText(dateFormat.format(Date()))
    }

    private fun setupCourseDropdown() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, courses)
        actvCourse.setAdapter(adapter)
    }

    private fun setupDatePicker() {
        etEnrollmentDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            
            DatePickerDialog(
                this,
                R.style.GreenDatePickerDialog,
                { _, year, month, dayOfMonth ->
                    val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    etEnrollmentDate.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun checkEditMode() {
        studentId = intent.getLongExtra("student_id", -1)
        isEditMode = studentId != -1L
        
        if (isEditMode) {
            title = "Edit Student"
            loadStudentData()
        } else {
            title = "Add New Student"
        }
    }

    private fun loadStudentData() {
        val student = dbHelper.getStudent(studentId)
        student?.let {
            etStudentId.setText(it.studentId)
            etName.setText(it.name)
            etEmail.setText(it.email)
            etPhone.setText(it.phone)
            actvCourse.setText(it.course, false)
            etEnrollmentDate.setText(it.enrollmentDate)
            etGpa.setText(if (it.gpa > 0) it.gpa.toString() else "")
        }
    }

    private fun setupClickListeners() {
        btnSave.setOnClickListener {
            if (validateInputs()) {
                saveStudent()
            }
        }
        
        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        
        // Clear previous errors
        tilStudentId.error = null
        tilName.error = null
        tilEmail.error = null
        tilPhone.error = null
        tilCourse.error = null
        
        val studentIdText = etStudentId.text.toString().trim()
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val course = actvCourse.text.toString().trim()
        
        if (studentIdText.isEmpty()) {
            tilStudentId.error = "Student ID is required"
            isValid = false
        } else if (dbHelper.isStudentIdExists(studentIdText, studentId)) {
            tilStudentId.error = "Student ID already exists"
            isValid = false
        }
        
        if (name.isEmpty()) {
            tilName.error = "Name is required"
            isValid = false
        }
        
        if (email.isEmpty()) {
            tilEmail.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.error = "Invalid email format"
            isValid = false
        }
        
        if (phone.isEmpty()) {
            tilPhone.error = "Phone is required"
            isValid = false
        }
        
        if (course.isEmpty()) {
            tilCourse.error = "Course is required"
            isValid = false
        }
        
        return isValid
    }

    private fun saveStudent() {
        val gpaText = etGpa.text.toString().trim()
        val gpa = if (gpaText.isNotEmpty()) gpaText.toDoubleOrNull() ?: 0.0 else 0.0
        
        val student = Student(
            id = if (isEditMode) studentId else 0,
            studentId = etStudentId.text.toString().trim(),
            name = etName.text.toString().trim(),
            email = etEmail.text.toString().trim(),
            phone = etPhone.text.toString().trim(),
            course = actvCourse.text.toString().trim(),
            enrollmentDate = etEnrollmentDate.text.toString().trim(),
            gpa = gpa
        )
        
        if (isEditMode) {
            dbHelper.updateStudent(student)
            Toast.makeText(this, "Student updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            dbHelper.insertStudent(student)
            Toast.makeText(this, "Student added successfully", Toast.LENGTH_SHORT).show()
        }
        
        setResult(RESULT_OK)
        finish()
    }
}
