import scala.scalajs.js
import scala.scalajs.js.Dynamic.global
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.{Date, _}
import scala.scalajs.js.annotation._

@JSExportTopLevel("RuleEngine")
object RuleEngine {
  val cca = new CouponCartAssistant()

  @ScalaJSDefined
  trait LocationJS extends js.Object {
    val city: String
    val postalCode: String
    val stateOrProvinceCode: String
  }

  case class Location(city: String,
                      postalCode: String,
                      state: String)

  object Location {
    implicit class LocationOps(val self: Location) extends AnyVal {
      def toLocationJS: LocationJS = new LocationJS {
        override val city: String = self.city
        override val postalCode: String = self.postalCode
        override val stateOrProvinceCode: String = self.state
      }
    }
  }

  object LocationJS {
    implicit class LocationJSOps(val self: LocationJS) extends AnyVal {
      def toLocation(log: Boolean = false): Location = {
        if (log) global.console.log(s"Scala: toLocation(self: ${JSON.stringify(self)})")
        Location(
          self.city,
          self.postalCode,
          self.stateOrProvinceCode
        )
      }
    }
  }

  @ScalaJSDefined
  trait TotalsJS extends js.Object {
    val grandTotal: Double
    val subTotal: Double
    val taxTotal: Double
    val discount: UndefOr[Double] = js.undefined
  }

  case class Totals(grandTotal: Double,
                    subTotal: Double,
                    taxTotal: Double,
                    discount: Option[Double])

  object Totals {
    implicit class TotalsOps(val self: Totals) extends AnyVal {
      def toTotalsJS: TotalsJS = new TotalsJS {
        override val grandTotal: Double = self.grandTotal
        override val taxTotal: Double = self.taxTotal
        override val discount: UndefOr[Double] = self.discount.orUndefined
        override val subTotal: Double = self.subTotal
      }
    }
  }

  object TotalsJS {
    implicit class TotalsJSOps(val self: TotalsJS) extends AnyVal {
      def toTotals(log: Boolean = false): Totals = {
        if (log) global.console.log(s"Scala: toTotals(self: ${JSON.stringify(self)})")
        Totals(
          self.grandTotal,
          self.subTotal,
          self.taxTotal,
          self.discount.toOption
        )
      }
    }
  }

  @ScalaJSDefined
  trait ComparisonPriceJS extends js.Object {
    val hidePriceForSOI: Boolean
    val isClearance: Boolean
    val isEligibleForAssociateDiscount: Boolean
    val isReducedPrice: Boolean
    val isRollback: Boolean
    val isStrikethrough: Boolean
  }

  case class ComparisonPrice(hidePriceForSOI: Boolean,
                             isClearance: Boolean,
                             isEligibleForAssociateDiscount: Boolean,
                             isReducedPrice: Boolean,
                             isRollback: Boolean,
                             isStrikethrough: Boolean)

  object ComparisonPrice {
    implicit class CPOps(val self: ComparisonPrice) extends AnyVal {
      def toComparisonPriceJS: ComparisonPriceJS = new ComparisonPriceJS {
        override val hidePriceForSOI: Boolean = self.hidePriceForSOI
        override val isReducedPrice: Boolean = self.isReducedPrice
        override val isStrikethrough: Boolean = self.isStrikethrough
        override val isClearance: Boolean = self.isClearance
        override val isRollback: Boolean = self.isRollback
        override val isEligibleForAssociateDiscount: Boolean = self.isEligibleForAssociateDiscount
      }
    }
  }

  object ComparisonPriceJS {
    implicit class CPJSOps(val self: ComparisonPriceJS) extends AnyVal {
      def toComparisonPrice(log: Boolean = false): ComparisonPrice = {
        if (log) global.console.log(s"Scala: toComparisonPrice(self: ${JSON.stringify(self)})")
        ComparisonPrice(
          self.hidePriceForSOI,
          self.isClearance,
          self.isEligibleForAssociateDiscount,
          self.isReducedPrice,
          self.isRollback,
          self.isStrikethrough
        )
      }
    }
  }

  @ScalaJSDefined
  trait ProductMarketAttributesJS extends js.Object {
    val walmart_department_number: String
  }

  case class ProductMarketAttributes(walmart_department_number: String)

  object ProductMarketAttributes {
    implicit class PMAOps(val self: ProductMarketAttributes) extends AnyVal {
      def toPMAJS: ProductMarketAttributesJS = new ProductMarketAttributesJS {
        override val walmart_department_number: String = self.walmart_department_number
      }
    }
  }

