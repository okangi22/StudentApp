package com.ecospace.studentapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ecospace.studentapp.R
import com.ecospace.studentapp.model.Student

/**
 * RecyclerView Adapter for displaying student list
 * Using Kotlin with ListAdapter for efficient updates
 */
class StudentAdapter(
    private val onItemClick: (Student) -> Unit,
    private val onEditClick: (Student) -> Unit,
    private val onDeleteClick: (Student) -> Unit
) : ListAdapter<Student, StudentAdapter.StudentViewHolder>(StudentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvStudentName)
        private val tvStudentId: TextView = itemView.findViewById(R.id.tvStudentId)
        private val tvCourse: TextView = itemView.findViewById(R.id.tvCourse)
        private val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        private val tvInitials: TextView = itemView.findViewById(R.id.tvInitials)
        private val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(student: Student) {
            tvName.text = student.name
            tvStudentId.text = student.studentId
            tvCourse.text = student.course
            tvEmail.text = student.email
            
            // Generate initials from name
            val initials = student.name.split(" ")
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                .joinToString("")
            tvInitials.text = initials.ifEmpty { "?" }

            // Click listeners
            itemView.setOnClickListener { onItemClick(student) }
            btnEdit.setOnClickListener { onEditClick(student) }
            btnDelete.setOnClickListener { onDeleteClick(student) }
        }
    }

    class StudentDiffCallback : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem == newItem
        }
    }
}
