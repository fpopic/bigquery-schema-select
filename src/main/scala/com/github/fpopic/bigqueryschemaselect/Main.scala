package com.github.fpopic.bigqueryschemaselect

import scala.io.Source
import scala.util.{Failure, Success, Try}

object Main {

  def main(args: Array[String]): Unit = {
    val input = Source.fromInputStream(System.in).getLines().mkString
    val tryOutput: Try[String] = generateBigQuerySelectClause(input)
    tryOutput match {
      case Failure(exception)          => throw exception
      case Success(selectClauseString) => println(selectClauseString)
    }
  }
}
