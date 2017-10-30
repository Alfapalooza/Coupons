package models.coupon

import play.api.libs.json._


case class Family(couponName: String,
                  imageUrl: String,
                  companyPrefix: String,
                  familyCode: String,
                  upcList: Set[UPC])

object Family {
  implicit val wr = new Writes[Family] {
    def writes(fam: Family): JsValue = {
      Json.obj(fam.companyPrefix + "-" + fam.familyCode -> Json.obj("upcs" -> fam.upcList ))
    }
  }
}
