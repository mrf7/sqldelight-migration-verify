package com.example.shared

import com.example.Database
import com.example.databases.TableA

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.example.databases.TableB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DatabaseHelper(driver: SqlDriver) {
    val db = Database(driver)

    fun getAll(): Flow<List<A>> =
        db.tableAQueries.selectAll()
            .asFlow()
            .mapToList()
            .map { entries ->
                entries.map {
                    A(it.name, it.number.toInt(), getBById(it.bId.toInt()))
                }
            }

    private suspend fun getBById(id: Int): B = withContext(Dispatchers.Default) {
        val row = db.tableBQueries.selectById(id.toLong()).executeAsOne()
        B(row.id.toInt(), row.bStuff)
    }

    suspend fun insert(entry: A) {
        withContext(Dispatchers.Default) {
            db.transaction {
                val b = TableB(entry.b.data, entry.b.id.toLong())
                db.tableBQueries.insertB(b)
                val a = TableA(entry.name, entry.number.toLong(), b.id)
                db.tableAQueries.insertA(a)
            }
        }
    }
}

data class A(val name: String, val number: Int, val b: B)
data class B(val id: Int, val data: String)