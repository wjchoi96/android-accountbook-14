package com.example.android_accountbook_14.presenter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_accountbook_14.domain.model.TextLabel
import com.example.android_accountbook_14.domain.usecase.InsertPaymentUseCase
import com.example.android_accountbook_14.domain.usecase.UpdatePaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentEditViewModel @Inject constructor(
    private val insertUseCase: InsertPaymentUseCase,
    private val updateUseCase: UpdatePaymentUseCase
) : ViewModel() {
    private val _dataLoading: MutableLiveData<Boolean> = MutableLiveData()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _viewState: MutableLiveData<PaymentEditViewState> = MutableLiveData()
    val viewState: LiveData<PaymentEditViewState> = _viewState

    private val _title: MutableLiveData<String> = MutableLiveData()
    val title: LiveData<String> = _title

    private val _btnEnable: MutableLiveData<Boolean> = MutableLiveData()
    val btnEnable: LiveData<Boolean> = _btnEnable

    private val _modeChanged: MutableLiveData<Mode> = MutableLiveData()
    val modeChanged: LiveData<Mode> = _modeChanged

    enum class Mode {
        Edit, NewItem
    }
    private var currentMode = Mode.NewItem
    private var editLabel: TextLabel? = null
    private var inputText: String? = null

    fun textChanged(text: String){
        inputText = text
        _btnEnable.value = !inputText.isNullOrBlank()
    }

    fun initData(label: TextLabel?){
        when(label){
            null -> {
                currentMode = Mode.NewItem
            }
            else -> {
                currentMode = Mode.Edit
                with(label) {
                    _title.value = title
                    inputText = title
                    editLabel = label
                }
            }
        }
        _modeChanged.value = currentMode
        _btnEnable.value = !inputText.isNullOrBlank()
    }

    fun btnClick(){
        when(currentMode){
            Mode.NewItem -> insertPayment()
            Mode.Edit -> updatePayment()
        }
    }
    private fun insertPayment(){
        viewModelScope.launch {
            _dataLoading.value = true
            insertUseCase(
                requireNotNull(inputText)
            ).onSuccess {
                when(it){
                    true -> _viewState.value = PaymentEditViewState.FinishEdit()
                    else -> _viewState.value = PaymentEditViewState.FailEdit(Throwable("중복된 데이터입니다"))
                }
            }.onFailure {
                it.printStackTrace()
                _viewState.value = PaymentEditViewState.FailEdit(it)
            }.also {
                _dataLoading.value = false
            }
        }
    }

    private fun updatePayment(){
        if(editLabel == null) return
        viewModelScope.launch {
            _dataLoading.value = true
            updateUseCase(
                editLabel!!.copy(
                    title = requireNotNull(inputText)
                )
            ).onSuccess {
                when(it){
                    true ->  _viewState.value = PaymentEditViewState.FinishEdit()
                    else ->  _viewState.value = PaymentEditViewState.FailEdit(Throwable("정보 업데이트를 실패하였습니다"))
                }
            }.onFailure {
                it.printStackTrace()
                _viewState.value = PaymentEditViewState.FailEdit(it)
            }.also {
                _dataLoading.value = false
            }
        }
    }

    sealed class PaymentEditViewState(
        val error: Throwable? = null
    ) {
        class FailEdit(error: Throwable): PaymentEditViewState(error)
        class FinishEdit: PaymentEditViewState()
    }
}