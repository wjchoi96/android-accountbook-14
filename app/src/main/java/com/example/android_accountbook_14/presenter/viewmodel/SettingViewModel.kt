package com.example.android_accountbook_14.presenter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_accountbook_14.data.repsoitory.LabelRepositoryImpl
import com.example.android_accountbook_14.domain.model.ColorLabel
import com.example.android_accountbook_14.domain.model.TextLabel
import com.example.android_accountbook_14.domain.repository.LabelRepository
import com.example.android_accountbook_14.domain.usecase.FetchLabelBundleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val fetchUserCase: FetchLabelBundleUseCase
) : ViewModel() {
    private val _dataLoading: MutableLiveData<Boolean> = MutableLiveData()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> = _errorMessage

    private val _textLabels: MutableLiveData<Map<String, List<TextLabel>>> = MutableLiveData()
    val textLabels: LiveData<Map<String, List<TextLabel>>> = _textLabels

    private val _colorLabels: MutableLiveData<Map<String, List<ColorLabel>>> = MutableLiveData()
    val colorLabels: LiveData<Map<String, List<ColorLabel>>> = _colorLabels

    private val _viewState: MutableLiveData<SettingViewState> = MutableLiveData()
    val viewState: LiveData<SettingViewState> = _viewState

    private val spendKey = "지출 카테고리"
    private val incomeKey = "수입 카테고리"

    private val navigateDelay = 200L

    fun fetchLabels(){
        _dataLoading.value = true
        viewModelScope.launch {
            fetchUserCase(spendKey, incomeKey)
                .onSuccess {
                    _textLabels.value = it.first
                    _colorLabels.value = it.second
                }
                .onFailure {
                    it.printStackTrace()
                    _errorMessage.value = it.message
                }.also {
                    _dataLoading.value = false
                }
        }
    }

    val clickTextLabel: (TextLabel) -> (Unit) = {
        Timber.d("click text ${it.title}")
        viewModelScope.launch {
            delay(navigateDelay)
            _viewState.value = SettingViewState.NavigateToPaymentEditView(it)
        }
    }

    val clickColorLabel: (ColorLabel) -> (Unit) = {
        Timber.d("click color ${it.title}")
        viewModelScope.launch {
            delay(navigateDelay)
            colorLabels.value?.let { map ->
                when {
                    map[spendKey]?.contains(it) == true -> {
                        _viewState.value = SettingViewState.NavigateToSpendLabelEditView(it)
                    }
                    map[incomeKey]?.contains(it) == true -> {
                        _viewState.value = SettingViewState.NavigateToIncomeLabelEditView(it)
                    }
                }
            }
        }
    }

    val clickPlusType:(String) -> (Unit) = {
        Timber.d("click type $it")
        viewModelScope.launch {
            delay(navigateDelay)
            when(it){
                spendKey -> _viewState.value = SettingViewState.NavigateToSpendLabelEditView(null)
                incomeKey -> _viewState.value = SettingViewState.NavigateToIncomeLabelEditView(null)
                else -> _viewState.value = SettingViewState.NavigateToPaymentEditView(null)
            }
        }
    }

    sealed class SettingViewState(
        val textLabel: TextLabel? = null,
        val colorLabel: ColorLabel? = null
    ) {
        class NavigateToPaymentEditView(textLabel: TextLabel?): SettingViewState(textLabel)
        class NavigateToSpendLabelEditView(colorLabel: ColorLabel?): SettingViewState(colorLabel = colorLabel)
        class NavigateToIncomeLabelEditView(colorLabel: ColorLabel?): SettingViewState(colorLabel = colorLabel)
    }
}