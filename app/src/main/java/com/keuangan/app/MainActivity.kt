package com.keuangan.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var repository: TransactionRepository
    private lateinit var adapter: TransactionAdapter
    private val allTransactions = mutableListOf<Transaction>()

    private lateinit var tvBalance: TextView
    private lateinit var tvIncome: TextView
    private lateinit var tvExpense: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = TransactionRepository(this)
        allTransactions.addAll(repository.loadAll())

        tvBalance = findViewById(R.id.tv_balance)
        tvIncome = findViewById(R.id.tv_income)
        tvExpense = findViewById(R.id.tv_expense)

        adapter = TransactionAdapter { tx ->
            allTransactions.removeAll { it.id == tx.id }
            repository.saveAll(allTransactions)
            refreshUi()
        }

        val recyclerView: RecyclerView = findViewById(R.id.rv_transactions)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        refreshUi()
    }

    private fun refreshUi() {
        var income = 0L
        var expense = 0L
        for (tx in allTransactions) {
            if (tx.type == TransactionType.INCOME) income += tx.amount else expense += tx.amount
        }
        tvBalance.text = TransactionAdapter.formatRupiah(income - expense)
        tvIncome.text = TransactionAdapter.formatRupiah(income)
        tvExpense.text = TransactionAdapter.formatRupiah(expense)
        adapter.submitList(allTransactions.toList())
    }
}