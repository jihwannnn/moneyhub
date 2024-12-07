package com.example.moneyhub.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.moneyhub.BudgetFragment
import com.example.moneyhub.CalendarFragment
import com.example.moneyhub.HistoryFragment

class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HistoryFragment()
            1 -> CalendarFragment()
            2 -> BudgetFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}