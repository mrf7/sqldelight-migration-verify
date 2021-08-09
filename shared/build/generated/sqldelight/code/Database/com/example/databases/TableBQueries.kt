package com.example.databases

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Long
import kotlin.String
import kotlin.Unit

public interface TableBQueries : Transacter {
  public fun <T : Any> selectAll(mapper: (bStuff: String, id: Long) -> T): Query<T>

  public fun selectAll(): Query<TableB>

  public fun <T : Any> selectById(id: Long, mapper: (bStuff: String, id: Long) -> T): Query<T>

  public fun selectById(id: Long): Query<TableB>

  public fun insertB(TableB: TableB): Unit
}
