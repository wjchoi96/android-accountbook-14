package com.example.android_accountbook_14.presenter.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.example.android_accountbook_14.R
import com.example.android_accountbook_14.databinding.FragmentLedgerListBinding
import com.example.android_accountbook_14.presenter.ui.compose.LedgerListScreen
import com.example.android_accountbook_14.presenter.viewmodel.LedgerListViewModel
import com.example.android_accountbook_14.presenter.viewmodel.MainViewModel
import com.example.android_accountbook_14.util.ColorUtil
import com.example.android_accountbook_14.util.showSnackBar
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LedgerListFragment: BaseFragment<FragmentLedgerListBinding>() {
    override val layoutResId: Int
        get() = R.layout.fragment_ledger_list

    private val viewModel: LedgerListViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.composeView.setContent {
            MdcTheme {
                LedgerListScreen(
                    ledgerList = viewModel.ledgerListItems,
                    selectLedgers = viewModel.selectLedgers,
                    itemClickListener = viewModel.itemClickListener,
                    itemLongClickListener = viewModel.itemLongClickListener
                ){ ColorUtil.convertToComposeColor(it)}
            }
        }
        binding.checkBoxLeft.title = "수입"
        binding.checkBoxRight.title = "지출"
        setToolbar()
        setListener()
        observeData()
        binding.checkBoxLeft.checked = viewModel.leftCheckedState
        binding.checkBoxRight.checked = viewModel.rightCheckedState
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchList()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden)
            viewModel.fetchList()
    }

    private fun setToolbar(){
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(binding.toolBar)
            supportActionBar?.apply {
                setDisplayShowTitleEnabled(false)
                setDisplayHomeAsUpEnabled(false)
            }
        }
    }


    private fun setListener(){
        binding.checkBoxLeft.setCheckChangedListener {
            viewModel.clickFilterButton(it, binding.checkBoxRight.isSelected)
        }

        binding.checkBoxRight.setCheckChangedListener {
            viewModel.clickFilterButton(binding.checkBoxLeft.isSelected, it)
        }

        binding.btnRegister.setOnClickListener {
            viewModel.clickRegisterButton()
        }

        binding.btnLeftArrow.setOnClickListener {
            mainViewModel.minusMonth()
        }

        binding.btnRightArrow.setOnClickListener {
            mainViewModel.plusMonth()
        }

        binding.btnRemove.setOnClickListener {
            viewModel.clickRemoveButton()
        }

        binding.tvToolBarTitle.setOnClickListener {
            viewModel.clickTitle()
        }
    }

    private fun observeData(){
        mainViewModel.yearMonthData.observe(viewLifecycleOwner){
            viewModel.setDate(it.first, it.second)
        }

        viewModel.dataLoading.observe(viewLifecycleOwner) {
            binding.progress.isVisible = it
        }

        viewModel.errorMessage.observe(viewLifecycleOwner){
            showSnackBar(binding.layoutBackground, it)
        }

        viewModel.title.observe(viewLifecycleOwner){
            binding.tvToolBarTitle.text = it
        }

        viewModel.totalSpend.observe(viewLifecycleOwner) {
            binding.checkBoxRight.title = "지출 $it"
        }

        viewModel.totalIncome.observe(viewLifecycleOwner) {
            binding.checkBoxLeft.title = "수입 $it"
        }

        viewModel.navigateToEditView.observe(viewLifecycleOwner){
            parentFragmentManager.commit {
                addToBackStack(null)
                hide(this@LedgerListFragment)
                add(R.id.container_fragment, LedgerEditFragment.getInstanceEditMode(it))
            }
        }

        viewModel.navigateToRegisterView.observe(viewLifecycleOwner){
            parentFragmentManager.commit {
                addToBackStack(null)
                hide(this@LedgerListFragment)
                add(R.id.container_fragment, LedgerEditFragment.getInstance(it))
            }
        }

        viewModel.selectMode.observe(viewLifecycleOwner){
            binding.btnRemove.isVisible = it
            binding.btnLeftArrow.isVisible = !it
            binding.btnRightArrow.isVisible = !it
        }

        viewModel.showDatePickDialog.observe(viewLifecycleOwner){
            it.first.setDismissListener { y, m ->
                mainViewModel.setData(y, m) // viewModel 에서 main view model 을 아는걸 원치 않아서 이렇게 작성
            }
            it.first.show(childFragmentManager, it.second)

        }
    }
}