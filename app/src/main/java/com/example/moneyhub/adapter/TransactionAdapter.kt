package com.example.moneyhub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.moneyhub.R
import com.example.moneyhub.model.Transaction
import java.util.Calendar
import java.util.Date

// 변경: items -> transactions로 변경하여 데이터 업데이트 지원
class TransactionAdapter(
    private var transactions: List<Transaction>,
    private val isForBudget: Boolean,
    private val isForCalendar: Boolean = false,
    private val onItemClick: (Transaction) -> Unit = {},
    private val onDeleteClick: ((Transaction) -> Unit)? = null
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.item_title)
        val category: TextView = itemView.findViewById(R.id.item_category)
        val transaction: TextView = itemView.findViewById(R.id.item_transaction)
        val deleteButton: ImageView = itemView.findViewById(R.id.btn_delete)

        init {
            itemView.setOnClickListener {
                onItemClick.invoke(transactions[position])
            }

            deleteButton.setOnClickListener { view ->
                val transaction = transactions[position]
                AlertDialog.Builder(view.context)
                    .setTitle("거래 내역 삭제")
                    .setMessage("정말로 이 거래 내역을 삭제하시겠습니까?")
                    .setPositiveButton("삭제") { dialog, _ ->
                        onDeleteClick?.invoke(transaction)
                        dialog.dismiss()
                    }
                    .setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val layoutId = R.layout.item_layout_history
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)

        // 기본 배경 설정
        val backgroundRes = when {
            isForCalendar -> R.drawable.yellow_thin_block_less_corners
            isForBudget -> R.drawable.grey_block
            else -> R.drawable.cyan_block
        }
        view.setBackgroundResource(backgroundRes)

        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = transactions[position]

        holder.title.text = item.title
        holder.category.text = item.category

        // 금액 표시 (type에 따라 +/- 구분)
        val amountText = if (item.type) {
            String.format("+ ₩%,d", item.amount)
        } else {
            String.format("- ₩%,d", item.amount)
        }
        holder.transaction.text = amountText

        // 금액 색상 설정
        val textColorRes = if (item.type) {
            R.color.moneyGreenThick  // 수입
        } else {
            R.color.moneyRed  // 지출
        }
        holder.transaction.setTextColor(holder.itemView.context.getColor(textColorRes))

        // Calendar에서의 과거/미래 날짜 구분
        if (isForCalendar) {
            val currentDate = Calendar.getInstance()
            val itemDate = Date(item.payDate)

            val calendarBackgroundRes = if (itemDate.before(currentDate.time)) {
                R.drawable.yellow_thin_block_less_corners
            } else {
                R.drawable.grey_block
            }
            holder.itemView.setBackgroundResource(calendarBackgroundRes)
        }

        // 삭제 버튼 표시 여부
        holder.deleteButton.visibility = if (!isForBudget && !isForCalendar && onDeleteClick != null) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun getItemCount(): Int = transactions.size
}