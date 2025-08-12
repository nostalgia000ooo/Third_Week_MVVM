package com.example.third_week_mvvm.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import kotlin.math.abs

class TodoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodoBinding
    private lateinit var viewModel: TodoViewModel
    private lateinit var gestureDetector: GestureDetector
    private lateinit var vibrator: Vibrator
    private var adapter = TodoAdapter(
//        emptyList(),
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

        binding.inputContainer.visibility = View.GONE

        // 初始化振动器
        vibrator = ContextCompat.getSystemService(this, Vibrator::class.java)!!
        
        // 初始化手势检测器
        setupGestureDetector()

        binding.showInputButton.setOnClickListener {
            viewModel.toggleInputVisibility()
        }

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

        viewModel.isInputVisible.observe(this) { isVisible ->
            animateInputContainer(isVisible)
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

    private fun setupGestureDetector() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 80  // 降低阈值，使手势更敏感
            private val SWIPE_VELOCITY_THRESHOLD = 80

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null) return false
                
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                
                // 确保是垂直滑动而不是水平滑动
                if (abs(diffY) > abs(diffX)) {
                    if (abs(diffY) > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        // 添加触觉反馈
                        performHapticFeedback()
                        
                        if (diffY < 0) {
                            // 向上滑动 - 显示添加组件
                            viewModel.hideInputWithGesture()
                        } else {
                            // 向下滑动 - 隐藏添加组件
                            viewModel.showInputWithGesture()
                        }
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun performHapticFeedback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    // 为RecyclerView也添加手势支持
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun animateInputContainer(isVisible: Boolean) {
        if (isVisible) {
            // 显示动画 - 从上方滑入并淡入
            binding.inputContainer.visibility = View.VISIBLE
            binding.inputContainer.alpha = 0f
            binding.inputContainer.translationY = -100f
            binding.inputContainer.scaleX = 0.9f
            binding.inputContainer.scaleY = 0.9f
            
            // 淡入动画
            ObjectAnimator.ofFloat(binding.inputContainer, "alpha", 0f, 1f).apply {
                duration = 350
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
            
            // 滑入动画
            ObjectAnimator.ofFloat(binding.inputContainer, "translationY", -100f, 0f).apply {
                duration = 350
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
            
            // 缩放动画
            ObjectAnimator.ofFloat(binding.inputContainer, "scaleX", 0.9f, 1f).apply {
                duration = 350
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
            
            ObjectAnimator.ofFloat(binding.inputContainer, "scaleY", 0.9f, 1f).apply {
                duration = 350
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        } else {
            // 隐藏动画 - 向上滑出并淡出
            ObjectAnimator.ofFloat(binding.inputContainer, "alpha", 1f, 0f).apply {
                duration = 250
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
            
            ObjectAnimator.ofFloat(binding.inputContainer, "translationY", 0f, -100f).apply {
                duration = 250
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
            
            ObjectAnimator.ofFloat(binding.inputContainer, "scaleX", 1f, 0.9f).apply {
                duration = 250
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
            
            ObjectAnimator.ofFloat(binding.inputContainer, "scaleY", 1f, 0.9f).apply {
                duration = 250
                interpolator = AccelerateDecelerateInterpolator()
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        binding.inputContainer.visibility = View.GONE
                    }
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
                start()
            }
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