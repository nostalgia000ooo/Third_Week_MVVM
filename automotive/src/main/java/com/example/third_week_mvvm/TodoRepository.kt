package com.example.third_week_mvvm

import com.example.third_week_mvvm.Dao.TodoDao

class TodoRepository (private val todoDao: TodoDao){
    private val todos = mutableListOf<Todo>()

    suspend fun getAllTodos(): List<Todo> = todoDao.getAll()

    suspend fun addTodo(todo: Todo) = todoDao.insert(todo)

    suspend fun updateTodo(todo: Todo) = todoDao.update(todo)

    suspend fun deleteTodo(todo: Todo) = todoDao.delete(todo)

}