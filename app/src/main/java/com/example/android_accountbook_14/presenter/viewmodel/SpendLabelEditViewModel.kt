package com.example.android_accountbook_14.presenter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_accountbook_14.application.constant.LabelColorConstant
import com.example.android_accountbook_14.domain.model.ColorLabel
import com.example.android_accountbook_14.domain.usecase.InsertSpendLabelUseCase
import com.example.android_accountbook_14.domain.usecase.UpdateSpendLabelUseCase
import com.example.android_accountbook_14.util.ColorUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SpendLabelEditViewModel @Inject constructor(
    private val insertUseCase: InsertSpendLabelUseCase,
    private val updateUseCase: UpdateSpendLabelUseCase
): ViewModel() {
    private val _dataLoading: MutableLiveData<Boolean> = MutableLiveData()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _keyboardShown: MutableLiveData<Boolean> = MutableLiveData()
    val keyboardShown: LiveData<Boolean> = _keyboardShown

    private val _viewState: MutableLiveData<SpendLabelEditViewState> = MutableLiveData()
    val viewState: LiveData<SpendLabelEditViewState> = _viewState

    private val _title: MutableLiveData<String> = MutableLiveData()
    val title: LiveData<String> = _title

    private val _selectColorForState: MutableLiveData<Int> = MutableLiveData()
    val selectColorForState: LiveData<Int> = _selectColorForState

    private val _btnEnable: MutableLiveData<Boolean> = MutableLiveData()
    val btnEnable: LiveData<Boolean> = _btnEnable

    private val _modeChanged: MutableLiveData<Mode> = MutableLiveData()
    val modeChanged: LiveData<Mode> = _modeChanged

    enum class Mode {
        Edit, NewItem
    }
    val colors = LabelColorConstant.spendLabelColor.map{ColorUtil.convertToComposeColor(it)}
    private var currentMode = Mode.NewItem
    private var editLabel: ColorLabel? = null
    private var selectColor: String? = null
    private var inputText: String? = null

    val colorSelectEvent: (String)->(Unit) = {
        Timber.d("color select [$it]")
        _keyboardShown.value = false
        selectColor = it
        _btnEnable.value = selectColor != null && !inputText.isNullOrBlank()
    }

    fun textChanged(text: String){
        inputText = text
        _btnEnable.value = selectColor != null && !inputText.isNullOrBlank()
    }

    fun initData(label: ColorLabel?){
        when(label){
            null -> {
                currentMode = Mode.NewItem
                selectColor = ColorUtil.convertToHexString(colors.first())
                _selectColorForState.value = ColorUtil.convertToArgb(requireNotNull(selectColor))
            }
            else -> {
                currentMode = Mode.Edit
                with(label) {
                    _title.value = title
                    _selectColorForState.value = ColorUtil.convertToArgb(color)
                    inputText = title
                    selectColor = color
                    editLabel = label
                }
            }
        }
        _modeChanged.value = currentMode
        _btnEnable.value = selectColor != null && !inputText.isNullOrBlank()
    }

    fun btnClick(){
        when(currentMode){
            Mode.NewItem -> insertSpendLabel()
            Mode.Edit -> updateSpendLabel()
        }
    }
    private fun insertSpendLabel(){
        viewModelScope.launch {
            _dataLoading.value = true
            insertUseCase(
                requireNotNull(inputText),
                requireNotNull(selectColor)
            ).onSuccess {
                when(it){
                    true -> _viewState.value = SpendLabelEditViewState.FinishEdit()
                    else -> _viewState.value = SpendLabelEditViewState.FailEdit(Throwable("중복된 데이터입니다"))
                }
            }.onFailure {
                it.printStackTrace()
                _viewState.value = SpendLabelEditViewState.FailEdit(it)
            }.also {
                _dataLoading.value = false
            }
        }
    }

    private fun updateSpendLabel(){
        if(editLabel == null) return
        viewModelScope.launch {
            _dataLoading.value = true
            updateUseCase(
                editLabel!!.copy(
                    title = requireNotNull(inputText),
                    color = requireNotNull(selectColor)
                )
            ).onSuccess {
                when(it){
                    true ->  _viewState.value = SpendLabelEditViewState.FinishEdit()
                    else ->  _viewState.value = SpendLabelEditViewState.FailEdit(Throwable("정보 업데이트를 실패하였습니다"))
                }
            }.onFailure {
                it.printStackTrace()
                _viewState.value = SpendLabelEditViewState.FailEdit(it)
            }.also {
                _dataLoading.value = false
            }
        }
    }

    sealed class SpendLabelEditViewState(
        val error: Throwable? = null
    ) {
        class FailEdit(error: Throwable): SpendLabelEditViewState(error)
        class FinishEdit: SpendLabelEditViewState()
    }

}