package com.example.databases

import kotlin.Long
import kotlin.String

public data class TableB(
  public val bStuff: String,
  public val id: Long
) {
  public override fun toString(): String = """
  |TableB [
  |  bStuff: $bStuff
  |  id: $id
  |]
  """.trimMargin()
}
