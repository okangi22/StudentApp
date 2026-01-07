package com.ecospace.studentapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ecospace.studentapp.model.Student;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite Database Helper class for Student CRUD operations
 * Written in Java for robust database handling
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    
    // Database Info
    private static final String DATABASE_NAME = "StudentManager.db";
    private static final int DATABASE_VERSION = 1;

    // Table Create Statement
    private static final String CREATE_TABLE_STUDENTS = 
        "CREATE TABLE " + Student.TABLE_NAME + " (" +
        Student.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        Student.COLUMN_STUDENT_ID + " TEXT UNIQUE NOT NULL, " +
        Student.COLUMN_NAME + " TEXT NOT NULL, " +
        Student.COLUMN_EMAIL + " TEXT NOT NULL, " +
        Student.COLUMN_PHONE + " TEXT NOT NULL, " +
        Student.COLUMN_COURSE + " TEXT NOT NULL, " +
        Student.COLUMN_ENROLLMENT_DATE + " TEXT, " +
        Student.COLUMN_GPA + " REAL DEFAULT 0.0" +
        ")";

    // Singleton instance
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database table");
        db.execSQL(CREATE_TABLE_STUDENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + Student.TABLE_NAME);
        onCreate(db);
    }

    // ==================== CRUD Operations ====================

    /**
     * Insert a new student record
     */
    public long insertStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(Student.COLUMN_STUDENT_ID, student.getStudentId());
        values.put(Student.COLUMN_NAME, student.getName());
        values.put(Student.COLUMN_EMAIL, student.getEmail());
        values.put(Student.COLUMN_PHONE, student.getPhone());
        values.put(Student.COLUMN_COURSE, student.getCourse());
        values.put(Student.COLUMN_ENROLLMENT_DATE, student.getEnrollmentDate());
        values.put(Student.COLUMN_GPA, student.getGpa());

        long id = db.insert(Student.TABLE_NAME, null, values);
        Log.d(TAG, "Inserted student with ID: " + id);
        return id;
    }

    /**
     * Get a single student by ID
     */
    public Student getStudent(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selectQuery = "SELECT * FROM " + Student.TABLE_NAME + 
                           " WHERE " + Student.COLUMN_ID + " = ?";
        
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
        
        Student student = null;
        if (cursor.moveToFirst()) {
            student = cursorToStudent(cursor);
        }
        cursor.close();
        return student;
    }

    /**
     * Get all students
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + Student.TABLE_NAME + 
                           " ORDER BY " + Student.COLUMN_NAME + " ASC";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                students.add(cursorToStudent(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        
        Log.d(TAG, "Retrieved " + students.size() + " students");
        return students;
    }

    /**
     * Search students by name or student ID
     */
    public List<Student> searchStudents(String query) {
        List<Student> students = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + Student.TABLE_NAME + 
                           " WHERE " + Student.COLUMN_NAME + " LIKE ? OR " +
                           Student.COLUMN_STUDENT_ID + " LIKE ? OR " +
                           Student.COLUMN_COURSE + " LIKE ?" +
                           " ORDER BY " + Student.COLUMN_NAME + " ASC";
        
        String searchPattern = "%" + query + "%";
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, 
            new String[]{searchPattern, searchPattern, searchPattern});
        
        if (cursor.moveToFirst()) {
            do {
                students.add(cursorToStudent(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        
        return students;
    }

    /**
     * Update a student record
     */
    public int updateStudent(Student student) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(Student.COLUMN_STUDENT_ID, student.getStudentId());
        values.put(Student.COLUMN_NAME, student.getName());
        values.put(Student.COLUMN_EMAIL, student.getEmail());
        values.put(Student.COLUMN_PHONE, student.getPhone());
        values.put(Student.COLUMN_COURSE, student.getCourse());
        values.put(Student.COLUMN_ENROLLMENT_DATE, student.getEnrollmentDate());
        values.put(Student.COLUMN_GPA, student.getGpa());

        int rowsAffected = db.update(Student.TABLE_NAME, values, 
            Student.COLUMN_ID + " = ?", 
            new String[]{String.valueOf(student.getId())});
        
        Log.d(TAG, "Updated " + rowsAffected + " rows");
        return rowsAffected;
    }

    /**
     * Delete a student record
     */
    public void deleteStudent(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Student.TABLE_NAME, 
            Student.COLUMN_ID + " = ?", 
            new String[]{String.valueOf(id)});
        Log.d(TAG, "Deleted student with ID: " + id);
    }

    /**
     * Get total student count
     */
    public int getStudentCount() {
        String countQuery = "SELECT COUNT(*) FROM " + Student.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * Check if student ID already exists
     */
    public boolean isStudentIdExists(String studentId, long excludeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + Student.TABLE_NAME + 
                      " WHERE " + Student.COLUMN_STUDENT_ID + " = ? AND " +
                      Student.COLUMN_ID + " != ?";
        
        Cursor cursor = db.rawQuery(query, 
            new String[]{studentId, String.valueOf(excludeId)});
        
        boolean exists = false;
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0;
        }
        cursor.close();
        return exists;
    }

    /**
     * Helper method to convert cursor to Student object
     */
    private Student cursorToStudent(Cursor cursor) {
        return new Student(
            cursor.getLong(cursor.getColumnIndexOrThrow(Student.COLUMN_ID)),
            cursor.getString(cursor.getColumnIndexOrThrow(Student.COLUMN_STUDENT_ID)),
            cursor.getString(cursor.getColumnIndexOrThrow(Student.COLUMN_NAME)),
            cursor.getString(cursor.getColumnIndexOrThrow(Student.COLUMN_EMAIL)),
            cursor.getString(cursor.getColumnIndexOrThrow(Student.COLUMN_PHONE)),
            cursor.getString(cursor.getColumnIndexOrThrow(Student.COLUMN_COURSE)),
            cursor.getString(cursor.getColumnIndexOrThrow(Student.COLUMN_ENROLLMENT_DATE)),
            cursor.getDouble(cursor.getColumnIndexOrThrow(Student.COLUMN_GPA))
        );
    }
}
