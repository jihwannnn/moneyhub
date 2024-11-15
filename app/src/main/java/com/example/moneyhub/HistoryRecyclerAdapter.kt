package com.example.moneyhub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryRecyclerAdapter(
    private val items: List<HistoryRecyclerDataClass>
) : RecyclerView.Adapter<HistoryRecyclerAdapter.HistoryViewHolder>() {

    // ViewHolder: 아이템 뷰를 저장하는 클래스
    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.item_icon)
        val title: TextView = itemView.findViewById(R.id.item_title)
        val category: TextView = itemView.findViewById(R.id.item_category)
        val transaction: TextView = itemView.findViewById(R.id.item_transaction)
    }

    // onCreateViewHolder: 새로운 뷰 홀더를 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_history, parent, false)
        return HistoryViewHolder(view)
    }

    // onBindViewHolder: 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = items[position]
        holder.icon.setImageResource(item.icon)
        holder.title.text = item.title
        holder.category.text = item.category
        holder.transaction.text = if (item.transaction < 0) "-$ ${-item.transaction}" else "$ ${item.transaction}"
    }

    // getItemCount: 아이템의 총 개수를 반환
    override fun getItemCount(): Int = items.size
}
