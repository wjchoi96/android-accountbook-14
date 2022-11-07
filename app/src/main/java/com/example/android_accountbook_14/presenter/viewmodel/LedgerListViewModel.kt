package com.example.android_accountbook_14.presenter.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_accountbook_14.domain.model.LedgerListItemModel
import com.example.android_accountbook_14.domain.model.LedgerModel
import com.example.android_accountbook_14.domain.usecase.FetchLedgerListItemsUseCase
import com.example.android_accountbook_14.domain.usecase.FilterLedgersUseCase
import com.example.android_accountbook_14.domain.usecase.RemoveLedgesUseCase
import com.example.android_accountbook_14.presenter.ui.fragment.DatePickBottomSheetDialogFragment
import com.example.android_accountbook_14.util.toCashString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LedgerListViewModel @Inject constructor(
    private val fetchLedgerListItemsUseCase: FetchLedgerListItemsUseCase,
    private val removeLedgersUseCase: RemoveLedgesUseCase,
    private val filterLedgersUseCase: FilterLedgersUseCase
): ViewModel() {
    private val _dataLoading: MutableLiveData<Boolean> = MutableLiveData()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> = _errorMessage

    private val _title: MutableLiveData<String> = MutableLiveData()
    val title: LiveData<String> = _title

    private val _ledgerListItems: MutableLiveData<List<LedgerListItemModel>> = MutableLiveData()
    val ledgerListItems: LiveData<List<LedgerListItemModel>> = _ledgerListItems

    private val _navigateToRegisterView: MutableLiveData<Int> = MutableLiveData()
    val navigateToRegisterView: LiveData<Int> = _navigateToRegisterView

    private val _navigateToEditView: MutableLiveData<LedgerModel> = MutableLiveData()
    val navigateToEditView: LiveData<LedgerModel> = _navigateToEditView

    private val _selectMode: MutableLiveData<Boolean> = MutableLiveData()
    val selectMode: LiveData<Boolean> = _selectMode

    private val _showDatePickDialog: MutableLiveData<Pair<DatePickBottomSheetDialogFragment, String>> = MutableLiveData()
    val showDatePickDialog: LiveData<Pair<DatePickBottomSheetDialogFragment, String>> = _showDatePickDialog

    private val _totalIncome: MutableLiveData<String> = MutableLiveData()
    val totalIncome: LiveData<String> = _totalIncome

    private val _totalSpend: MutableLiveData<String> = MutableLiveData()
    val totalSpend: LiveData<String> = _totalSpend

    private var year = 0
    private var month = 0
    var leftCheckedState: Boolean = true
        private set
    var rightCheckedState: Boolean = false
        private set
    private var rawLedgers = listOf<LedgerListItemModel>()


    val selectLedgers = mutableStateListOf<LedgerModel>()
    private var selectItemCount = 0
        set(value) {
            Timber.d("checkModeItemCount setter[$field -> $value]")
            if(field == 0 && value > 0){
                _selectMode.value = true
            }
            field = if(value<0) 0 else value
            if(value == 0){
                _selectMode.value = false
                selectLedgers.clear()
                _title.value = "${year}년 ${month}월"
            }else{
                _title.value = "${value}개 선택"
            }
        }
        get() = selectLedgers.size

    private fun setSelectLedger(idxInfo: Pair<Int, Int>, id: Int){
        ledgerListItems.value?.let {
            if (selectLedgers.find { i -> i.id == id } == null) {
                selectLedgers.add(it[idxInfo.first].dayLedgers[idxInfo.second])
                Timber.d("select set [$idxInfo], $id => set select")
            }else{
                selectLedgers.removeIf{ i -> i.id == id }
                Timber.d("select set [$idxInfo], $id => remove")
            }
            selectItemCount = selectLedgers.size
        }
    }

    val itemClickListener: (Pair<Int, Int>, Int) -> (Unit) = { idxInfo, id ->
        viewModelScope.launch {
            Timber.d("itemClickListener[$id]")
            if(_selectMode.value == true){
                setSelectLedger(idxInfo, id)
                return@launch
            }
            delay(200) // click effect 가 보여지기 전에 view 이동하는 문제때문
            val editItem: LedgerModel? = rawLedgers.findUseId(id)
            if(editItem != null)
                _navigateToEditView.value = editItem
            else {
                _errorMessage.value = "can not find edit item"
            }
        }
    }

    val itemLongClickListener: (Pair<Int, Int>, Int) -> (Unit) = { idxInfo, id ->
        Timber.d("itemLongClickListener[$idxInfo], $id")
        if (_selectMode.value != true)
            _selectMode.value = true
        setSelectLedger(idxInfo, id)
    }

    fun setDate(year: Int, month: Int){
        this.year = year
        this.month = month
        _title.value = "${year}년 ${month}월"
        fetchList()
    }

    fun clickTitle(){
        if(_selectMode.value != true){
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


    fun fetchList(){
        if(year == 0 || month == 0) return
        Timber.d("fetch ledgers date [$year/$month]")
        _dataLoading.value = true
        viewModelScope.launch {
            fetchLedgerListItemsUseCase(year, month)
                .onSuccess {
                    Timber.d("fetch ledgers[${it.sumOf { it.dayLedgers.size }}]")
                    rawLedgers = it.toMutableList()
                    _totalIncome.value = rawLedgers.sumOf { i -> i.income }.toCashString()
                    _totalSpend.value = rawLedgers.sumOf { i -> i.spend }.toCashString()
                    filterList(leftCheckedState, rightCheckedState)
                }
                .onFailure {
                    it.printStackTrace()
                    _errorMessage.value = it.message
                }.also {
                    _dataLoading.value = false
                }
        }
    }

    fun clickRemoveButton(){
        Timber.d("remove[${selectLedgers.size}]")
        selectLedgers.forEach {
            Timber.d("remove item $it")
        }
        viewModelScope.launch {
            _dataLoading.value = true
            removeLedgersUseCase(selectLedgers.map { it.id })
                .onSuccess {
                    Timber.d("remove item $it")
                    when(it){
                        true -> {
                            finishSelectMode()
                            fetchList()
                        }
                        else -> _errorMessage.value = "삭제 실패"
                    }
                }.onFailure {
                    Timber.d("remove item failure")
                    it.printStackTrace()
                    _errorMessage.value = "삭제 오류"
                }.also {
                    _dataLoading.value = false
                }
        }
    }
    private fun finishSelectMode(){
        selectItemCount = 0 // 연결된 setter 로 인해 select item list 가 clear 된다
    }

    fun clickRegisterButton(){
        _navigateToRegisterView.value = when {
            leftCheckedState -> 1
            rightCheckedState -> -1
            else -> 1
        }
    }

    fun clickFilterButton(leftSelect: Boolean, rightSelect: Boolean){
        Timber.d("clickFilterButton => leftSelect[$leftSelect], rightSelect[$rightSelect]")
        leftCheckedState = leftSelect
        rightCheckedState = rightSelect
        filterList(leftCheckedState, rightCheckedState)
    }

    private fun filterList(containIncome: Boolean, containSpend: Boolean){
        _dataLoading.value = true
        viewModelScope.launch {
            filterLedgersUseCase(rawLedgers, containIncome, containSpend)
                .onSuccess {
//                    ledgerListItems.clear()
//                    ledgerListItems.addAll(it)
                    _ledgerListItems.value = it
                }.onFailure {
                    _errorMessage.value = "리스트 필터링 오류 발생"
                }.also {
                    _dataLoading.value = false
                }
        }
    }

    private fun List<LedgerListItemModel>.findUseId(id: Int): LedgerModel?{
        var item: LedgerModel? = null
        for(i in rawLedgers.indices){
            Timber.d("loop raw data => ${rawLedgers[i]}")
            item = rawLedgers[i].dayLedgers.find { it.id == id }
            if(item != null) break
        }
        return item
    }
}