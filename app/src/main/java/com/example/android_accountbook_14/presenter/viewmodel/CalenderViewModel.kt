package com.example.android_accountbook_14.presenter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_accountbook_14.domain.model.LedgerListItemModel
import com.example.android_accountbook_14.domain.usecase.FetchCalenderLedgerListUseCase
import com.example.android_accountbook_14.presenter.ui.fragment.DatePickBottomSheetDialogFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalenderViewModel @Inject constructor(
    private val fetchCalenderLedgerListUseCase: FetchCalenderLedgerListUseCase
) : ViewModel() {
    private val _dataLoading: MutableLiveData<Boolean> = MutableLiveData()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> = _errorMessage

    private val _title: MutableLiveData<String> = MutableLiveData()
    val title: LiveData<String> = _title

    private val _ledgerListItems: MutableLiveData<List<LedgerListItemModel>> = MutableLiveData()
    val ledgerListItems: LiveData<List<LedgerListItemModel>> = _ledgerListItems

    private val _showMonth: MutableLiveData<Int> = MutableLiveData()
    val showMonth: LiveData<Int> = _showMonth

    private val _showDatePickDialog: MutableLiveData<Pair<DatePickBottomSheetDialogFragment, String>> = MutableLiveData()
    val showDatePickDialog: LiveData<Pair<DatePickBottomSheetDialogFragment, String>> = _showDatePickDialog

    var year = 0
        private set
    var month = 0
        private set

    fun setDate(year: Int, month: Int){
        this.year = year
        this.month = month
        _showMonth.value = month
        _title.value = "${year}년 ${month}월"
        fetchList()
    }

    fun fetchList(){
        if(year == 0 || month == 0) return
        Timber.d("fetch ledgers date [$year/$month]")
        _dataLoading.value = true
        viewModelScope.launch {
            fetchCalenderLedgerListUseCase(year, month)
                .onSuccess {
                    Timber.d("fetch calender size[${it.size}], itemsSize[${it.sumOf { i -> i.dayLedgers.size }}]")
                    _ledgerListItems.value = it
                }
                .onFailure {
                    it.printStackTrace()
                    _errorMessage.value = it.message
                }.also {
                    _dataLoading.value = false
                }
        }
    }

    fun clickTitle(){
        _showDatePickDialog.value = DatePickBottomSheetDialogFragment.get(
            Calendar.getInstance().get(Calendar.YEAR),
            year,
            month
        ).apply {
            setDateChangedListener { y, m ->
                year = y
                month = m
                _title.value = "${year}년 ${month}월"
            }
        }to "date_pick_dialog"
    }

}