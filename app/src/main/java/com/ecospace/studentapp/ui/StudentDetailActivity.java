package com.ecospace.studentapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ecospace.studentapp.R;
import com.ecospace.studentapp.database.DatabaseHelper;
import com.ecospace.studentapp.model.Student;
import com.google.android.material.button.MaterialButton;

/**
 * Activity for displaying student details
 * Written in Java
 */
public class StudentDetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private long studentId;
    private Student currentStudent;

    private TextView tvInitials;
    private TextView tvName;
    private TextView tvStudentId;
    private TextView tvEmail;
    private TextView tvPhone;
    private TextView tvCourse;
    private TextView tvEnrollmentDate;
    private TextView tvGpa;
    
    private MaterialButton btnEdit;
    private MaterialButton btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);

        dbHelper = DatabaseHelper.getInstance(this);
        studentId = getIntent().getLongExtra("student_id", -1);

        if (studentId == -1) {
            finish();
            return;
        }

        initViews();
        loadStudentData();
        setupClickListeners();
    }

    private void initViews() {
        tvInitials = findViewById(R.id.tvInitials);
        tvName = findViewById(R.id.tvName);
        tvStudentId = findViewById(R.id.tvStudentId);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvCourse = findViewById(R.id.tvCourse);
        tvEnrollmentDate = findViewById(R.id.tvEnrollmentDate);
        tvGpa = findViewById(R.id.tvGpa);
        
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadStudentData() {
        currentStudent = dbHelper.getStudent(studentId);
        
        if (currentStudent == null) {
            finish();
            return;
        }

        // Generate initials
        String[] nameParts = currentStudent.getName().split(" ");
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < Math.min(2, nameParts.length); i++) {
            if (!nameParts[i].isEmpty()) {
                initials.append(nameParts[i].charAt(0));
            }
        }
        tvInitials.setText(initials.toString().toUpperCase());

        tvName.setText(currentStudent.getName());
        tvStudentId.setText(currentStudent.getStudentId());
        tvEmail.setText(currentStudent.getEmail());
        tvPhone.setText(currentStudent.getPhone());
        tvCourse.setText(currentStudent.getCourse());
        
        String enrollmentDate = currentStudent.getEnrollmentDate();
        tvEnrollmentDate.setText(enrollmentDate.isEmpty() ? "Not specified" : enrollmentDate);
        
        double gpa = currentStudent.getGpa();
        tvGpa.setText(gpa > 0 ? String.format("%.2f", gpa) : "Not specified");
    }

    private void setupClickListeners() {
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditStudentActivity.class);
            intent.putExtra("student_id", studentId);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this, R.style.GreenAlertDialog)
            .setTitle("Delete Student")
            .setMessage("Are you sure you want to delete " + currentStudent.getName() + "? This action cannot be undone.")
            .setPositiveButton("Delete", (dialog, which) -> {
                dbHelper.deleteStudent(studentId);
                finish();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudentData();
    }
}
