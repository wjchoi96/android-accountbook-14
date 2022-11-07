package com.example.android_accountbook_14.presenter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_accountbook_14.domain.model.LedgerMonthChartModel
import com.example.android_accountbook_14.domain.usecase.FetchLedgerMonthChartDataUseCase
import com.example.android_accountbook_14.presenter.ui.fragment.DatePickBottomSheetDialogFragment
import com.example.android_accountbook_14.util.toCashString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val fetchLedgerMonthChartDataUseCase: FetchLedgerMonthChartDataUseCase
): ViewModel() {
    private val _dataLoading: MutableLiveData<Boolean> = MutableLiveData()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> = _errorMessage

    private val _title: MutableLiveData<String> = MutableLiveData()
    val title: LiveData<String> = _title

    private val _showDatePickDialog: MutableLiveData<Pair<DatePickBottomSheetDialogFragment, String>> = MutableLiveData()
    val showDatePickDialog: LiveData<Pair<DatePickBottomSheetDialogFragment, String>> = _showDatePickDialog

    private val _tableChartData: MutableLiveData<List<Pair<Float, LedgerMonthChartModel.LedgerChartItemModel>>> = MutableLiveData()
    val tableChartData: LiveData<List<Pair<Float, LedgerMonthChartModel.LedgerChartItemModel>>> = _tableChartData

    private val _monthSpend: MutableLiveData<String> = MutableLiveData()
    val monthSpend: LiveData<String> = _monthSpend

    private val _pieChartData: MutableLiveData<List<Pair<Float, String>>> = MutableLiveData()
    val pieChartData: LiveData<List<Pair<Float, String>>> = _pieChartData

    private val _emptyData: MutableLiveData<String> = MutableLiveData()
    val emptyData: LiveData<String> = _emptyData

    private var year = 0
    private var month = 0

    fun setDate(year: Int, month: Int){
        this.year = year
        this.month = month
        _title.value = "${year}년 ${month}월"
        fetchList()
    }

    fun fetchList(){
        if(year == 0 || month == 0) return
        Timber.d("fetch ledgers chart date [$year/$month]")
        _dataLoading.value = true
        viewModelScope.launch {
            fetchLedgerMonthChartDataUseCase.invoke(year, month)
                .onSuccess {
                    when{
                        it.ledgerChardItems.isEmpty() -> {
                            _emptyData.value = "${year}년 ${month}월 데이터가 없습니다"
                            _pieChartData.value = emptyList()
                            _tableChartData.value = emptyList()
                            _monthSpend.value = ""
                        }
                        else -> {
                            val list = mutableListOf<Pair<Float, String>>()
                            it.percents.indices.forEach { i ->
                                list.add(it.percents[i].first to it.colors[i])
                            }
                            Timber.d("fetch ledgers chart res ${list.size}")
                            _emptyData.value = ""
                            _tableChartData.value = it.percents.sortedBy { v -> -v.first }
                            _pieChartData.value = list.sortedBy { v -> -v.first }
                            _monthSpend.value = (it.spend).toCashString()
                        }
                    }
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