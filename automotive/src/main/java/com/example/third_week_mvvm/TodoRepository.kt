package com.example.third_week_mvvm

import com.example.third_week_mvvm.Dao.TodoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TodoRepository (private val todoDao: TodoDao) {

    suspend fun getAllTodos(): List<Todo> = withContext(Dispatchers.IO) {
        todoDao.getAll()
    }

    suspend fun addTodo(todo: Todo) = withContext(Dispatchers.IO) {
        todoDao.insert(todo)
    }

    suspend fun updateTodo(todo: Todo) = withContext(Dispatchers.IO) {
        todoDao.update(todo)
    }

    suspend fun deleteTodo(todo: Todo) = withContext(Dispatchers.IO) {
        todoDao.delete(todo)
    }
}