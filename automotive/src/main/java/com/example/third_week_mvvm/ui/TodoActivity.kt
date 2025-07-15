package com.example.third_week_mvvm.ui

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.third_week_mvvm.DataBase.AppDatabase
import com.example.third_week_mvvm.Todo
import com.example.third_week_mvvm.TodoViewModel
import com.example.third_week_mvvm.adapter.TodoAdapter
import com.example.third_week_mvvm.databinding.ActivityTodoBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay

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
        val database = AppDatabase.Companion.getInstance(this)
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
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.addButton)//upon the addButton
                    .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                    .show()
//                val toast = Toast.makeText(this,it, Toast.LENGTH_SHORT)
//                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 100)
//                toast.show()
                viewModel.clearErrorMessage()
            }
        }

        binding.todoRecyclerView.post {
            viewModel.loadTodos()
        }

    }

    private fun onTodoClick(todo: Todo){
        viewModel.updateTodosStatus(todo)
    }

    private fun onDeleteClick(todo: Todo){
        AlertDialog.Builder(this)
            .setTitle("删除")
            .setMessage("确定要删除${todo.title}吗")
            .setPositiveButton("确定") {_,_ ->
                viewModel.deleteTodo(todo)
            }
            .setNegativeButton("取消", null)
            .show()
    }
}