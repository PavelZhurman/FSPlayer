package com.github.pavelzhurman.core.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding, VM : ViewModel> : AppCompatActivity() {

    private lateinit var viewModel: VM
    protected lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding()

        viewModel = ViewModelProvider(this).get(viewModelClass)
        initObservers(viewModel)

        setContentView(binding.root)

        initViews()
    }

    abstract fun initObservers(viewModel: VM)
    abstract fun initViews()
    abstract fun getViewBinding(): VB

    abstract val viewModelClass: Class<VM>
}