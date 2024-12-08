package com.example.moneyhub.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.moneyhub.R
import com.example.moneyhub.adapter.HomePagerAdapter
import com.example.moneyhub.databinding.FragmentHomeBinding
import com.example.moneyhub.fragments.SharedTransactionViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel by viewModels()
    private val sharedViewModel: SharedTransactionViewModel by activityViewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupViewPagerAndTabs()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMonthNavigation()
        observeYearMonth()
        observeCurrentUser()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupMonthNavigation() {
        binding.imageViewPrevioiusMonthButton.setOnClickListener {
            val newMonth = homeViewModel.currentYearMonth.value.minusMonths(1)
            homeViewModel.moveToPreviousMonth()
            sharedViewModel.setCurrentYearMonth(newMonth)   // ViewModel에 변경 사항 반영
        }

        binding.imageViewNextMonthButton.setOnClickListener {
            val newMonth = homeViewModel.currentYearMonth.value.plusMonths(1)
            homeViewModel.moveToNextMonth()
            sharedViewModel.setCurrentYearMonth(newMonth)   // ViewModel에 변경 사항 반영
        }
    }

    private fun observeYearMonth() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.currentYearMonth.collect { yearMonth ->
                    binding.currentMonthText.text = "${yearMonth.month.name.take(3)} ${yearMonth.year}"

                    homeViewModel.setCurrentYearMonth(yearMonth)
                }
            }

            homeViewModel.currentYearMonth.collect { yearMonth ->
                binding.currentMonthText.text = homeViewModel.getMonthDisplayText()
                sharedViewModel.updateMonthlyTransactions(yearMonth)
            }
        }
    }

    private fun observeCurrentUser(){
        viewLifecycleOwner.lifecycleScope.launch{
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                sharedViewModel.currentUser.collect{user ->
                    user?.let{
                        binding.customGroupbarYellowInclude.textViewGroupName.text = it.currentGname
                    }
                }
            }
        }
    }

    private fun setupViewPagerAndTabs() {
        val adapter = HomePagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.customView = createTabView(position)
        }.attach()

        binding.tabLayout.apply {
            for (i in 0 until tabCount) {
                val tab = getTabAt(i)
                val view = (tab?.view as? ViewGroup)
                view?.setPadding(0, 0, 0, 0)
            }
        }

        setInitialTabState()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.customView?.setBackgroundResource(R.drawable.tab_selected_background)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.customView?.setBackgroundResource(R.drawable.tab_unselected_background)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Do nothing
            }
        })
    }

    private fun setInitialTabState() {
        val initialTab = binding.tabLayout.getTabAt(binding.viewPager.currentItem)
        initialTab?.customView?.setBackgroundResource(R.drawable.tab_selected_background)
    }

    private fun createTabView(position: Int): View {
        val tabView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab, null)
        val tabText = tabView.findViewById<TextView>(R.id.tabText)
        tabText.text = when (position) {
            0 -> "History"
            1 -> "Calendar"
            2 -> "Budget"
            else -> "Tab"
        }
        return tabView
    }

    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }
}