  object ProductMarketAttributesJS {
    implicit class PMAJSOps(val self: ProductMarketAttributesJS) extends AnyVal {
      def toPMA(log: Boolean = false): ProductMarketAttributes = {
        if (log) global.console.log(s"Scala: toPMA(self: ${JSON.stringify(self)})")
        ProductMarketAttributes(
          self.walmart_department_number
        )
      }
    }
  }

  @ScalaJSDefined
  trait PreComputedItemJS extends js.Object {
    val comparisonPrice: ComparisonPriceJS
    val id: String
    val isRestrictedItem: Boolean
    val isWarrantyEligible: Boolean
    val name: String
    val price: Double
    val productMarketAttributes: ProductMarketAttributesJS
    val `type`: String
    val unitOfMeasure: String
    val upc: String
  }

  @ScalaJSDefined
  trait ItemJS extends js.Object {
    val comparisonPrice: ComparisonPriceJS
    val id: String
    val isRestrictedItem: Boolean
    val isWarrantyEligible: Boolean
    val linePrice: Double
    val name: String
    val price: Double
    val freeCouponItems: UndefOr[Int]
    val productMarketAttributes: ProductMarketAttributesJS
    val quantity: Int
    val `type`: String
    val unitOfMeasure: String
    val upc: String
  }

  case class Item(comparisonPrice: ComparisonPrice,
                  id: String,
                  isRestrictedItem: Boolean,
                  isWarrantyEligible: Boolean,
                  linePrice: Double,
                  name: String,
                  price: Double,
                  freeCouponItems: Option[Int],
                  productMarketAttributes: ProductMarketAttributes,
                  quantity: Int,
                  `type`: String,
                  unitOfMeasure: String,
                  upc: String)

  object Item {
    implicit class ItemOps(val self: Item) extends AnyVal {
      def toItemJS: ItemJS = new ItemJS {
        override val comparisonPrice: ComparisonPriceJS = self.comparisonPrice.toComparisonPriceJS
        override val `type`: String = self.`type`
        override val productMarketAttributes: ProductMarketAttributesJS = self.productMarketAttributes.toPMAJS
        override val price: Double = self.price
        override val freeCouponItems: UndefOr[Int] = self.freeCouponItems.orUndefined
        override val id: String = self.id
        override val isRestrictedItem: Boolean = self.isRestrictedItem
        override val quantity: Int = self.quantity
        override val unitOfMeasure: String = self.unitOfMeasure
        override val linePrice: Double = self.linePrice
        override val upc: String = self.upc
        override val name: String = self.name
        override val isWarrantyEligible: Boolean = self.isWarrantyEligible
      }
    }
  }

  object ItemJS {
    implicit class ItemsJSOps(val self: ItemJS) extends AnyVal {
      def toItem(log: Boolean = false): Item = {
        if (log) global.console.log(s"Scala: toItem(self: ${JSON.stringify(self)})")
        Item(
          self.comparisonPrice.toComparisonPrice(log),
          self.id,
          self.isRestrictedItem,
          self.isWarrantyEligible,
          self.linePrice,
          self.name,
          self.price,
          self.freeCouponItems.toOption,
          self.productMarketAttributes.toPMA(log),
          self.quantity,
          self.`type`,
          self.unitOfMeasure,
          self.upc
        )
      }
    }
  }

  @ScalaJSDefined
  trait CartJS extends js.Object {
    val id: String
    val location: LocationJS
    val storeIds: js.Array[Int]
    val totals: UndefOr[TotalsJS] = js.undefined
    val `type`: String
  }

  @ScalaJSDefined
  trait RenderableCartJS extends js.Object {
    val id: String
    val location: LocationJS
    val storeIds: js.Array[Int]
    val totals: UndefOr[TotalsJS] = js.undefined
    val `type`: String
  }

  @ScalaJSDefined
  trait CartWrapperJS extends js.Object {
    val cart: CartJS
    val coupons: UndefOr[js.Array[CouponJS]] = js.undefined
    val items: UndefOr[js.Array[ItemJS]] = js.undefined
  }

  @ScalaJSDefined
  trait RenderableCartWrapperJS extends js.Object {
    val cart: RenderableCartJS
    val coupons: UndefOr[js.Array[RenderableCouponJS]] = js.undefined
    val items: UndefOr[js.Array[ItemJS]] = js.undefined
  }

