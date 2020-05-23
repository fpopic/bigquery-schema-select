import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json.{Reads, _}

object Main {

  val input =
    """
      |[
      |  {
      |    "name": "A",
      |    "type": "TIMESTAMP"
      |  },
      |  {
      |    "name": "B",
      |    "type": "RECORD",
      |    "fields": [
      |      {
      |        "name": "B1",
      |        "type": "TIMESTAMP"
      |      }
      |    ]
      |  },
      |  {
      |    "name": "C",
      |    "type": "RECORD",
      |    "fields": [
      |      {
      |        "name": "C1",
      |        "type": "TIMESTAMP"
      |      },
      |	     {
      |        "name": "C2",
      |        "type": "TIMESTAMP"
      |      }
      |    ]
      |  },
      |  {
      |    "name": "D",
      |    "type": "RECORD",
      |    "mode": "REPEATED",
      |    "fields": [
      |      {
      |        "name": "D1",
      |        "type": "TIMESTAMP"
      |      },
      |	     {
      |        "name": "D2",
      |        "type": "TIMESTAMP"
      |      }
      |    ]
      |  },
      |  {
      |    "name": "E",
      |    "type": "TIMESTAMP",
      |    "mode": "REPEATED"
      |  }
      |]""".stripMargin

  final case class Field(
    name: String,
    `type`: String,
    mode: Option[String],
    description: Option[String],
    fields: Option[Seq[Field]]
  ) {
    def isRepeated: Boolean = mode.contains("REPEATED")

    def isRecord: Boolean = `type` == "RECORD"

    def isPrimitive: Boolean = !isRecord
  }

  implicit lazy val readsField: Reads[Field] = (
    (__ \ "name").read[String] and
      (__ \ "type").read[String] and
      (__ \ "mode").readNullable[String] and
      (__ \ "description").readNullable[String] and
      (__ \ "fields").lazyReadNullable(Reads.seq[Field](readsField))
    ) (Field)

  def rInit(fs: Seq[Field]): Unit = {
    println("SELECT")
    fs.foreach(r(_, depth = 1))
  }

  def printWithDepth(depth: Int, str: String) = println("  " * depth + str)

  def r(current: Field, depth: Int, parent: Option[Field] = None): Unit = {
    if (current.isPrimitive) {
      if (parent.isEmpty)
        printWithDepth(depth, current.name)
      else
        printWithDepth(depth, (s"${parent.get.name}.${current.name}"))
    }
    else if (current.isRecord) {
      if (current.isRepeated) {
        printWithDepth(depth, "ARRAY(")
        printWithDepth(depth, "  SELECT")
        printWithDepth(depth, "    STRUCT(")
        current.fields.get.foreach(r(_, depth + 3, Some(current)))
        printWithDepth(depth, s"    ) AS ${current.name}")
        printWithDepth(depth, "  FROM")
        printWithDepth(depth, s"    UNNEST(${current.name}) AS ${current.name}")
        printWithDepth(depth, "    WITH OFFSET o")
        printWithDepth(depth, "  ORDER BY")
        printWithDepth(depth, "    o")
        printWithDepth(depth, s") AS ${current.name}")
      }
      else {
        printWithDepth(depth, "STRUCT(")
        current.fields.get.foreach(r(_, depth + 1, Some(current)))
        printWithDepth(depth, s") AS ${current.name}")
      }
    }
  }


  def main(args: Array[String]): Unit = {
    import play.api.libs.json._

    Json.parse(input).validate[Seq[Field]] match {
      case JsSuccess(fields: Seq[Field], path: JsPath) => rInit(fields)
      case e: JsError =>
        println("Errors: " + JsError.toFlatForm(e).toString())
    }
  }
}
