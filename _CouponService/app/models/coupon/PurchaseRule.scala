package models.coupon

import play.api.libs.json._

case class PurchaseRule(code: Int, value: String)

object PurchaseRule {
  implicit val wr: Writes[PurchaseRule] = new Writes[PurchaseRule] {
    override def writes(o: PurchaseRule): JsValue = JsString(o.value)
  }

  private def codeToValue(code: String) =
    code match {
      case "0" => "or_or"
      case "1" => "and_and"
      case "2" => "and_or"
      case "3" => "meet_first_requirement"
    }

  def apply(code: String): PurchaseRule = PurchaseRule(code.toInt, PurchaseRule.codeToValue(code))
}