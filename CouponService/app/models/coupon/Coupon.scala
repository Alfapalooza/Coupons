package models.coupon

import models.Databar
import org.joda.time.LocalDate
import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Coupon(
  allowMultipleOffers: Boolean,
  associateIntervention: Boolean,
  couponProvider: String,
  expiryDate: Option[LocalDate],
  families: Seq[Family],
  dataBarId: Databar,
  image: String,
  offerCode: String,
  purchaseRule: Option[PurchaseRule],
  primaryRequirement: CouponRequirement,
  secondaryRequirement: Option[CouponRequirement],
  tertiaryRequirement: Option[CouponRequirement],
  retailerPrefix: Option[String],
  saveDetails: SaveDetails,
  startDate: Option[LocalDate],
  serial: Option[String],
  storeCoupon: Option[Int]
)

object Coupon {
  private val pattern = "yyyy-MM-dd"

  implicit val dateFormat: Format[LocalDate] =
    Format[LocalDate](Reads.jodaLocalDateReads(pattern), Writes.jodaLocalDateWrites(pattern))
  
  implicit val wr: Writes[Coupon] = (
    (JsPath \ "allowMultipleOffers").write[Boolean] and
      (JsPath \ "associateIntervention").write[Boolean] and
      (JsPath \ "couponProvider").write[String] and
      (JsPath \ "expiryDate").writeNullable[LocalDate] and
      (JsPath \ "families").write[Seq[Family]] and
      (JsPath \ "databar").write[String] and
      (JsPath \ "image").write[String] and
      (JsPath \ "offerCode").write[String] and
      (JsPath \ "purchaseRule").writeNullable[PurchaseRule] and
      (JsPath \ "primaryRequirement").write[CouponRequirement] and
      (JsPath \ "secondaryRequirement").writeNullable[CouponRequirement] and
      (JsPath \ "tertiaryRequirement").writeNullable[CouponRequirement] and
      (JsPath \ "retailerPrefix").writeNullable[String] and
      (JsPath \ "save").write[SaveDetails] and
      (JsPath \ "startDate").writeNullable[LocalDate] and
      (JsPath \ "serial").writeNullable[String] and
      (JsPath \ "storeCoupon").writeNullable[Int]
  )(unlift(Coupon.unapply))
}
