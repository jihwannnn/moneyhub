package com.example.moneyhub

import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.moneyhub.R
import com.example.moneyhub.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding:FragmentHomeBinding

    override fun onCreate(savedInstnaceState: Bundle?) {
        super.onCreate(savedInstnaceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Call setupViewPagerAndTabs
        setupViewPagerAndTabs()

        return binding.root
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
        val initalTab = binding.tabLayout.getTabAt(binding.viewPager.currentItem)
        initalTab?.customView?.setBackgroundResource(R.drawable.tab_selected_background)
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