  case class Cart(id: String,
                  location: Location,
                  storeIds: Array[Int],
                  totals: Option[Totals],
                  `type`: String)

  object Cart {
    implicit class CartOps(val self: Cart) extends AnyVal {
      def toCartJS: CartJS = new CartJS {
        override val id: String = self.id
        override val location: LocationJS = self.location.toLocationJS
        override val storeIds: js.Array[Int] = self.storeIds
        override val totals: UndefOr[TotalsJS] = self.totals.map(_.toTotalsJS).orUndefined
        override val `type`: String = self.`type`
      }
    }
  }

  object CartJS {
    implicit class CartJSOps(val self: CartJS) extends AnyVal {
      def toCart(log: Boolean = false): Cart = {
        if (log) global.console.log(s"Scala: toCart(self: ${JSON.stringify(self)})")
        Cart(
          self.id,
          self.location.toLocation(log),
          self.storeIds,
          self.totals.toOption.map(_.toTotals(log)),
          self.`type`
        )
      }

      def toRenderableCartJS: RenderableCartJS = new RenderableCartJS {
        override val id: String = self.id
        override val location: LocationJS = self.location
        override val storeIds: js.Array[Int] = self.storeIds
        override val totals: UndefOr[TotalsJS] = self.totals.map { total =>
          new TotalsJS {
            override val grandTotal: Double = BigDecimal(total.grandTotal).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
            override val taxTotal: Double = BigDecimal(total.taxTotal).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
            override val subTotal: Double = BigDecimal(total.subTotal).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
            override val discount: UndefOr[Double] = total.discount.map(BigDecimal(_).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble)
          }
        }
        override val `type`: String = self.`type`
      }
    }
  }

  case class CartWrapper(cart: Cart, coupons: Option[Seq[Coupon]], items: Option[Seq[Item]])

  object CartWrapper {
    implicit class CartWrapperOps(val self: CartWrapper) extends AnyVal {
      def toCartWrapperJS: CartWrapperJS = new CartWrapperJS {
        override val cart: CartJS = self.cart.toCartJS
        override val coupons: UndefOr[js.Array[CouponJS]] = self.coupons.map(_.map(_.toCouponJS).toJSArray).orUndefined
        override val items: UndefOr[js.Array[ItemJS]] = self.items.map(_.map(_.toItemJS).toJSArray).orUndefined
      }
    }
  }

  object CartWrapperJS {
    implicit class CartWrapperJSOps(val self: CartWrapperJS) extends AnyVal {
      def toCartWrapper(log: Boolean = false): CartWrapper = {
        if (log) global.console.log(s"Scala: toCartWrapper(self: ${JSON.stringify(self)})")
        CartWrapper(
          self.cart.toCart(log),
          self.coupons.toOption.map(_.map(_.toCoupon(log))),
          self.items.toOption.map(_.map(_.toItem(log))))
      }
      def toRenderableCartWrapperJS(log: Boolean = false): RenderableCartWrapperJS = new RenderableCartWrapperJS {
        override val cart: RenderableCartJS = self.cart.toRenderableCartJS
        override val coupons: UndefOr[js.Array[RenderableCouponJS]] = self.coupons.map(_.map(_.toRenderableCouponJS(toCartWrapper(log))))
        override val items: UndefOr[js.Array[ItemJS]] = self.items.map(_.map { item =>
          new ItemJS {
            override val comparisonPrice: ComparisonPriceJS = item.comparisonPrice
            override val `type`: String = item.`type`
            override val productMarketAttributes: ProductMarketAttributesJS = item.productMarketAttributes
            override val price: Double = BigDecimal(item.price).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
            override val freeCouponItems: UndefOr[Int] = item.freeCouponItems
            override val id: String = item.id
            override val isRestrictedItem: Boolean = item.isRestrictedItem
            override val quantity: Int = item.quantity
            override val unitOfMeasure: String = item.unitOfMeasure
            override val linePrice: Double = BigDecimal(item.linePrice).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
            override val upc: String = item.upc
            override val name: String = item.name
            override val isWarrantyEligible: Boolean = item.isWarrantyEligible
          }
        })
      }
    }
  }

  @ScalaJSDefined
  trait UPCArrayJS extends js.Object {
    val upcs: js.Array[UPCJS]
  }

  case class UPCArray(upcs: Seq[UPC])

  object UPCArray {
    implicit class UPCArrayJSOps(val self: UPCArray) extends AnyVal {
      def toUPCArrayJS: UPCArrayJS = new UPCArrayJS {
        override val upcs: Array[UPCJS] = self.upcs.map(_.toUPCJS).toJSArray
      }
    }
  }

