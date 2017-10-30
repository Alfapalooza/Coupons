package models.coupon

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class CouponRequirement(companyPrefix: Long,
                             familyCode: Long,
                             measurementUnit: RequirementUOM,
                             threshold: String)

object CouponRequirement{
  implicit val wr: Writes[CouponRequirement] = new Writes[CouponRequirement] {
    override def writes(o: CouponRequirement): JsValue = Json.obj(
      "companyPrefix" -> o.companyPrefix.toString,
      "familyCode" -> o.familyCode.toString,
      "measurementUnit" -> o.measurementUnit,
      "threshold" -> o.threshold
    )
  }

  def apply(companyPrefix: Long, familyCode: Long, uomCode: String , threshold: String): CouponRequirement =
    CouponRequirement(companyPrefix, familyCode, RequirementUOM(uomCode), threshold)

  def apply(primaryCompanyPrefix: Long, companyPrefix: Option[Long], familyCode: Long, uomCode: String , threshold: String): CouponRequirement = {
    val prefix = companyPrefix.getOrElse(primaryCompanyPrefix)
    apply(prefix, familyCode, uomCode, threshold)
  }
}