package com.example.projet_dm.tasklist

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.projet_dm.DetailActivity
import com.example.projet_dm.data.Api
import com.example.projet_dm.databinding.FragmentTaskListBinding
import kotlinx.coroutines.launch
import java.util.*

class TaskListFragment : Fragment() {
    private var _binding : FragmentTaskListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TasksListViewModel by viewModels()

    private val adapterListener : TaskListListener = object : TaskListListener {
        override fun onClickDelete(task: Task) {
            viewModel.remove(task)
            adapter.submitList(viewModel.tasksStateFlow.value)
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

    private val createTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as Task? ?: return@registerForActivityResult
        viewModel.add(task)
        adapter.submitList(viewModel.tasksStateFlow.value)
    }

    private val editTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as Task? ?: return@registerForActivityResult
        viewModel.edit(task)
        adapter.submitList(viewModel.tasksStateFlow.value)
    }

    private val shareTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as Task? ?: return@registerForActivityResult
        viewModel.add(task)
        adapter.submitList(viewModel.tasksStateFlow.value)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(layoutInflater, container, false)
        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            viewModel.tasksStateFlow.collect { newList : List<Task> ->
                viewModel.refresh()
                adapter.submitList(viewModel.tasksStateFlow.value)
            }
        }
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

        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
                    viewModel.tasksStateFlow.collect { newList : List<Task> ->
                        viewModel.refresh()
                        adapter.submitList(viewModel.tasksStateFlow.value)
                    }
                }
                mainHandler.postDelayed(this, 1000)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val user = Api.userWebService.fetchUser().body()!!
            binding.taskUsername.text = user.name
        }
        viewModel.refresh() // on demande de rafraîchir les données sans attendre le retour directement
    }
}