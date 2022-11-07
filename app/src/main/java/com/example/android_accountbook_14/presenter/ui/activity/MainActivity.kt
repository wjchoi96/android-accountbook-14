package com.example.android_accountbook_14.presenter.ui.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.size
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.android_accountbook_14.R
import com.example.android_accountbook_14.databinding.ActivityMainBinding
import com.example.android_accountbook_14.presenter.ui.fragment.CalenderFragment
import com.example.android_accountbook_14.presenter.ui.fragment.LedgerListFragment
import com.example.android_accountbook_14.presenter.ui.fragment.SettingFragment
import com.example.android_accountbook_14.presenter.ui.fragment.ChartFragment
import com.example.android_accountbook_14.presenter.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity: BaseActivity<ActivityMainBinding>() {
    override val layoutResId: Int
        get() = R.layout.activity_main

    private val viewModel: MainViewModel by viewModels()

    override fun onBackPressed() {
        Timber.d("onBackPressed[$${supportFragmentManager.backStackEntryCount}]")
        if(supportFragmentManager.backStackEntryCount != 0){
            supportFragmentManager.popBackStack()
        }else {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        setListener()
        observeData()
        viewModel.initView()
    }

    private fun setListener(){
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.menu_main_ledger_list -> viewModel.selectMenu(0)
                R.id.menu_main_calender -> viewModel.selectMenu(1)
                R.id.menu_main_chart -> viewModel.selectMenu(2)
                R.id.menu_main_setting -> viewModel.selectMenu(3)
            }
            true
        }
    }

    private fun setSelectTab(idx: Int){
        if(!(idx in 0 until binding.bottomNavigationView.menu.size))
            return
        binding.bottomNavigationView.menu.getItem(idx).isChecked = true
        viewModel.selectMenu(idx)
    }

    private fun observeData(){
        viewModel.viewState.observe(this){
            when(it){
                is MainViewModel.MainViewState.TabSelect -> {
                    it.index?.let { idx ->
                        setSelectTab(idx )
                    }
                }
                is MainViewModel.MainViewState.LedgerListViewShow -> {
                    supportFragmentManager.apply {
                        popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        commit {
                            replace<LedgerListFragment>(R.id.container_fragment)
                        }
                    }
                }
                is MainViewModel.MainViewState.CalenderViewShow -> {
                    supportFragmentManager.apply {
                        popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        commit {
                            replace<CalenderFragment>(R.id.container_fragment)
                        }
                    }
                }
                is MainViewModel.MainViewState.StatsViewShow -> {
                    supportFragmentManager.apply {
                        popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        commit {
                            replace<ChartFragment>(R.id.container_fragment)
                        }
                    }
                }
                is MainViewModel.MainViewState.SettingViewShow -> {
                    supportFragmentManager.apply {
                        popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        commit {
                            replace<SettingFragment>(R.id.container_fragment)
                        }
                    }
                }
            }
        }
    }


}