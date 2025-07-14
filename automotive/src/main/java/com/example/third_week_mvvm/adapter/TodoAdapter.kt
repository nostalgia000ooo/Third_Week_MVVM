package com.example.third_week_mvvm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.third_week_mvvm.Todo
import com.example.third_week_mvvm.databinding.ItemTodoBinding

class TodoAdapter (
    private var todos: List<Todo>,
    private val onTodoClick: (Todo) -> Unit,
    private val onDeleteClick: (Todo) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>(){

    class TodoViewHolder(private val binding: ItemTodoBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(todo: Todo, onTodoClick: (Todo) -> Unit, onDeleteClick: (Todo) -> Unit) {
            binding.todoTitle.text = todo.title
            binding.todoCheckbox.isChecked = todo.isCompleted

            // 设置整个item的点击事件
            binding.root.setOnClickListener { onTodoClick(todo) }

            // 设置删除按钮点击事件
            binding.deleteButton.setOnClickListener { onDeleteClick(todo) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoAdapter.TodoViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoAdapter.TodoViewHolder, position: Int) {
        holder.bind(todos[position], onTodoClick, onDeleteClick)
    }

    override fun getItemCount() = todos.size

    fun updateData(newTodos: List<Todo>){
        todos = newTodos
        notifyDataSetChanged()
    }

}



