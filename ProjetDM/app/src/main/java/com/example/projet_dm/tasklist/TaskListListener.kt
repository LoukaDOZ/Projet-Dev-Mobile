package com.example.projet_dm.tasklist

interface TaskListListener {
    fun onClickDelete(task: Task)
    fun onClickEdit(task: Task)
    fun onLongClickCopy(task: Task): Boolean
}