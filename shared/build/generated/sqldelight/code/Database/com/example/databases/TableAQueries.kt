package com.example.databases

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Long
import kotlin.String
import kotlin.Unit

public interface TableAQueries : Transacter {
  public fun <T : Any> selectAll(mapper: (
    name: String,
    number: Long,
    bId: Long
  ) -> T): Query<T>

  public fun selectAll(): Query<TableA>

  public fun insertA(TableA: TableA): Unit
}
