package com.example.third_week_mvvm.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.third_week_mvvm.Todo
import com.example.third_week_mvvm.databinding.ItemTodoBinding

class TodoAdapter (
//    private var todos: List<Todo>,
    private val onTodoClick: (Todo) -> Unit,
    private val onDeleteClick: (Todo) -> Unit
) : ListAdapter<Todo, TodoAdapter.TodoViewHolder>(Diffallback()){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TodoViewHolder =
        TodoViewHolder(
            ItemTodoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    override fun onBindViewHolder(
        holder: TodoViewHolder,
        position: Int
    ) = holder.bind(getItem(position), onTodoClick, onDeleteClick)

    class TodoViewHolder(private val binding: ItemTodoBinding): RecyclerView.ViewHolder(binding.root) {
        private var currentIsCompleted = false
        fun bind(todo: Todo, onTodoClick: (Todo) -> Unit, onDeleteClick: (Todo) -> Unit) {
            if (currentIsCompleted != todo.isCompleted) {
                binding.todoCheckbox.isChecked = todo.isCompleted
                currentIsCompleted = todo.isCompleted
            }
            binding.todo = todo
            binding.onTodoClick = onTodoClick
            binding.onDeleteClick = onDeleteClick
            binding.executePendingBindings()

        }
    }

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
//        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context),parent,false)
//        return TodoViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
//        holder.bind(todos[position], onTodoClick, onDeleteClick)
//    }

    class Diffallback : DiffUtil.ItemCallback<Todo>(){
        override fun areItemsTheSame(
            oldItem: Todo,
            newItem: Todo
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Todo,
            newItem: Todo
        ): Boolean {
           return oldItem == newItem
        }

    }

//    override fun getItemCount() = todos.size

//    @SuppressLint("NotifyDataSetChanged")
//    fun updateData(newTodos: List<Todo>){
//        todos = newTodos
//        notifyDataSetChanged()
//    }

    fun updateData(newTodos: List<Todo>) = submitList(newTodos)

}



