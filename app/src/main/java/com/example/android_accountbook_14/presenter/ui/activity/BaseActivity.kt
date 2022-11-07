package com.example.android_accountbook_14.presenter.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.android_accountbook_14.presenter.LogLifecycleObserver

abstract class BaseActivity<T: ViewDataBinding>: AppCompatActivity() {
    protected val TAG: String = this::class.java.simpleName

    protected lateinit var binding: T
    abstract val layoutResId: Int

    private val logLifecycleObserver = LogLifecycleObserver(TAG)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(logLifecycleObserver)
        binding = DataBindingUtil.setContentView(this, layoutResId)
        binding.lifecycleOwner = this
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(logLifecycleObserver)
    }
}