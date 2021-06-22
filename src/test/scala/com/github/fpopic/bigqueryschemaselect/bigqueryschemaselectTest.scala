package com.github.fpopic.bigqueryschemaselect

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Source
import scala.util.Try

class bigqueryschemaselectTest extends AnyFlatSpec with Matchers {

  behavior of "bigqueryschemaselect"

  it should "generate correct BigQuery Select clause string out of schema file (long)." in {
    val inputSchema = Source.fromResource("my_schema.json").mkString
    val expectedOutput = Source.fromResource("my_select.sql").mkString
    val actualOutput: Try[String] = generateBigQuerySelectClause(inputSchema, useSnakeCase = false)

    actualOutput.get.trim shouldBe expectedOutput.trim
  }

  it should "generate correct BigQuery Select clause string out of schema file using snake cased field names (long)." in {
    val inputSchema = Source.fromResource("my_camel_schema.json").mkString
    val expectedOutput = Source.fromResource("my_camel_select.sql").mkString
    val actualOutput: Try[String] = generateBigQuerySelectClause(inputSchema, useSnakeCase = true)

    actualOutput.get.trim shouldBe expectedOutput.trim
  }

  it should "generate correct BigQuery Select clause string out of schema file using snake cased field names (short)." in {
    val inputSchema = Source.fromResource("my_camel_short_schema.json").mkString
    val expectedOutput = Source.fromResource("my_camel_short_select.sql").mkString
    val actualOutput: Try[String] = generateBigQuerySelectClause(inputSchema, useSnakeCase = true)

    actualOutput.get.trim shouldBe expectedOutput.trim
  }

}
