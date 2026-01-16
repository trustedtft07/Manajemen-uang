package com.keuangan.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private lateinit var repository: TransactionRepository
    private lateinit var adapter: TransactionAdapter
    private val allTransactions = mutableListOf<Transaction>()
    private var activeFilter: TransactionType? = null

    private lateinit var tvBalance: TextView
    private lateinit var tvIncome: TextView
    private lateinit var tvExpense: TextView
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repository = TransactionRepository(this)
        allTransactions.addAll(repository.loadAll())

        tvBalance = findViewById(R.id.tv_balance)
        tvIncome = findViewById(R.id.tv_income)
        tvExpense = findViewById(R.id.tv_expense)
        tvEmpty = findViewById(R.id.tv_empty)

        adapter = TransactionAdapter { tx ->
            allTransactions.removeAll { it.id == tx.id }
            repository.saveAll(allTransactions)
            refreshUi()
        }

        val recyclerView: RecyclerView = findViewById(R.id.rv_transactions)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val chipGroup: ChipGroup = findViewById(R.id.chip_filter)
        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            activeFilter = when (checkedIds.firstOrNull()) {
                R.id.chip_income -> TransactionType.INCOME
                R.id.chip_expense -> TransactionType.EXPENSE
                else -> null
            }
            refreshList()
        }

        findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener {
            showAddDialog()
        }

        refreshUi()
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_transaction, null)
        val etTitle = dialogView.findViewById<EditText>(R.id.et_title)
        val etAmount = dialogView.findViewById<EditText>(R.id.et_amount)
        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinner_category)
        val rbIncome = dialogView.findViewById<RadioButton>(R.id.rb_income)

        val categories = arrayOf("Makan", "Transport", "Tagihan", "Belanja", "Gaji", "Lainnya")
        spinnerCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)

        AlertDialog.Builder(this)
            .setTitle("Tambah Transaksi")
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val title = etTitle.text.toString().trim()
                val amount = etAmount.text.toString().trim().toLongOrNull()

                if (title.isEmpty() || amount == null || amount <= 0) {
                    Toast.makeText(this, "Keterangan dan nominal harus valid", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val type = if (rbIncome.isChecked) TransactionType.INCOME else TransactionType.EXPENSE
                val date = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date())

                val tx = Transaction(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    category = spinnerCategory.selectedItem.toString(),
                    amount = amount,
                    type = type,
                    date = date
                )

                allTransactions.add(0, tx)
                repository.saveAll(allTransactions)
                refreshUi()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun refreshUi() {
        val summary = calculateSummary()
        tvBalance.text = TransactionAdapter.formatRupiah(summary.balance)
        tvIncome.text = TransactionAdapter.formatRupiah(summary.totalIncome)
        tvExpense.text = TransactionAdapter.formatRupiah(summary.totalExpense)
        refreshList()
    }

    private fun refreshList() {
        val filtered = activeFilter?.let { filter ->
            allTransactions.filter { it.type == filter }
        } ?: allTransactions.toList()

        adapter.submitList(filtered)
        tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun calculateSummary(): FinanceSummary {
        var income = 0L
        var expense = 0L
        for (tx in allTransactions) {
            if (tx.type == TransactionType.INCOME) income += tx.amount else expense += tx.amount
        }
        return FinanceSummary(income - expense, income, expense)
    }
}
