package com.keuangan.app

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class TransactionAdapter(
    private val onDelete: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    private var items: List<Transaction> = emptyList()

    fun submitList(newItems: List<Transaction>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onDelete)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_item_title)
        private val tvMeta: TextView = itemView.findViewById(R.id.tv_item_meta)
        private val tvAmount: TextView = itemView.findViewById(R.id.tv_item_amount)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)

        fun bind(tx: Transaction, onDelete: (Transaction) -> Unit) {
            tvTitle.text = tx.title
            tvMeta.text = "${tx.category} • ${tx.date}"

            val isIncome = tx.type == TransactionType.INCOME
            val sign = if (isIncome) "+" else "-"
            tvAmount.text = "$sign${formatRupiah(tx.amount)}"
            tvAmount.setTextColor(Color.parseColor(if (isIncome) "#15803D" else "#B91C1C"))

            btnDelete.setOnClickListener { onDelete(tx) }
        }
    }

    companion object {
        fun formatRupiah(amount: Long): String {
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            formatter.maximumFractionDigits = 0
            return formatter.format(amount)
        }
    }
}
