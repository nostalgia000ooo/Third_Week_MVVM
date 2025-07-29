package com.example.third_week_mvvm

import com.example.third_week_mvvm.Dao.TodoDao
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


@OptIn(ExperimentalCoroutinesApi::class)
internal class TodoRepositoryTest {

    @MockK
    private lateinit var todoDao: TodoDao

    private lateinit var repository: TodoRepository

    @BeforeEach
    fun setUp(){
        MockKAnnotations.init(this)
        repository = TodoRepository(todoDao)
    }

    @Test
    fun `getAllTodos should return todos from dao`() = runTest {

        // Given
        val expected = listOf(Todo(id = 1.toString(), title = "TEst", isCompleted = false))
        coEvery { todoDao.getAll() } returns expected

        // When
        val actual = repository.getAllTodos()

        // Then
        assertEquals(expected, actual)
        coVerify(exactly = 1) { todoDao.getAll() }

    }

    @Test
    fun `addTodo should delegate to dao insert`() = runTest {
        // Given
        val todo = Todo(id = 2.toString(), title = "买牛奶", isCompleted = true)
        coEvery { todoDao.insert(todo) } returns Unit

        // When
        repository.addTodo(todo)

        // Then
        coVerify(exactly = 1) { todoDao.insert(todo) }
    }

    @Test
    fun `updateTodo should delegate to dao update`() = runTest {
        // Given
        val todo = Todo(id = 3.toString(), title = "更新任务", isCompleted = false)
        coEvery { todoDao.update(todo) } returns Unit

        // When
        repository.updateTodo(todo)

        // Then
        coVerify(exactly = 1) { todoDao.update(todo) }
    }

    @Test
    fun `deleteTodo should delegate to dao delete`() = runTest {
        // Given
        val todo = Todo(id = 4.toString(), title = "删除任务", isCompleted = true)
        coEvery { todoDao.delete(todo) } returns Unit

        // When
        repository.deleteTodo(todo)

        // Then
        coVerify(exactly = 1) { todoDao.delete(todo) }
    }

}