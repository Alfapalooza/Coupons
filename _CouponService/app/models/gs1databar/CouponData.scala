package models.gs1databar

import models.Databar
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

import scala.util.{Failure, Success, Try}

case class CouponData(
  appId: String,
  primaryGS1CompanyPrefix: Long,
  offerCode: String,
  saveValue: String,
  primaryPurchaseRequirement: String,
  primaryPurchaseRequirementCode: String,
  primaryPurchaseFamilyCode: Long,
  secondaryQualifyingPurchase: Option[QualifyingPurchase] = None,
  tertiaryQualifyingPurchase: Option[QualifyingPurchase] = None,
  expirationDate: Option[LocalDate] = None,
  startDate: Option[LocalDate] = None,
  serialNumber: Option[String] = None,
  retailerId: Option[String] = None,
  miscElements: Option[MiscElements] = None
)

case class QualifyingPurchase(
  purchaseRulesCode: Option[String],
  purchaseRequirement: String,
  purchaseRequirementCode: String,
  purchaseFamilyCode: Long,
  gS1CompanyPrefix: Option[Long]
)

case class MiscElements(
  saveValueCode: String,
  saveValueApply: String,
  storeCouponFlag: String,
  dontMutiplyFlag: String
)

object CouponData {
  private val gcpVLIOffset = 6
  private val serialVLIOffset = 6
  private val vli = 1

  def apply(databar: Databar): CouponData = {
    parseDataBar(databar)
  }

  private def parseDataBar(databar: Databar): CouponData = {
    var tuple = databar.splitAt(4)
    val appId = tuple._1

    tuple = tuple._2.splitAt(vli)
    val primaryGCPVLI = tuple._1

    tuple = tuple._2.splitAt(primaryGCPVLI.toInt + gcpVLIOffset)
    val primaryGCP = tuple._1.toLong

    tuple = tuple._2.splitAt(6)
    val offerCode = tuple._1

    tuple = tuple._2.splitAt(vli)
    val saveValueVLI = tuple._1

    tuple = tuple._2.splitAt(saveValueVLI.toInt)
    val saveValue = tuple._1

    tuple = tuple._2.splitAt(vli)
    val primaryPRVLI = tuple._1

    tuple = tuple._2.splitAt(primaryPRVLI.toInt)
    val primaryPR = tuple._1

    tuple = tuple._2.splitAt(1)
    val primaryPRCode = tuple._1

    tuple = tuple._2.splitAt(3)
    val primaryPurchaseFamilyCode = tuple._1.toLong

    tuple = tuple._2.splitAt(1)

    var couponData = new CouponData(
      appId,
      primaryGCP,
      offerCode,
      saveValue,
      primaryPR,
      primaryPRCode,
      primaryPurchaseFamilyCode
    )

    if (tuple._1 == "1") {
      val (secondaryQP, t) = parseSecondQualifyingPurchase(tuple._2)

      couponData = couponData.copy(secondaryQualifyingPurchase = Some(secondaryQP))

      tuple = t.splitAt(1)
    }
    if (tuple._1 == "2") {
      val (tertiaryQP, t) = parseTertiaryQualifyingPurchase(tuple._2)

      couponData = couponData.copy(tertiaryQualifyingPurchase = Some(tertiaryQP))

      tuple = t.splitAt(1)
    }
    if (tuple._1 == "3") {
      tuple = tuple._2.splitAt(6)
      val expirationDate = parseDateFromDataBar(tuple._1)

      couponData = couponData.copy(expirationDate = expirationDate)

      tuple = tuple._2.splitAt(1)
    }
    if (tuple._1 == "4") {
      tuple = tuple._2.splitAt(6)
      val startDate = parseDateFromDataBar(tuple._1)

      couponData = couponData.copy(startDate = startDate)

      tuple = tuple._2.splitAt(1)
    }
    if (tuple._1 == "5") {
      tuple = tuple._2.splitAt(1)
      val serialNumberVLI = tuple._1

      tuple = tuple._2.splitAt(serialNumberVLI.toInt + serialVLIOffset)
      val serialNumber = tuple._1

      couponData = couponData.copy(serialNumber = Some(serialNumber))

      tuple = tuple._2.splitAt(1)
    }
    if (tuple._1 == "6") {
      tuple = tuple._2.splitAt(1)
      val retailerIdVLI = tuple._1

      tuple = tuple._2.splitAt(retailerIdVLI.toInt)
      val retailerId = tuple._1

      couponData = couponData.copy(retailerId = Some(retailerId))

      tuple = tuple._2.splitAt(1)
    }
    if (tuple._1 == "9") {
      tuple = tuple._2.splitAt(1)
      val saveValueCode = tuple._1

      tuple = tuple._2.splitAt(1)
      val saveValueApply = tuple._1

      tuple = tuple._2.splitAt(1)
      val storeCouponFlag = tuple._1

      tuple = tuple._2.splitAt(1)
      val dontMultiplyFlag = tuple._1

      val miscelements = MiscElements(saveValueCode, saveValueApply, storeCouponFlag, dontMultiplyFlag)

      couponData = couponData.copy(miscElements = Some(miscelements))
    }

    couponData
  }

