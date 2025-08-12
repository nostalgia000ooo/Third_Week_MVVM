package com.example.third_week_mvvm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.third_week_mvvm.Dao.TodoDao
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class TodoViewModel(private val todoDao: TodoDao) : ViewModel(){
    private val _isInputVisible = MutableLiveData(false)
    val isInputVisible: LiveData<Boolean> = _isInputVisible

    private val repository = TodoRepository(todoDao)

//    待办列表
    private val _todos = MutableLiveData<List<Todo>>()
    val todos: LiveData<List<Todo>> = _todos

//    加载状态
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        loadTodos()
    }

    fun toggleInputVisibility() {
        _isInputVisible.value = !(_isInputVisible.value ?: false)
    }

    fun showInputWithGesture() {
        if (_isInputVisible.value != true) {
            _isInputVisible.value = true
        }
    }

    fun hideInputWithGesture() {
        if (_isInputVisible.value == true) {
            _isInputVisible.value = false
        }
    }

    fun loadTodos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getAllTodos()
                // 排序：未完成的在前，已完成的在后，同状态按标题排序
                val sortedResult = result.sortedWith(compareBy<Todo> { it.isCompleted }.thenBy { it.title })
                
                // 防抖：只有数据真正变化时才更新
                if (_todos.value != sortedResult) {
                    _todos.postValue(sortedResult) // 使用postValue而非value
                }

            } catch (e: Exception) {
                Log.e("error","出现错误${e.message}")
                _errorMessage.value = "加载待办事项失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addTodo(title: String){
        if(title.isBlank()){
            Log.d("IsBLANK","添加的事项不能为空")
            _errorMessage.value = "添加的事项不能为空"
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
                _isInputVisible.value = false // 添加后隐藏输入框
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

    private var updateJob: Job? = null
    private val updateDebounceTime = 100L // 减少防抖时间到100ms
    private val updatingTodoIds = mutableSetOf<String>() // 跟踪正在更新的待办事项 ID
    
    fun updateTodosStatus(todo: Todo){
        // 如果该代办正在更新中，直接返回
        if (updatingTodoIds.contains(todo.id)) {
            Log.d("updateTodo", "Todo ${todo.id} is already updating, ignoring")
            return
        }
        
        // 取消之前的更新任务
        updateJob?.cancel()
        
        updateJob = viewModelScope.launch {
            updatingTodoIds.add(todo.id)
            
            try {
                // 防抖延迟
                delay(updateDebounceTime)
                
                _isLoading.value = true
                val updatedTodo = todo.copy(isCompleted = !todo.isCompleted)
                repository.updateTodo(updatedTodo)
                Log.d("updateTodo","updateTodo Successful${updatedTodo}")
                
                // 更新本地数据并重新排序
                val currentTodos = _todos.value?.toMutableList() ?: mutableListOf()
                val index = currentTodos.indexOfFirst { it.id == todo.id }
                if (index != -1) {
                    currentTodos[index] = updatedTodo
                    // 重新排序：未完成的在前，已完成的在后
                    val sortedTodos = currentTodos.sortedWith(compareBy<Todo> { it.isCompleted }.thenBy { it.title })
                    _todos.value = sortedTodos
                }
            }catch (e: Exception){
                _errorMessage.value = "更新失败：${e.message}"
                // 发生错误时重新加载以确保数据一致性
                loadTodos()
            }finally {
                _isLoading.value = false
                updatingTodoIds.remove(todo.id)
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