package com.example.moneyhub.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.moneyhub.R
import com.example.moneyhub.adapter.HomePagerAdapter
import com.example.moneyhub.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import java.time.YearMonth
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val homeViewModel: HomeViewModel by viewModels()
    private val sharedViewModel: SharedTransactionViewModel by activityViewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Call setupViewPagerAndTabs
        setupViewPagerAndTabs()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 월 이동 버튼 설정
        setupMonthNavigation()
        // 년월 변경 관찰
        observeYearMonth()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 월 이동 버튼 클릭 리스너 설정
    private fun setupMonthNavigation() {
        // 이전 달 버튼 클릭 시
        binding.imageViewPrevioiusMonthButton.setOnClickListener {
            homeViewModel.moveToPreviousMonth()
        }

        // 다음 달 버튼 클릭 시
        binding.imageViewNextMonthButton.setOnClickListener {
            homeViewModel.moveToNextMonth()
        }
    }

    // 현재 선택된 년월 변경 감지 및 처리
    private fun observeYearMonth() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.currentYearMonth.collect { yearMonth ->
                // 현재 월 텍스트 업데이트 (예: "Oct")
                binding.currentMonthText.text = homeViewModel.getMonthDisplayText()

                // 선택된 월에 해당하는 거래 내역만 필터링하도록 요청
                sharedViewModel.updateMonthlyTransactions(yearMonth)
            }
        }
    }

    private fun setupViewPagerAndTabs() {
        val adapter = HomePagerAdapter(this)
        binding.viewPager.adapter = adapter

        // connecting 'TabLayout' and 'ViewPager'
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.customView = createTabView(position)
        }.attach()

        // Remove padding for each tab to eliminate space between border and items
        binding.tabLayout.apply {
            for (i in 0 until tabCount) {
                val tab = getTabAt(i)
                val view = (tab?.view as? ViewGroup)
                view?.setPadding(0, 0, 0, 0)
            }
        }

        // set the initially selected tap background
        setInitialTabState()

        // change color according to the states of tap selection
        binding.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}