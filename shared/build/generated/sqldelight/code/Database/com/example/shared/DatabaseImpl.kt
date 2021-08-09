package com.example.shared

import com.example.Database
import com.example.databases.TableA
import com.example.databases.TableAQueries
import com.example.databases.TableB
import com.example.databases.TableBQueries
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.TransacterImpl
import com.squareup.sqldelight.`internal`.copyOnWriteList
import com.squareup.sqldelight.db.SqlCursor
import com.squareup.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Unit
import kotlin.collections.MutableList
import kotlin.reflect.KClass

internal val KClass<Database>.schema: SqlDriver.Schema
  get() = DatabaseImpl.Schema

internal fun KClass<Database>.newInstance(driver: SqlDriver): Database = DatabaseImpl(driver)

private class DatabaseImpl(
  driver: SqlDriver
) : TransacterImpl(driver), Database {
  public override val tableAQueries: TableAQueriesImpl = TableAQueriesImpl(this, driver)

  public override val tableBQueries: TableBQueriesImpl = TableBQueriesImpl(this, driver)

  public object Schema : SqlDriver.Schema {
    public override val version: Int
      get() = 2

    public override fun create(driver: SqlDriver): Unit {
      driver.execute(null, """
          |CREATE TABLE TableB(
          |    bStuff TEXT NOT NULL,
          |    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE TableA (
          |    name TEXT NOT NULL PRIMARY KEY,
          |    number INTEGER NOT NULL,
          |    bId INTEGER NOT NULL,
          |    FOREIGN KEY (bId) REFERENCES TableB(id)
          |)
          """.trimMargin(), 0)
    }

    public override fun migrate(
      driver: SqlDriver,
      oldVersion: Int,
      newVersion: Int
    ): Unit {
      if (oldVersion <= 1 && newVersion > 1) {
        driver.execute(null, "ALTER TABLE TableB RENAME TO TempTableB", 0)
        driver.execute(null, """
            |CREATE TABLE TableB(
            |    bStuff TEXT NOT NULL,
            |    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT
            |)
            """.trimMargin(), 0)
        driver.execute(null, "INSERT INTO TableB(bStuff,  id) SELECT bStuff, id FROM TempTableB", 0)
        driver.execute(null, "DROP TABLE TempTableB", 0)
      }
    }
  }
}

private class TableBQueriesImpl(
  private val database: DatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), TableBQueries {
  internal val selectAll: MutableList<Query<*>> = copyOnWriteList()

  internal val selectById: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> selectAll(mapper: (bStuff: String, id: Long) -> T): Query<T> =
      Query(-1251296784, selectAll, driver, "TableB.sq", "selectAll", "SELECT * FROM TableB") {
      cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getLong(1)!!
    )
  }

  public override fun selectAll(): Query<TableB> = selectAll { bStuff, id ->
    TableB(
      bStuff,
      id
    )
  }

  public override fun <T : Any> selectById(id: Long, mapper: (bStuff: String, id: Long) -> T):
      Query<T> = SelectByIdQuery(id) { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getLong(1)!!
    )
  }

  public override fun selectById(id: Long): Query<TableB> = selectById(id) { bStuff, id_ ->
    TableB(
      bStuff,
      id_
    )
  }

  public override fun insertB(TableB: TableB): Unit {
    driver.execute(-1966394796, """INSERT OR REPLACE INTO TableB(id, bStuff) VALUES (?, ?)""", 2) {
      bindLong(1, TableB.id)
      bindString(2, TableB.bStuff)
    }
    notifyQueries(-1966394796, {database.tableBQueries.selectAll +
        database.tableBQueries.selectById})
  }

  private inner class SelectByIdQuery<out T : Any>(
    public val id: Long,
    mapper: (SqlCursor) -> T
  ) : Query<T>(selectById, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(-135453341,
        """SELECT * FROM TableB WHERE id = ?""", 1) {
      bindLong(1, id)
    }

    public override fun toString(): String = "TableB.sq:selectById"
  }
}

private class TableAQueriesImpl(
  private val database: DatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), TableAQueries {
  internal val selectAll: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> selectAll(mapper: (
    name: String,
    number: Long,
    bId: Long
  ) -> T): Query<T> = Query(-745738159, selectAll, driver, "TableA.sq", "selectAll",
      "SELECT * FROM TableA") { cursor ->
    mapper(
      cursor.getString(0)!!,
      cursor.getLong(1)!!,
      cursor.getLong(2)!!
    )
  }

  public override fun selectAll(): Query<TableA> = selectAll { name, number, bId ->
    TableA(
      name,
      number,
      bId
    )
  }

  public override fun insertA(TableA: TableA): Unit {
    driver.execute(-2095477516,
        """INSERT OR REPLACE INTO TableA(name, number, bId) VALUES (?, ?, ?)""", 3) {
      bindString(1, TableA.name)
      bindLong(2, TableA.number)
      bindLong(3, TableA.bId)
    }
    notifyQueries(-2095477516, {database.tableAQueries.selectAll})
  }
}
