package models.coupon

import play.api.libs.json.{JsPath, Writes}
import play.api.libs.functional.syntax._

case class UPC(upc: String, name: String)

object UPC {
  implicit val wr: Writes[UPC] = (
    (JsPath \ "id").write[String] and
    (JsPath \ "name").write[String]
  )(unlift(UPC.unapply))
}
