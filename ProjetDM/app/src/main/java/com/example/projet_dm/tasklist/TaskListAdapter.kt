package com.example.projet_dm.tasklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.projet_dm.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

object MyItemsDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task) : Boolean {
        return (oldItem.id == newItem.id)
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task) : Boolean {
        return oldItem == newItem
    }
}

class TaskListAdapter(val listener: TaskListListener) : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(MyItemsDiffCallback) {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.task_title)
        private val description = itemView.findViewById<TextView>(R.id.task_description)
        private val deleteButton = itemView.findViewById<FloatingActionButton>(R.id.deleteButton)
        private val editButton = itemView.findViewById<FloatingActionButton>(R.id.editButton)

        fun bind(task: Task) {
            title.text = task.title;
            description.text = task.description;
            deleteButton.setOnClickListener { listener.onClickDelete(task) }
            editButton.setOnClickListener { listener.onClickEdit(task) }
            itemView.setOnLongClickListener { listener.onLongClickCopy(task) }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}