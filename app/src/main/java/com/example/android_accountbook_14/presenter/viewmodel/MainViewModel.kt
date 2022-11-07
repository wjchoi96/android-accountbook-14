package com.example.android_accountbook_14.presenter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class MainViewModel: ViewModel() {

    private val _viewState: MutableLiveData<MainViewState> = MutableLiveData()
    val viewState: LiveData<MainViewState> = _viewState

    private val _yearMonthData: MutableLiveData<Pair<Int, Int>> = MutableLiveData<Pair<Int, Int>>().apply {
        Calendar.getInstance().let {
            this.value = it.get(Calendar.YEAR) to it.get(Calendar.MONTH) + 1
        }
    }
    val yearMonthData: LiveData<Pair<Int, Int>> = _yearMonthData

    private var selectTabIndex = 0
    fun initView(){
        _viewState.value = MainViewState.TabSelect(selectTabIndex)
    }

    fun selectMenu(idx: Int){
        selectTabIndex = idx
        when(idx){
            0 -> _viewState.value = MainViewState.LedgerListViewShow
            1 -> _viewState.value = MainViewState.CalenderViewShow
            2 -> _viewState.value = MainViewState.StatsViewShow
            3 -> _viewState.value = MainViewState.SettingViewShow
        }
    }

    fun setData(year: Int, month: Int){
        _yearMonthData.value = year to month
    }

    fun minusMonth(){
        yearMonthData.value?.let {
            var year = it.first
            var month = it.second
            if(month == 1){
                month = 12
                year--
            }else{
                month--
            }
            _yearMonthData.value = year to month
        }
    }

    fun plusMonth(){
        yearMonthData.value?.let {
            var year = it.first
            var month = it.second
            if(month == 12){
                month = 1
                year++
            }else{
                month++
            }
            _yearMonthData.value = year to month
        }
    }


    sealed class MainViewState(
        val index: Int? = null
    ){
        class TabSelect(index: Int) : MainViewState(index)
        object LedgerListViewShow : MainViewState()
        object CalenderViewShow : MainViewState()
        object StatsViewShow : MainViewState()
        object SettingViewShow : MainViewState()
    }
}