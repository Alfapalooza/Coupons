package services

import models.Databar
import models.coupon.{Coupon, CouponRequirement, Family, SaveDetails, _}
import models.gs1databar.{CouponData, QualifyingPurchase}
import persistence.Persistence

import scala.concurrent.{ExecutionContext, Future}

class CouponService(persistence: Persistence)(implicit ec: ExecutionContext) {
  def computeCouponDatabarToCouponData(databar: Databar): CouponData =
    CouponData(databar)

  def computeCouponDatabarToCoupon(databar: Databar): Future[Option[Coupon]] =
    computeCouponDataToCoupon(databar, computeCouponDatabarToCouponData(databar))

  def computeCouponDataToCoupon(databar: Databar, couponData: CouponData): Future[Option[Coupon]] = {
    val allowMultipleOffers = getAllowMultipleOffers(couponData)
    val associateIntervention = getAssociateIntervention(couponData)
    val expiryDate = couponData.expirationDate
    val dataBarId = databar
    val offerCode = couponData.offerCode
    val purchaseRule = couponData.secondaryQualifyingPurchase.flatMap(x => x.purchaseRulesCode.map(PurchaseRule(_))) //getPurchaseRule(dataBarData)
    val primaryReq = getPrimaryRequirement(couponData)
    val secondaryReqOpt =
      getExtraRequirement(couponData.primaryGS1CompanyPrefix, couponData.secondaryQualifyingPurchase)
    val tertiaryReqOpt = getExtraRequirement(couponData.primaryGS1CompanyPrefix, couponData.tertiaryQualifyingPurchase)
    val retailerPrefix = couponData.retailerId
    val saveDetails = getSaveDetails(couponData)
    val startDate = couponData.startDate
    val serial = couponData.serialNumber
    val storeCoupon = couponData.miscElements.map(_.storeCouponFlag.toInt)

    for {
      primaryFamilyOpt <- getFamily(couponData.primaryGS1CompanyPrefix, couponData.primaryPurchaseFamilyCode)
      addlFamilies <- getFamilies(Seq(secondaryReqOpt, tertiaryReqOpt).flatten: _*)
    } yield {
      primaryFamilyOpt.map { primaryFamily =>
        Coupon(
          allowMultipleOffers,
          associateIntervention,
          primaryFamily.couponName,
          expiryDate,
          primaryFamily +: addlFamilies,
          dataBarId,
          primaryFamily.imageUrl,
          offerCode,
          purchaseRule,
          primaryReq,
          secondaryReqOpt,
          tertiaryReqOpt,
          retailerPrefix,
          saveDetails,
          startDate,
          serial,
          storeCoupon
        )
      }
    }
  }

  private def getAllowMultipleOffers(dataBarData: CouponData): Boolean = {
    dataBarData.miscElements match {
      case Some(value) => if (value.dontMutiplyFlag == "1") false else true
      case None => true //default if Data field 9 is missing
    }
  }

  private def getAssociateIntervention(dataBarData: CouponData): Boolean = {
    val interventionCode = "9"
    val primaryAssociateIntervention = dataBarData.primaryPurchaseRequirementCode == interventionCode
    val secondaryAssociateIntervention = dataBarData.secondaryQualifyingPurchase match {
      case Some(value) if value.purchaseRequirementCode == interventionCode => true
      case _ => false
    }
    val tertiaryAssociateIntervention = dataBarData.tertiaryQualifyingPurchase match {
      case Some(value) if value.purchaseRequirementCode == interventionCode => true
      case _ => false
    }

    primaryAssociateIntervention || secondaryAssociateIntervention || tertiaryAssociateIntervention
  }

  private def getPrimaryRequirement(dataBarData: CouponData): CouponRequirement =
    CouponRequirement(
      dataBarData.primaryGS1CompanyPrefix,
      dataBarData.primaryPurchaseFamilyCode,
      dataBarData.primaryPurchaseRequirementCode,
      dataBarData.primaryPurchaseRequirement
    )

  private def getExtraRequirement(
    primaryCompanyPrefix: Long,
    requirement: Option[QualifyingPurchase]
  ): Option[CouponRequirement] = {
    requirement.map { value =>
      CouponRequirement(
        primaryCompanyPrefix,
        value.gS1CompanyPrefix,
        value.purchaseFamilyCode,
        value.purchaseRequirementCode,
        value.purchaseRequirement
      )
    }
  }

  private def getSaveDetails(dataBarData: CouponData): SaveDetails = {
    SaveDetails(
      dataBarData.saveValue,
      dataBarData.miscElements.map(_.saveValueCode),
      dataBarData.miscElements.map(_.saveValueApply)
    )
  }

  private def getFamily(companyPrefix: Long, familyCode: Long): Future[Option[Family]] = {
    persistence.familyDal.getFamilyUPCSByCompanyPrefixAndFamily(companyPrefix, familyCode).map(_.map { familyUpcs =>
      val upcs = familyUpcs.upcs.map { upc =>
        UPC.apply(upc.upc, upc.name)
      }
      Family.apply(
        familyUpcs.familyCodeRow.couponName,
        familyUpcs.familyCodeRow.imageUrl,
        companyPrefix.toString,
        familyCode.toString,
        upcs)
    })
  }

  private def getFamilies(requirements: CouponRequirement*): Future[Seq[Family]] = {
    Future.sequence(requirements.map(req => getFamily(req.companyPrefix, req.familyCode))).map(_.flatten)
  }
}
