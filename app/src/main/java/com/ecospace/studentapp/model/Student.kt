package com.ecospace.studentapp.model

/**
 * Data class representing a Student entity
 * Using Kotlin for clean, concise model definition
 */
data class Student(
    val id: Long = 0,
    val studentId: String,
    val name: String,
    val email: String,
    val phone: String,
    val course: String,
    val enrollmentDate: String = "",
    val gpa: Double = 0.0
) {
    companion object {
        const val TABLE_NAME = "students"
        const val COLUMN_ID = "id"
        const val COLUMN_STUDENT_ID = "student_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_COURSE = "course"
        const val COLUMN_ENROLLMENT_DATE = "enrollment_date"
        const val COLUMN_GPA = "gpa"
    }
    
    fun isValid(): Boolean {
        return studentId.isNotBlank() && 
               name.isNotBlank() && 
               email.isNotBlank() && 
               phone.isNotBlank() && 
               course.isNotBlank()
    }
    
    fun getFormattedGpa(): String {
        return String.format("%.2f", gpa)
    }
}