  object UPCArrayJS {
    implicit class UPCArrayOps(val self: UPCArrayJS) extends AnyVal {
      def toUPCArray(log: Boolean = false): UPCArray = {
        if (log) global.console.log(s"Scala: toUPCArray(self: ${JSON.stringify(self)})")
        UPCArray(self.upcs.map(_.toUPC(log)).toSeq)
      }
    }
  }

  @ScalaJSDefined
  trait UPCJS extends js.Object {
    val id: String
    val name: String
  }

  case class UPC(id: String, name: String)

  object UPC {
    implicit class UPCJSOps(val self: UPC) extends AnyVal {
      def toUPCJS: UPCJS = new UPCJS {
        override val name: String = self.name
        override val id: String = self.id
      }
    }
  }


  object UPCJS {
    implicit class UPCOps(val self: UPCJS) extends AnyVal {
      def toUPC(log: Boolean = false): UPC = {
        if (log) global.console.log(s"Scala: toUPC(self: ${JSON.stringify(self)})")
        UPC(self.id, self.name)
      }
    }
  }

  @ScalaJSDefined
  trait RequirementJS extends js.Object {
    val companyPrefix: String
    val familyCode: String
    val measurementUnit: String
    val threshold: String
  }

  @ScalaJSDefined
  trait RenderableRequirementJS extends js.Object {
    val amount: String
    val measurementUnit: String
    val threshold: String
    val operand: String
  }

  case class Requirement(companyPrefix:String,
                         familyCode: String,
                         measurementUnit: String,
                         threshold: String)

  object Requirement {
    implicit class RequirementJSOps(val self: Requirement) extends AnyVal {
      def toRequirementJS: RequirementJS = new RequirementJS {
        override val companyPrefix: String = self.companyPrefix
        override val familyCode: String = self.familyCode
        override val measurementUnit: String = self.measurementUnit
        override val threshold: String = self.threshold
      }
    }
  }

  object RequirementJS {
    implicit class RequirementOps(val self: RequirementJS) extends AnyVal {
      def toRequirement(log: Boolean = false): Requirement = {
        if (log) global.console.log(s"Scala: toRequirement(self: ${JSON.stringify(self)})")
        Requirement(
          self.companyPrefix,
          self.familyCode,
          self.measurementUnit,
          self.threshold
        )
      }
      def toRenderableRequirementJS(coupon: Coupon, cartWrapper: CartWrapper, log: Boolean = false): RenderableRequirementJS = {
        new RenderableRequirementJS {
          override val threshold: String = self.threshold
          override val measurementUnit: String = self.measurementUnit
          override val amount: String = cca.computeRequirement(toRequirement(log), coupon, cartWrapper).amount
          override val operand: String = "AND"
        }
      }
    }
  }

  @ScalaJSDefined
  trait SaveJS extends js.Object {
    val amount: Double
    val measurementUnit: String
    val whichItem: Int
  }

  case class Save(amount: Double,
                  measurementUnit: String,
                  whichItem: Int)

  object Save {
    implicit class SaveJSOps(val self: Save) extends AnyVal {
      def toSaveJS: SaveJS = new SaveJS {
        override val amount: Double = self.amount
        override val measurementUnit: String = self.measurementUnit
        override val whichItem: Int = self.whichItem
      }
    }
  }

  object SaveJS {
    implicit class SaveOps(val self: SaveJS) extends AnyVal {
      def toSave(log: Boolean = false): Save = {
        if (log) global.console.log(s"Scala: toSave(self: ${JSON.stringify(self)})")
        Save(
          self.amount,
          self.measurementUnit,
          self.whichItem
        )
      }
    }
  }

  @ScalaJSDefined
  trait CouponJS extends js.Object {
    val id: String
    val allowMultipleOffers: Boolean
    val associateIntervention: UndefOr[Boolean] = js.undefined
    val couponProvider: String
    val expiryDate: UndefOr[String] = js.undefined
    val families: js.Array[js.Dictionary[UPCArrayJS]]
    val databar: String
    val image: String
    val offerCode: String
    val primaryRequirement: RequirementJS
    val secondaryRequirement: UndefOr[RequirementJS] = js.undefined
    val tertiaryRequirement: UndefOr[RequirementJS] = js.undefined
    val purchaseRule: UndefOr[String] = js.undefined
    val retailerPrefix: UndefOr[String] = js.undefined
    val save: SaveJS
    val startDate: UndefOr[String] = js.undefined
    val serial: UndefOr[String] = js.undefined
    val storeCoupon: UndefOr[Int] = js.undefined
  }

