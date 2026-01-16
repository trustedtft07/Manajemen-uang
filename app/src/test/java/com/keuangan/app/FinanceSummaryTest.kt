package com.keuangan.app

import org.junit.Assert.assertEquals
import org.junit.Test

class FinanceSummaryTest {

    @Test
    fun calculatesBalanceFromTransactions() {
        val items = listOf(
            Transaction("1", "Gaji", "Gaji", 5000000L, TransactionType.INCOME, "01 Jan 2026"),
            Transaction("2", "Makan", "Makan", 50000L, TransactionType.EXPENSE, "02 Jan 2026"),
            Transaction("3", "Transport", "Transport", 25000L, TransactionType.EXPENSE, "02 Jan 2026")
        )

        var income = 0L
        var expense = 0L
        for (tx in items) {
            if (tx.type == TransactionType.INCOME) income += tx.amount else expense += tx.amount
        }

        assertEquals(5000000L, income)
        assertEquals(75000L, expense)
        assertEquals(4925000L, income - expense)
    }
}
