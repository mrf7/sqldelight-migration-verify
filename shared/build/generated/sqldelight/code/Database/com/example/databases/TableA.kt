package com.example.databases

import kotlin.Long
import kotlin.String

public data class TableA(
  public val name: String,
  public val number: Long,
  public val bId: Long
) {
  public override fun toString(): String = """
  |TableA [
  |  name: $name
  |  number: $number
  |  bId: $bId
  |]
  """.trimMargin()
}
