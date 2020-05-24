package com.github.fpopic.bigqueryschemaselect

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{Reads, _}

private[bigqueryschemaselect] final case class BigQuerySchemaField(
    name: String,
    `type`: String,
    mode: Option[String],
    description: Option[String],
    fields: Option[Seq[BigQuerySchemaField]]
) {
  def isRepeated: Boolean = mode.contains("REPEATED")

  def isRecord: Boolean = `type` == "RECORD"

  def isPrimitive: Boolean = !isRecord
}

// TODO figure out how to avoid type error if I put these implicits in companion object (some trick with [.type]
private[bigqueryschemaselect] object BigQuerySchemaField2 {

  implicit lazy val readsSeqBigQuerySchemaField: Reads[Seq[BigQuerySchemaField]] =
    Reads.seq[BigQuerySchemaField](readsBigQuerySchemaField)

  implicit lazy val readsBigQuerySchemaField: Reads[BigQuerySchemaField] = (
    (__ \ "name").read[String] and
      (__ \ "type").read[String] and
      (__ \ "mode").readNullable[String] and
      (__ \ "description").readNullable[String] and
      (__ \ "fields").lazyReadNullable(readsSeqBigQuerySchemaField)
  )(BigQuerySchemaField)

}
