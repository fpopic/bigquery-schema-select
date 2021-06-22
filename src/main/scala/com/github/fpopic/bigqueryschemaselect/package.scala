package com.github.fpopic

import play.api.libs.json.{JsError, JsSuccess, Json}

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Try}

package object bigqueryschemaselect {

  private val nl: String = System.lineSeparator()

  private def calculateFieldName(name: String, useSnakeCase: Boolean): String = {
    if (!useSnakeCase) name
    else {
      val word = ListBuffer.empty[Char]
      word += name.head.toLower
      name.tail.toCharArray.foreach { char =>
        if (char.isUpper) {
          word += '_'
        }
        word += char.toLower
      }
      word.result.mkString
    }
  }

  private def toSelectClauseRecursive(
      current: BigQuerySchemaField,
      depth: Int,
      sb: StringBuilder,
      prefix: Option[String],
      useSnakeCase: Boolean
  ): String = {
    val fullyQualifiedName = prefix match {
      case None       => current.name
      case Some(pref) => s"${pref}.${current.name}"
    }
    val indent = "  " * depth
    // PRIMTIVE (SCALAR, ARRAY)
    if (current.isPrimitive) {
      val result =
        s"${fullyQualifiedName}${
          val calculatedFieldName = calculateFieldName(current.name, useSnakeCase)
          if (useSnakeCase && calculatedFieldName != current.name)
            s" AS ${calculatedFieldName}"
          else
            ""
        }"
      sb.append(s"${indent}${result}").toString()
    }
    // RECORD REPEATED
    else if (current.isRecord && current.isRepeated) {
      val start =
        s"""|ARRAY(
            |  SELECT AS STRUCT""".replace("|", s"|$indent").stripMargin
      val end =
        s"""
           |  FROM
           |    UNNEST(${fullyQualifiedName}) AS ${current.name}
           |  WITH
           |    OFFSET
           |  ORDER BY
           |    OFFSET
           |) AS ${calculateFieldName(current.name, useSnakeCase)}""".replace("|", s"|$indent").stripMargin
      val result = current.fields.get
        .map(child => toSelectClauseRecursive(child, depth + 2, new StringBuilder(), Some(current.name), useSnakeCase))
        .mkString(start = s"${start}${nl}", sep = s",${nl}", end = end)
      new StringBuilder().append(result).toString()
    }
    //  RECORD SCALAR
    else {
      val start =
        s"""|STRUCT(""".replace("|", s"|$indent").stripMargin
      val end =
        s"""
           |) AS ${calculateFieldName(current.name, useSnakeCase)}""".replace("|", s"|$indent").stripMargin
      val result = current.fields.get
        .map(child =>
          toSelectClauseRecursive(child, depth + 1, new StringBuilder(), Some(fullyQualifiedName), useSnakeCase)
        )
        .mkString(s"${start}${nl}", s",${nl}", end)
      new StringBuilder().append(result).toString()
    }
  }

  private def toSelectClause(fields: Seq[BigQuerySchemaField], useSnakeCase: Boolean): String = {
    val result = fields
      .map(field => toSelectClauseRecursive(field, depth = 1, sb = new StringBuilder(), prefix = None, useSnakeCase))
      .mkString(s"SELECT${nl}", s",${nl}", "")
    result
  }

  def generateBigQuerySelectClause(schemaString: String, useSnakeCase: Boolean): Try[String] = {
    Json.parse(schemaString).validate(BigQuerySchemaField2.readsSeqBigQuerySchemaField) match {
      case JsSuccess(fields, _) => Try(toSelectClause(fields, useSnakeCase))
      case e: JsError           => Failure(new RuntimeException(JsError.toFlatForm(e).toString()))
    }
  }

}