  @ScalaJSDefined
  trait RenderableCouponJS extends js.Object {
    val id: String
    val allowMultipleOffers: Boolean
    val associateIntervention: UndefOr[Boolean] = js.undefined
    val couponProvider: String
    val image: String
    val requirements: Array[RenderableRequirementJS]
    val retailerPrefix: UndefOr[String] = js.undefined
    val save: SaveJS
    val `type`: String
  }

  case class Coupon(id: String,
                    allowMultipleOffers: Boolean,
                    associateIntervention: Option[Boolean],
                    couponProvider: String,
                    expiryDate: Option[String],
                    families: Seq[Map[String, UPCArray]],
                    databar: String,
                    image: String,
                    offerCode: String,
                    primaryRequirement: Requirement,
                    secondaryRequirement: Option[Requirement],
                    tertiaryRequirement: Option[Requirement],
                    purchaseRule: Option[String],
                    retailerPrefix: Option[String],
                    save: Save,
                    startDate: Option[String],
                    serial: Option[String],
                    storeCoupon: Option[Int])

  object Coupon {
    implicit class CouponJSOps(val self: Coupon) extends AnyVal {
      def toCouponJS: CouponJS = new CouponJS {
        override val allowMultipleOffers: Boolean = self.allowMultipleOffers
        override val couponProvider: String = self.couponProvider
        override val families: js.Array[Dictionary[UPCArrayJS]] = self.families.toJSArray.map(_.mapValues(_.toUPCArrayJS).toJSDictionary)
        override val databar: String = self.databar
        override val image: String = self.image
        override val offerCode: String = self.offerCode
        override val primaryRequirement: RequirementJS = self.primaryRequirement.toRequirementJS
        override val associateIntervention: UndefOr[Boolean] = self.associateIntervention.orUndefined
        override val expiryDate: UndefOr[String] = self.expiryDate.orUndefined
        override val secondaryRequirement: UndefOr[RequirementJS] = self.secondaryRequirement.map(_.toRequirementJS).orUndefined
        override val tertiaryRequirement: UndefOr[RequirementJS] = self.tertiaryRequirement.map(_.toRequirementJS).orUndefined
        override val purchaseRule: UndefOr[String] = self.purchaseRule.orUndefined
        override val retailerPrefix: UndefOr[String] = self.retailerPrefix.orUndefined
        override val startDate: UndefOr[String] = self.startDate.orUndefined
        override val serial: UndefOr[String] = self.serial.orUndefined
        override val storeCoupon: UndefOr[Int] = self.storeCoupon.orUndefined
        override val save: SaveJS = self.save.toSaveJS
        override val id: String = self.id
      }
    }
  }

