package com.keuangan.app

enum class TransactionType {
    INCOME,
    EXPENSE
}

data class Transaction(
    val id: String,
    val title: String,
    val category: String,
    val amount: Long,
    val type: TransactionType,
    val date: String
)
