package models.coupon

import play.api.libs.functional.syntax._
import play.api.libs.json._

import scala.math.BigDecimal.RoundingMode

case class SaveDetails(amount: Double, measurementUnit: SaveUOM, whichItem: Int)

object SaveDetails {
  val decimalWrites = new Writes[BigDecimal]{
    def writes(o: BigDecimal): JsValue = JsNumber(o.setScale(2, RoundingMode.HALF_UP))
  }

  implicit val wr: Writes[SaveDetails] =
    ((JsPath \ "amount").write[Double] and
      (JsPath \ "measurementUnit").write[SaveUOM] and
      (JsPath \ "whichItem").write[Int]
      )(unlift(SaveDetails.unapply))



  def apply(amount: String, uomCode: Option[String], whichItem: Option[String]): SaveDetails = {
    val uom = SaveUOM(uomCode, amount)
    val saveAmount = if (amount == "0" && uom.code == 1) 1 else amount.toDouble
    val which = whichItem.map(_.toInt + 1).getOrElse(1)

    SaveDetails(saveAmount, uom, which)
  }
}
