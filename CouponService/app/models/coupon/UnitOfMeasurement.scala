package models.coupon

import play.api.libs.json._

abstract class UnitOfMeasurement(code: Int, value: String)
case class RequirementUOM(code: Int, value: String) extends UnitOfMeasurement(code,value)

object RequirementUOM{
  implicit val wr = new Writes[RequirementUOM]{
    override def writes(o: RequirementUOM): JsValue = JsString(o.value)
  }

  def codeToValueMap(code: String): String =
    code match {
      case "0" => "units"
      case "1" => "totalQualifyingItem"
      case "2" => "totalTransaction"
      case "3" => "lbs"
      case "4" => "kilogram"
      case "9" => "cashier"
    }

  def apply(code: String): RequirementUOM = RequirementUOM(code.toInt, codeToValueMap(code))
}

case class SaveUOM(code: Int, value: String) extends UnitOfMeasurement(code, value)

object SaveUOM{
  implicit val wr = new Writes[SaveUOM]{
    override def writes(o: SaveUOM): JsValue = JsString(o.value)
  }

  def apply(code: Option[String], saveValue: String): SaveUOM = {
    SaveUOM(code.map(_.toInt).getOrElse(0), codeToValue(code.getOrElse("0"),saveValue))
  }

  def codeToValue(code: String, saveValue: String): String = {
    if (code == "1") if (saveValue == "0") codeToValueMap("2") else codeToValueMap("0")
    else codeToValueMap(code)
  }

  def codeToValueMap(code: String) : String =
    code match {
      case "0" => "cents"
      case "2" => "free"
      case "5" => "percentage"
      case "6" => "centsOffBasket"
    }
}
