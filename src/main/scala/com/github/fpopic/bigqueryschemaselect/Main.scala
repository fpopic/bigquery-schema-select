package com.github.fpopic.bigqueryschemaselect

import scala.io.Source
import scala.util.{Failure, Success, Try}

object Main {

  def main(args: Array[String]): Unit = {
    val useSnakeCase = args.nonEmpty && args(0).trim == "--use_snake_case"
    val input = Source.fromInputStream(System.in).getLines().mkString
    val tryOutput: Try[String] = generateBigQuerySelectClause(input, useSnakeCase)
    tryOutput match {
      case Failure(exception)          => throw exception
      case Success(selectClauseString) => println(selectClauseString)
    }
  }
}
