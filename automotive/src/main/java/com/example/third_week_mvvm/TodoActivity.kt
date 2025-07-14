package com.example.third_week_mvvm

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.third_week_mvvm.DataBase.AppDatabase
import com.example.third_week_mvvm.adapter.TodoAdapter
import com.example.third_week_mvvm.databinding.ActivityTodoBinding


class TodoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodoBinding
    private lateinit var viewModel: TodoViewModel
    private var adapter = TodoAdapter(
        emptyList(),
        ::onTodoClick,
        ::onDeleteClick,
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 获取数据库实例
        val database = AppDatabase.getInstance(this)
        val todoDao = database.todoDao()


        // init viewModel
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TodoViewModel(todoDao) as T
            }
        }).get(TodoViewModel::class.java)

        // init RecyclerView
        binding.todoRecyclerView.adapter = adapter
        binding.todoRecyclerView.layoutManager = LinearLayoutManager(this)

        // set AddButton
        binding.addButton.setOnClickListener {
            val title = binding.todoEditText.text.toString().trim()
            viewModel.addTodo(title)
            binding.todoEditText.text.clear()
        }

        // observe data change in ViewModel
        viewModel.todos.observe(this) { todos ->
            adapter.updateData(todos)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this,it, Toast.LENGTH_SHORT).show()
                viewModel.clearErrorMessage()
            }
        }
    }

    private fun onTodoClick(todo: Todo){
        viewModel.updateTodosStatus(todo)
    }

    private fun onDeleteClick(todo: Todo){
        viewModel.deleteTodo(todo)
    }
}