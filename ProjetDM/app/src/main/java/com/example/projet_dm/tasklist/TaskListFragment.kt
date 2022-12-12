package com.example.projet_dm.tasklist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.projet_dm.DetailActivity
import com.example.projet_dm.databinding.FragmentTaskListBinding
import java.util.*

class TaskListFragment : Fragment() {
    private var _binding : FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val adapterListener : TaskListListener = object : TaskListListener {
        override fun onClickDelete(task: Task) {
            taskList = taskList - task
            adapter.submitList(taskList)
        }
        override fun onClickEdit(task: Task) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("task", task)
            editTask.launch(intent)
        }

        override fun onLongClickCopy(task: Task): Boolean {
            val sendIntent = Intent.createChooser(Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, task.description)
                type = "text/plain"
                // (Optional) Here we're setting the title of the content
                putExtra(Intent.EXTRA_TITLE, task.title)
            }, null)
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
            return true
        }
    }

    private val adapter = TaskListAdapter(adapterListener)
    private var taskList = listOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    )

    private val createTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as Task? ?: return@registerForActivityResult
        taskList = taskList + task
        // dans cette callback on récupèrera la task et on l'ajoutera à la liste
        adapter.submitList(taskList)
    }

    private val editTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as Task? ?: return@registerForActivityResult
        taskList = taskList.map { if (it.id == task.id) task else it}
        adapter.submitList(taskList)
    }

    private val shareTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as Task? ?: return@registerForActivityResult
        taskList = taskList + task
        // dans cette callback on récupèrera la task et on l'ajoutera à la liste
        adapter.submitList(taskList)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var shareDescription = ""
        var sharing = false

        val intent = activity?.intent;
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                        shareDescription = it
                        sharing = true
                    }
                }
            }
        }

        if(sharing) {
            val detailIntent = Intent(context, DetailActivity::class.java)
            detailIntent.putExtra("task", Task(id = UUID.randomUUID().toString(), title="", description = shareDescription))
            shareTask.launch(detailIntent)
        }

        binding.plusButton.setOnClickListener {
            /*taskCount++
            val newTask = Task(id = UUID.randomUUID().toString(), title = "Task ${taskCount}")
            taskList = taskList + newTask
            adapter.submitList(taskList)*/

            val plusIntent = Intent(context, DetailActivity::class.java)

            //startActivity(intent)
            createTask.launch(plusIntent)
        }

        val recyclerView = binding.recyclerViewID
        recyclerView.adapter = adapter
        adapter.submitList(taskList)
    }
}