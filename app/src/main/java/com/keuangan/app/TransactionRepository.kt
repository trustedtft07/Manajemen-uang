package com.keuangan.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class TransactionRepository(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadAll(): MutableList<Transaction> {
        val raw = prefs.getString(KEY_ITEMS, null) ?: return mutableListOf()
        val list = mutableListOf<Transaction>()
        val array = JSONArray(raw)

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val type = runCatching { TransactionType.valueOf(obj.getString("type")) }.getOrNull()
                ?: continue
            list.add(
                Transaction(
                    id = obj.getString("id"),
                    title = obj.getString("title"),
                    category = obj.getString("category"),
                    amount = obj.getLong("amount"),
                    type = type,
                    date = obj.getString("date")
                )
            )
        }
        return list
    }

    fun saveAll(items: List<Transaction>) {
        val array = JSONArray()
        for (item in items) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("category", item.category)
                put("amount", item.amount)
                put("type", item.type.name)
                put("date", item.date)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_ITEMS, array.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "keuangan_prefs"
        private const val KEY_ITEMS = "transactions"
    }
}
