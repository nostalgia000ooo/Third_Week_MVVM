package com.example.third_week_mvvm

import android.util.Log
import androidx.appcompat.widget.DialogTitle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.third_week_mvvm.BR.todo
import com.example.third_week_mvvm.Dao.TodoDao
import kotlinx.coroutines.launch

class TodoViewModel(private val todoDao: TodoDao) : ViewModel(){
    private val repository = TodoRepository(todoDao)

//    待办列表
    private val _todos = MutableLiveData<List<Todo>>()
    val todos: LiveData<List<Todo>> = _todos

//    加载状态
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        loadTodos()
    }

    fun loadTodos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getAllTodos()
                _todos.value = result
            } catch (e: Exception) {
                _errorMessage.value = "加载待办事项失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addTodo(title: String){
        if(title.isBlank()){
            _errorMessage.value = "不能为空"
            return
        }

        viewModelScope.launch{
            _isLoading.value = true
            try {
                val newTodo = Todo(title = title)
                Log.d("DB", "Inserting todo: ${title}")
                todoDao.insert(newTodo)
                Log.d("DB", "Insert completed")
                repository.addTodo(newTodo)
//                添加后重新加载
                loadTodos()
            }catch (e: Exception){
                Log.d("DB","Insert failed")
                _errorMessage.value = "添加失败：${e.message}"
            }finally {
                _isLoading.value = false
            }
        }

    }

    fun updateTodosStatus(todo: Todo){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val updatedTodo = todo.copy(isCompleted = !todo.isCompleted)
                repository.updateTodo(updatedTodo)
                Log.d("updateTodo","updateTodo Successful${updatedTodo}")
//                之后重新加载
                loadTodos()
            }catch (e: Exception){
                _errorMessage.value = "更新失败：${e.message}"
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTodo(todo: Todo){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteTodo(todo)

                loadTodos()
            }catch (e: Exception){
                _errorMessage.value = "删除失败：${e.message}"
            }finally {
                _isLoading.value = false
            }
        }
    }

    fun clearErrorMessage(){
        _errorMessage.value = null
    }

}