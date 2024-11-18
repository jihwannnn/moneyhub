package com.example.moneyhub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionRecyclerAdapter(
    private val items: List<TransactionRecyclerDataClass>,
    private val isForBudget: Boolean,
    private val onItemClick: () -> Unit = {}  // 기본값 설정
) : RecyclerView.Adapter<TransactionRecyclerAdapter.TransactionViewHolder>() {

    // ViewHolder: 아이템 뷰를 저장하는 클래스
    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.item_icon)
        val title: TextView = itemView.findViewById(R.id.item_title)
        val category: TextView = itemView.findViewById(R.id.item_category)
        val transaction: TextView = itemView.findViewById(R.id.item_transaction)

        init {
            itemView.setOnClickListener {
                onItemClick.invoke()
            }
        }
    }


    // onCreateViewHolder: 새로운 뷰 홀더를 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        // isForBudget 값에 따라 다른 layout 사용
        val layoutId = if (isForBudget) R.layout.item_layout_budget else R.layout.item_layout_history
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return TransactionViewHolder(view)
    }

    // onBindViewHolder: 뷰 홀더에 데이터를 바인딩
    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = items[position]

        // 데이터를 ViewHolder에 반영
        holder.icon.setImageResource(item.icon)
        holder.title.text = item.title
        holder.category.text = item.category
        holder.transaction.text = if (item.transaction < 0) "-$ ${-item.transaction}" else "$ ${item.transaction}"

        // 조건에 따라 배경색 변경
        val textColor = if (item.transaction < 0) R.color.moneyRed else R.color.moneyGreenThick
        holder.transaction.setTextColor(holder.itemView.context.getColor(textColor))
    }

    // getItemCount: 아이템의 총 개수를 반환
    override fun getItemCount(): Int = items.size
}