  private def parseSecondQualifyingPurchase(value: String): (QualifyingPurchase, String) = {
    var tuple = value.splitAt(1)
    val purchaseRuleCode = tuple._1

    tuple = tuple._2.splitAt(1)
    val purchaseRequirementVLI = tuple._1

    tuple = tuple._2.splitAt(purchaseRequirementVLI.toInt)
    val purchaseRequirement = tuple._1

    tuple = tuple._2.splitAt(1)
    val purchaseRequirementCode = tuple._1

    tuple = tuple._2.splitAt(3)
    val purchaseFamilyCode = tuple._1.toLong

    tuple = tuple._2.splitAt(vli)
    val gs1CompayPrefixVLI = tuple._1

    val gs1CompanyPrefix =
      (if (tuple._1 == "9") None
      else {
        tuple = tuple._2.splitAt(gs1CompayPrefixVLI.toInt + gcpVLIOffset)
        Some(tuple._1)
      }).map(_.toLong)

    val secondaryQP = QualifyingPurchase(
      Some(purchaseRuleCode),
      purchaseRequirement,
      purchaseRequirementCode,
      purchaseFamilyCode,
      gs1CompanyPrefix
    )

    (secondaryQP, tuple._2)
  }

  private def parseTertiaryQualifyingPurchase(value: String): (QualifyingPurchase, String) = {
    var tuple = value.splitAt(1)
    val purchaseRequirementVLI = tuple._1

    tuple = tuple._2.splitAt(purchaseRequirementVLI.toInt)
    val purchaseRequirement = tuple._1

    tuple = tuple._2.splitAt(1)
    val purchaseRequirementCode = tuple._1

    tuple = tuple._2.splitAt(3)
    val purchaseFamilyCode = tuple._1.toLong

    tuple = tuple._2.splitAt(vli)
    val gs1CompayPrefixVLI = tuple._1

    val gs1CompanyPrefix =
      (if (tuple._1 == "9") None
      else {
        tuple = tuple._2.splitAt(gs1CompayPrefixVLI.toInt + gcpVLIOffset)
        Some(tuple._1)
      }).map(_.toLong)

    val tertiaryQP =
      QualifyingPurchase(None, purchaseRequirement, purchaseRequirementCode, purchaseFamilyCode, gs1CompanyPrefix)

    (tertiaryQP, tuple._2)
  }

  private def parseDateFromDataBar(value: String): Option[LocalDate] = {
    val formatter = DateTimeFormat.forPattern("yyMMdd")
    Try {
      LocalDate.parse(value, formatter)
    } match {
      case Success(result) => Some(result)
      case Failure(_) => None
    }
  }
}
