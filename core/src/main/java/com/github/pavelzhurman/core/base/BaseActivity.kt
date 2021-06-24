package com.github.pavelzhurman.core.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseActivity<VM : ViewModel> : AppCompatActivity() {

    private lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(viewModelClass)
        initObservers(viewModel)

        setContentView(layout)

        initViews()
    }

    abstract fun initObservers(viewModel: VM)
    abstract fun initViews()

    abstract val viewModelClass: Class<VM>
    abstract val layout: Int

}