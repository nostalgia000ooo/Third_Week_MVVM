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
            binding.todo = todo
            binding.onTodoClick = onTodoClick
            binding.onDeleteClick = onDeleteClick
            binding.executePendingBindings()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TodoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(todos[position], onTodoClick, onDeleteClick)
    }

    override fun getItemCount() = todos.size

    fun updateData(newTodos: List<Todo>){
        todos = newTodos
        notifyDataSetChanged()
    }

}