  object CouponJS {
    implicit class CouponOps(val self: CouponJS) extends AnyVal {
      def toCoupon(log: Boolean = false): Coupon = {
        if (log) global.console.log(s"Scala: toCoupon(self: ${JSON.stringify(self)})")
        if (log) global.console.log(s"Scala: toCoupon: self.families: ${JSON.stringify(self.families)}")
        Coupon(
          self.id,
          self.allowMultipleOffers,
          self.associateIntervention.toOption,
          self.couponProvider,
          self.expiryDate.toOption,
          self.families.map(_.toMap.mapValues(_.toUPCArray(log))), //toList.map(_.toMap.mapValues(_.map(_.toUPC))),
          self.databar,
          self.image,
          self.offerCode,
          self.primaryRequirement.toRequirement(log),
          self.secondaryRequirement.toOption.map(_.toRequirement(log)),
          self.tertiaryRequirement.toOption.map(_.toRequirement(log)),
          self.purchaseRule.toOption,
          self.retailerPrefix.toOption,
          self.save.toSave(log),
          self.startDate.toOption,
          self.serial.toOption,
          self.storeCoupon.toOption
        )
      }

      def toRenderableCouponJS(cartWrapper: CartWrapper, log: Boolean = false): RenderableCouponJS = {
        val (adjustedAmount, adjustedMeasurementUnit) = {
          self.save.measurementUnit match {
            case "cents" | "centsOffBasket" =>
              if (self.save.amount >= 100)
                (self.save.amount / 100, "dollar")
              else
                (self.save.amount, "cent")
            case "dollars" =>
              (self.save.amount, "dollar")
            case _ =>
              (self.save.amount, self.save.measurementUnit)
          }
        }
        new RenderableCouponJS {
          override val id: String = self.id
          override val allowMultipleOffers: Boolean = self.allowMultipleOffers
          override val couponProvider: String = self.couponProvider
          override val image: String = self.image
          override val requirements: Array[RenderableRequirementJS] =
            Seq(
              Some(self.primaryRequirement.toRenderableRequirementJS(toCoupon(log), cartWrapper, log)),
              self.secondaryRequirement.toOption.map(_.toRenderableRequirementJS(toCoupon(log), cartWrapper, log)),
              self.tertiaryRequirement.toOption.map(_.toRenderableRequirementJS(toCoupon(log), cartWrapper, log))
            ).flatten.toJSArray
          override val save: SaveJS = new SaveJS {
            override val amount: Double = adjustedAmount
            override val measurementUnit: String = adjustedMeasurementUnit
            override val whichItem: Int = self.save.whichItem
          }
          override val `type`: String = {
            val threshold = BigDecimal(self.primaryRequirement.threshold).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
            val amount = BigDecimal(self.save.amount).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
            val prefix = self.primaryRequirement.measurementUnit match {
              case "totalTransaction" | "totalQualifyingItem"   =>
                s"Buy ${threshold.toString}$$, "
              case "units" =>
                s"Buy ${threshold.toInt}, "
            }
            val suffix = self.save.measurementUnit match {
              case "cents" | "centsOffBasket" =>
                if (amount >= 100)
                  s"Get ${amount / 100}$$ Off!"
                else
                  s"Get ${amount.toInt}c Off!"
              case "free" =>
                s"Get ${amount.toInt} Free!"
              case "percentage" =>
                s"Get ${amount.toInt}% Off!"
            }
            prefix + suffix
          }
        }
      }
    }
  }

  @JSExport
  def isGS1DatabarCoupon(databar: String, log: Boolean = false): Boolean = {
    if (log) global.console.log(s"Scala: isGS1DatabarCoupon(databar: ${JSON.stringify(databar)})")
    val appId = 8110
    val minNumDigits = 25
    val maxNumDigits = 70

    databar.startsWith(appId.toString) &&
    databar.length >= minNumDigits &&
    databar.length <= maxNumDigits
  }

  @JSExport
  def isBetweenActiveAndExpiryDate(couponJs: CouponJS, log: Boolean = false): Boolean = {
    if (log) global.console.log(s"Scala: isBetweenActiveAndExpiryDate(couponJs: ${JSON.stringify(couponJs)})")
    val afterStart = couponJs.startDate.fold(true) { startDate =>
      val today = Date.now()
      val start = Date.parse(startDate)
      today >= start
    }
    val beforeExp = couponJs.expiryDate.fold(true) { expDate =>
      val today = Date.now()
      val expiry = Date.parse(expDate)
      today <= expiry
    }
    afterStart && beforeExp
  }

  @JSExport
  def containsConflicts(newCoupon: CouponJS, existingCoupons: Array[CouponJS], log: Boolean = false): Boolean = {
    if (log) global.console.log(s"Scala: containsConflicts(newCoupon: ${JSON.stringify(newCoupon)}, existingCoupons: ${JSON.stringify(existingCoupons)})")
    existingCoupons.exists(coupon => coupon.databar == newCoupon.databar && !coupon.allowMultipleOffers)
  }

  @JSExport
  def computeCart(cartJs: CartWrapperJS, log: Boolean = false): CartWrapperJS = {
    if (log) global.console.log(s"Scala: computeCart(cartJs: ${JSON.stringify(cartJs)})")
    cca.computeCart(cartJs.toCartWrapper(log), log).toCartWrapperJS
  }

  @JSExport
  def toRenderableCart(cartJs: CartWrapperJS, log: Boolean = false): RenderableCartWrapperJS = {
    if (log) global.console.log(s"Scala: toRenderableCart(cartJs: ${JSON.stringify(cartJs)}")
    cartJs.toRenderableCartWrapperJS(log)
  }

  @JSExport
  def computeCartToRenderableCart(cartJs: CartWrapperJS, log: Boolean = false): RenderableCartWrapperJS = {
    if (log) global.console.log(s"Scala: computeCartToRenderableCart(cartJs: ${JSON.stringify(cartJs)}")
    toRenderableCart(computeCart(cartJs, log))
  }
}
