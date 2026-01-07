package com.ecospace.studentapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecospace.studentapp.R;
import com.ecospace.studentapp.adapter.StudentAdapter;
import com.ecospace.studentapp.database.DatabaseHelper;
import com.ecospace.studentapp.model.Student;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import kotlin.Unit;

/**
 * Main Activity displaying list of students
 * Written in Java for activity management
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ADD_STUDENT = 1;
    private static final int REQUEST_EDIT_STUDENT = 2;

    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private DatabaseHelper dbHelper;
    private EditText etSearch;
    private LinearLayout tvEmptyState;  // CHANGED: TextView to LinearLayout
    private TextView tvStudentCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupRecyclerView();
        setupSearch();
        loadStudents();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        etSearch = findViewById(R.id.etSearch);
        tvEmptyState = findViewById(R.id.tvEmptyState);  // Now correctly casts to LinearLayout
        tvStudentCount = findViewById(R.id.tvStudentCount);

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditStudentActivity.class);
            startActivityForResult(intent, REQUEST_ADD_STUDENT);
        });

        dbHelper = DatabaseHelper.getInstance(this);
    }

    private void setupRecyclerView() {
        adapter = new StudentAdapter(
                student -> {
                    // On item click - view details
                    Intent intent = new Intent(MainActivity.this, StudentDetailActivity.class);
                    intent.putExtra("student_id", student.getId());
                    startActivity(intent);
                    return Unit.INSTANCE;
                },
                student -> {
                    // On edit click
                    Intent intent = new Intent(MainActivity.this, AddEditStudentActivity.class);
                    intent.putExtra("student_id", student.getId());
                    startActivityForResult(intent, REQUEST_EDIT_STUDENT);
                    return Unit.INSTANCE;
                },
                student -> {
                    // On delete click
                    showDeleteConfirmation(student);
                    return Unit.INSTANCE;
                }
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStudents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadStudents() {
        List<Student> students = dbHelper.getAllStudents();
        updateUI(students);
    }

    private void filterStudents(String query) {
        List<Student> students;
        if (query.isEmpty()) {
            students = dbHelper.getAllStudents();
        } else {
            students = dbHelper.searchStudents(query);
        }
        updateUI(students);
    }

    private void updateUI(List<Student> students) {
        adapter.submitList(students);

        if (students.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvEmptyState.setVisibility(View.GONE);
        }

        // Update student count
        int totalCount = dbHelper.getStudentCount();
        tvStudentCount.setText(totalCount + " Students");
    }

    private void showDeleteConfirmation(Student student) {
        new AlertDialog.Builder(this, R.style.GreenAlertDialog)
                .setTitle("Delete Student")
                .setMessage("Are you sure you want to delete " + student.getName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteStudent(student.getId());
                    loadStudents();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadStudents();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudents();
    }
}