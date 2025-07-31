package com.example.third_week_mvvm

import com.example.third_week_mvvm.Dao.TodoDao
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TodoViewModelTest {


    private lateinit var viewModel: TodoViewModel
    private val todoDao: TodoDao = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TodoViewModel(todoDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun toggleInputVisibility_flipsEachTime() {
        assertEquals(false, viewModel.isInputVisible.value)
        viewModel.toggleInputVisibility()
        assertEquals(true, viewModel.isInputVisible.value)
        viewModel.toggleInputVisibility()
        assertEquals(false, viewModel.isInputVisible.value)
    }

    @Test
    fun loadTodos_success_updateList() {
        val fakeList = listOf(Todo(1.toString(), "Test"))
        coEvery { todoDao.getAll() } returns fakeList

        viewModel.loadTodos()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(false, viewModel.isLoading.value)
        assertEquals(fakeList, viewModel.todos.value)
        assertEquals(null, viewModel.errorMessage.value)
    }

    @Test
    fun loadTodos_error_setsErrorMessage() = runTest {
        coEvery { todoDao.getAll() } throws RuntimeException("disk broken")

        viewModel.loadTodos()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(false, viewModel.isLoading.value)
        assertEquals("加载待办事项失败: disk broken", viewModel.errorMessage.value)
    }

    @Test
    fun addTodo_blankTitle_showsError() = runTest {
        viewModel.addTodo("   ")

        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("添加的事项不能为空", viewModel.errorMessage.value)
    }

    @Test
    fun addTodo_nonBlankTitle_inserts_andRefreshes() = runTest {
        val title = "做作业"
        coEvery { todoDao.insert(any()) } just Runs
        coEvery { todoDao.getAll() } returns emptyList() andThen listOf(Todo(0.toString(), title))

        viewModel.addTodo(title)

        coVerify { todoDao.insert(match { it.title == title }) }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(false, viewModel.isInputVisible.value)
        assertEquals(null, viewModel.errorMessage.value)
    }

    @Test
    fun clearErrorMessage_setsNull() = runTest {

        viewModel.addTodo("")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("添加事项不能为空", viewModel.errorMessage.value)

        viewModel.clearErrorMessage()
        assertEquals(null, viewModel.errorMessage.value)
    }
}