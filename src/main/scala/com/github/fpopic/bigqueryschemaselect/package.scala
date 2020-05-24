package com.github.fpopic

import play.api.libs.json.{JsError, JsSuccess, Json}

import scala.util.{Failure, Try}

package object bigqueryschemaselect {

  private val nl: String = System.lineSeparator()

  private def toSelectClauseRecursive(
      current: BigQuerySchemaField,
      depth: Int,
      sb: StringBuilder,
      prefix: Option[String]
  ): String = {
    val fullyQualifiedName = prefix match {
      case None       => current.name
      case Some(pref) => s"${pref}.${current.name}"
    }
    val indent = "  " * depth
    // PRIMTIVE (SCALAR, ARRAY)
    if (current.isPrimitive) {
      val result = s"${fullyQualifiedName}"
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
           |    WITH OFFSET AS offset
           |  ORDER BY
           |    offset
           |) AS ${current.name}""".replace("|", s"|$indent").stripMargin
      val result = current.fields.get
        .map(child => toSelectClauseRecursive(child, depth + 2, new StringBuilder(), Some(current.name)))
        .mkString(s"${start}${nl}", s",${nl}", end)
      new StringBuilder().append(result).toString()
    }
    //  RECORD SCALAR
    else {
      val start =
        s"""|STRUCT(""".replace("|", s"|$indent").stripMargin
      val end =
        s"""
           |) AS ${current.name}""".replace("|", s"|$indent").stripMargin
      val result = current.fields.get
        .map(child => toSelectClauseRecursive(child, depth + 1, new StringBuilder(), Some(fullyQualifiedName)))
        .mkString(s"${start}${nl}", s",${nl}", end)
      new StringBuilder().append(result).toString()
    }
  }

  private def toSelectClause(fields: Seq[BigQuerySchemaField]): String = {
    val result = fields
      .map(field => toSelectClauseRecursive(field, depth = 1, sb = new StringBuilder(), prefix = None))
      .mkString(s"SELECT${nl}", s",${nl}", "")
    result
  }

  def generateBigQuerySelectClause(schemaString: String): Try[String] = {
    Json.parse(schemaString).validate(BigQuerySchemaField2.readsSeqBigQuerySchemaField) match {
      case JsSuccess(fields, _) => Try(toSelectClause(fields))
      case e: JsError           => Failure(new RuntimeException(JsError.toFlatForm(e).toString()))
    }
  }

}
