package com.github.fpopic.bigqueryschemaselect

import org.scalatest.flatspec.AnyFlatSpec

import scala.io.Source
import scala.util.{Success, Try}

class bigqueryschemaselectTest extends AnyFlatSpec {

  behavior of "bigqueryschemaselect"

  it should "generate correct BigQuery Select clause string out of schema file." in {
    val inputSchema = Source.fromResource("my_schema.json").mkString
    val expectedOutput = Source.fromResource("my_select.sql").mkString
    val actualOutput: Try[String] = generateBigQuerySelectClause(inputSchema)

    expectedOutput == actualOutput.get
  }

}
