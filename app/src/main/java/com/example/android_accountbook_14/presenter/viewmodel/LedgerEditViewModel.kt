package com.example.android_accountbook_14.presenter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_accountbook_14.domain.model.ColorLabel
import com.example.android_accountbook_14.domain.model.LedgerModel
import com.example.android_accountbook_14.domain.usecase.ConvertDayOfWeekUseCase
import com.example.android_accountbook_14.domain.usecase.FetchLabelBundleUseCase
import com.example.android_accountbook_14.domain.usecase.InsertLedgerUserCase
import com.example.android_accountbook_14.domain.usecase.UpdateLedgerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class LedgerEditViewModel @Inject constructor(
    private val fetchLabelBundleUseCase: FetchLabelBundleUseCase,
    private val insertLedgerUseCase: InsertLedgerUserCase,
    private val updateLedgerUseCse: UpdateLedgerUseCase,
    private val convertDayOfWeekUseCase: ConvertDayOfWeekUseCase
): ViewModel() {
    private val _dataLoading: MutableLiveData<Boolean> = MutableLiveData()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _modeChanged: MutableLiveData<LedgerRegisterMode> = MutableLiveData()
    val modeChanged: LiveData<LedgerRegisterMode> = _modeChanged

    private val _payment: MutableLiveData<List<Pair<Int, String>>> = MutableLiveData()
    val payment: LiveData<List<Pair<Int, String>>> = _payment

    private val _labels: MutableLiveData<List<Pair<Int, String>>> = MutableLiveData()
    val labels: LiveData<List<Pair<Int, String>>> = _labels

    private val _btnEnable: MutableLiveData<Boolean> = MutableLiveData()
    val btnEnable: LiveData<Boolean> = _btnEnable

    private val _isEditMode: MutableLiveData<Boolean> = MutableLiveData()
    val isEditMode: LiveData<Boolean> = _isEditMode

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String> = _errorMessage

    private val _finishView: MutableLiveData<String> = MutableLiveData()
    val finishView: LiveData<String> = _finishView

    private val _editView: MutableLiveData<LedgerModel> = MutableLiveData()
    val editView: LiveData<LedgerModel> =_editView

    private val _navigateToAddSpendLabel: MutableLiveData<Boolean> = MutableLiveData()
    val navigateToAddSpendLabel: LiveData<Boolean> = _navigateToAddSpendLabel

    private val _navigateToAddIncomeLabel: MutableLiveData<Boolean> = MutableLiveData()
    val navigateToAddIncomeLabel: LiveData<Boolean> = _navigateToAddIncomeLabel

    private val _navigateToAddPayment: MutableLiveData<Boolean> = MutableLiveData()
    val navigateToAddPayment: LiveData<Boolean> = _navigateToAddPayment

    private val _priceText: MutableLiveData<String> = MutableLiveData()
    val priceText: LiveData<String> =_priceText


    private var currentMode = LedgerRegisterMode.Income
    enum class LedgerRegisterMode{
        Income, Spend
    }

    private val spendLabelKey = "spend"
    private val incomeLabelKey = "income"
    private var labelBundle: Map<String, List<ColorLabel>>? = null
    private var editItem: LedgerModel? = null


    var inputDate: String? = null
        set(value) {
            field = value
            setRegisterBtnEnable()
        }

    private val df: DecimalFormat = DecimalFormat("###,###.####") //currency decimal format
    var inputPrice: String? = null
        set(it) {
            if(it.isNullOrBlank()) return
            if(it != field) { // StackOverflow 를 막기위함
                val price = try{
                    it.replace(",", "").toLong()
                }catch (e: Exception){
                    e.printStackTrace()
                    _priceText.value = field
                    return
                }
                field = df.format(price) // EditText 값을 변환하여, result 에 저장.
                _priceText.value = field
            }
            setRegisterBtnEnable()
        }

    private var selectPaymentId: Int? = null
    var inputPayment: String? = null
        set(value) {
            field = value
            _payment.value?.let { pair ->
                selectPaymentId = pair.find { it.second == value }?.first
            }
            setRegisterBtnEnable()
        }
    private var defaultLabelId: Int? = null
    private var selectLabelId: Int? = null
    var inputLabel: String? = null
        set(value) {
            field = value
            _labels.value?.let { pair ->
                selectLabelId = pair.find { it.second == value }?.first
            }
            setRegisterBtnEnable()
        }

    var inputContent: String? = null

    private fun setRegisterBtnEnable(){
        _btnEnable.value = when(currentMode){
            LedgerRegisterMode.Income -> !inputDate.isNullOrBlank() &&
                    !inputPrice.isNullOrBlank() &&
                    inputPrice?.replace(",","")?.toLongOrNull() != null

            else -> !inputDate.isNullOrBlank() &&
                    !inputPrice.isNullOrBlank() &&
                    inputPrice?.replace(",","")?.toLongOrNull() != null
        }
    }

    fun initView(mode: Int?, editItem: LedgerModel?){
        Timber.d("init view [$mode], [$editItem]")
        this.editItem = editItem
        _isEditMode.value = editItem != null
        if(mode == null || mode>=0)
            setMode(LedgerRegisterMode.Income)
        else
            setMode(LedgerRegisterMode.Spend)
    }
    fun setMode(mode: LedgerRegisterMode?){
        currentMode = when(mode){
            LedgerRegisterMode.Income -> LedgerRegisterMode.Income
            else -> LedgerRegisterMode.Spend
        }
        _modeChanged.value = currentMode
        processColorLabelsBundle()
    }

    val dateConverter: (Int, Int, Int, String?) -> (String) = { y, m, d, dayOfWeek ->
        if(dayOfWeek.isNullOrBlank())
            "${y}/${m}/${d} ${getDayOfWeek(y, m, d)}"
        else
            "${y}/${m}/${d} $dayOfWeek"
    }
    fun getDayOfWeek(year: Int, month: Int, dayOfMonth: Int): String {
        return convertDayOfWeekUseCase(year, month, dayOfMonth).second
    }

    fun fetchLabelsBundle(){
        _dataLoading.value = true
        viewModelScope.launch {
            fetchLabelBundleUseCase(spendLabelKey, incomeLabelKey)
                .onSuccess { pair ->
                    _payment.value = pair.first["결제수단"]?.map { it.id to it.title }
                    labelBundle = pair.second
                    processColorLabelsBundle()
                    editItem?.let {
                        _editView.value = it
                    }
                }
                .onFailure {
                    it.printStackTrace()
                }.also {
                    _dataLoading.value = false
                }
        }
    }
    private fun processColorLabelsBundle(){
        labelBundle?.let { map ->
            _labels.value = when (currentMode) {
                LedgerRegisterMode.Income -> requireNotNull(map[incomeLabelKey]).map { it.id to it.title }
                LedgerRegisterMode.Spend -> requireNotNull(map[spendLabelKey]).map { it.id to it.title }
            }
        }
        defaultLabelId = _labels.value?.firstOrNull()?.first
    }

    fun clickAddLabel(){
        when(currentMode){
            LedgerRegisterMode.Income -> _navigateToAddIncomeLabel.value = true
            else -> _navigateToAddSpendLabel.value = true
        }
    }

    fun clickAddPayment(){
        _navigateToAddPayment.value = true
    }

    fun clickRegisterBtn(){
        if(inputPrice?.replace(",","")?.toLongOrNull() == null){
            _errorMessage.value = "가격을 입력해주세요"
            return
        }
        if(defaultLabelId == null){
            _errorMessage.value = "분류를 선택해주세요"
            return
        }
        _dataLoading.value = true
        viewModelScope.launch {
            var price: Long = inputPrice?.replace(",","")?.toLongOrNull()!!
            price = when(currentMode){
                LedgerRegisterMode.Income -> price
                else -> -price
            }
            val paymentId = when(currentMode){
                LedgerRegisterMode.Income -> selectPaymentId
                else -> null
            }

            val dateArr = inputDate!!.split('/')

            val (day, dayOfWeek) = dateArr[2].split(" ")
            if(editItem == null){
                insertLedger(
                    dateArr[0].toInt(),
                    dateArr[1].toInt(),
                    day.toInt(),
                    dayOfWeek,
                    price,
                    selectLabelId ?: defaultLabelId!!,
                    paymentId,
                    inputContent
                )
            }else{
                updateLedger(
                    editItem!!.id,
                    dateArr[0].toInt(),
                    dateArr[1].toInt(),
                    dateArr[2].split(" ")[0].toInt(),
                    dateArr[2].split(" ")[1],
                    price,
                    selectLabelId ?: defaultLabelId!!,
                    paymentId,
                    inputContent
                )
            }
        }
    }
    private suspend fun insertLedger(
        year: Int,
        month: Int,
        dayOfMonth: Int,
        dayOfWeek: String,
        price: Long,
        tagId: Int,
        paymentId: Int?,
        content: String?
    ){
        insertLedgerUseCase(
            year,
            month,
            dayOfMonth,
            dayOfWeek,
            price,
            tagId,
            paymentId,
            content
        ).onSuccess {
            _finishView.value = "추가 성공"
        }.onFailure {
            it.printStackTrace()
            _errorMessage.value = it.message
        }.also {
            _dataLoading.value = false
        }
    }

    private suspend fun updateLedger(
        id: Int,
        year: Int,
        month: Int,
        dayOfMonth: Int,
        dayOfWeek: String,
        price: Long,
        tagId: Int,
        paymentId: Int?,
        content: String?
    ){
        updateLedgerUseCse(
            id,
            year,
            month,
            dayOfMonth,
            dayOfWeek,
            price,
            tagId,
            paymentId,
            content
        ).onSuccess {
            when(it){
                true -> _finishView.value = "업데이트 성공"
                else -> _errorMessage.value = "업데이트 실패"
            }
        }.onFailure {
            it.printStackTrace()
            _errorMessage.value = it.message
        }.also {
            _dataLoading.value = false
        }
    }

}