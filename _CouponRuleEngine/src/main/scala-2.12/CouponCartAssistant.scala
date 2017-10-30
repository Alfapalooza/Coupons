import scala.scalajs.js.Dynamic.global
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("CouponCartAssistant")
class CouponCartAssistant {
  @JSExport
  def computeCart(cartWrapper: CartWrapper, log: Boolean = false): CartWrapper = {
    global.console.log(s"Scala: CouponCartAssistant.computeCart($cartWrapper)")
    val discounts = computeDiscounts(cartWrapper)
    val computedItemsCart = computeItems(cartWrapper)
    computeTotals(computedItemsCart, discounts)
  }

  case class TransientRequirement(amount: String, threshold: String, measurementUnit: String, isMet: Boolean)
  def computeRequirement(requirement: Requirement, coupon: Coupon, cartWrapper: CartWrapper, log: Boolean = false): TransientRequirement = {
    if (log) global.console.log(s"Scala: CouponCartAssistant.computeRequirement($requirement, $coupon, $cartWrapper)")
    val items = getCartItemsForRequirement(requirement, cartWrapper, coupon)
    requirement.measurementUnit match {
      case "units" =>
        val countThreshold = try {
          requirement.threshold.toInt
        } catch { case _: Exception =>
          -1
        }
        val currentAmount = items.map(_.quantity).sum
        val isMet = countThreshold >= 0 && currentAmount >= countThreshold
        TransientRequirement(currentAmount.toString, countThreshold.toString, requirement.measurementUnit, isMet)
      case "totalQualifyingItem" =>
        val priceThreshold = try {
          requirement.threshold.toDouble
        } catch {
          case _: Exception => -1.0d
        }
        val currentAmount = items.map (item => item.price * item.quantity).sum
        val isMet = priceThreshold >= 0d && currentAmount >= priceThreshold
        TransientRequirement(currentAmount.toString, priceThreshold.toString, requirement.measurementUnit, isMet)
      case "totalTransaction" =>
        val priceThreshold = try {
          requirement.threshold.toDouble
        } catch {
          case _: Exception => -1.0d
        }
        val currentAmount = cartWrapper.cart.totals.map(_.subTotal).getOrElse(-1.0d)
        val isMet = items.nonEmpty && priceThreshold >= 0d && currentAmount >= priceThreshold
        TransientRequirement(currentAmount.toString, priceThreshold.toString, requirement.measurementUnit, isMet)
      case "lbs" =>
        val weightThreshold = try {
          requirement.threshold.toDouble
        } catch {
          case _: Exception => -1.0d
        }
        val lbsItems = items.filter(item => item.unitOfMeasure == "lbs")
        val currentAmount = lbsItems.map(_.quantity).sum
        val isMet = weightThreshold >= 0d && currentAmount >= weightThreshold
        TransientRequirement(currentAmount.toString, weightThreshold.toString, requirement.measurementUnit, isMet)
      case "kilograms" =>
        val weightThreshold = try {
          requirement.threshold.toDouble
        } catch {
          case _: Exception => -1.0d
        }
        val kgItems = items.filter(item => item.unitOfMeasure == "kilograms")
        val currentAmount = kgItems.map(_.quantity).sum
        val isMet = weightThreshold >= 0d && currentAmount >= weightThreshold
        TransientRequirement(currentAmount.toString, weightThreshold.toString, requirement.measurementUnit, isMet)
      case _ =>
        TransientRequirement("0", "0", "0", isMet = false)
    }
  }

  def computeCoupon(coupon: Coupon, cartWrapper: CartWrapper, log: Boolean = false): Double = {
    if (log) global.console.log(s"Scala: CouponCartAssistant.computeCoupon($coupon, $cartWrapper)")
    val rule = coupon.purchaseRule.getOrElse("or_or")
    val firstReq = coupon.primaryRequirement
    val secondReq = coupon.secondaryRequirement
    val thirdReq = coupon.tertiaryRequirement

    val isFirstReq = computeRequirement(firstReq, coupon, cartWrapper, log).isMet
    val isSecondReq = secondReq.exists(req => computeRequirement(req, coupon, cartWrapper, log).isMet)
    val isThirdReq = thirdReq.exists(req => computeRequirement(req, coupon, cartWrapper, log).isMet)

    def applyCoupon(coupon: Coupon, cartWrapper: CartWrapper): Double =  {
      if (log) global.console.log(s"Scala: CouponCartAssistant.applyCoupon($coupon, $cartWrapper)")
      coupon.save.measurementUnit match {
        case "cents" =>
          def centsOff(requirement: Requirement) = {
            val items = getCartItemsForRequirement(requirement, cartWrapper, coupon)
            if (coupon.allowMultipleOffers) {
              // get either coupon discount or price of item, whichever is lower, for each applicable item
              items.map(_.price).map(price => List(price * 100, coupon.save.amount).min).sum / 100
            } else {
              if (items.nonEmpty) {
                // get either coupon discount or price of most expensive item, whichever is lower
                List(items.map(_.price * 100).max, coupon.save.amount).min / 100
              } else 0.0d
            }
          }
          coupon.save.whichItem match {
            case 1 =>
              centsOff(coupon.primaryRequirement)
            case 2 =>
              coupon.secondaryRequirement.map(centsOff).getOrElse(0.0d) // if requirement not defined, can't apply savings
            case _ =>
              coupon.tertiaryRequirement.map(centsOff).getOrElse(0.0d)
          }
        case "centsOffBasket" =>
          def centsOffBasket(requirement: Requirement) = {
            val items = getCartItemsForRequirement(requirement, cartWrapper, coupon)
            // As long as a qualifying item is is the cart, give a flat discount irrelevant of the price of the item
            if (items.nonEmpty) coupon.save.amount else 0.0d
          }
          coupon.save.whichItem match {
            case 1 =>
              centsOffBasket(coupon.primaryRequirement)
            case 2 =>
              coupon.secondaryRequirement.map(centsOffBasket).getOrElse(0.0d) / 100
            case 3 =>
              coupon.tertiaryRequirement.map(centsOffBasket).getOrElse(0.0d) / 100
          }
        case "free" =>
          def freeItem(requirement: Requirement) = {
            val items = getCartItemsForRequirement(requirement, cartWrapper, coupon)
            // leaving out multiple offers on this for now. Complicated to determine how many you get for free.
            // Need to write the requirement checker to only look at each item once when making decisions.
            if (items.nonEmpty) {
              items.map(_.price).min // get the price of the lowest-cost qualifying item
            } else 0.0d
          }
          coupon.save.whichItem match {
            case 1 =>
              freeItem(coupon.primaryRequirement)
            case 2 =>
              coupon.secondaryRequirement.map(freeItem).getOrElse(0.0d)
            case 3 =>
              coupon.tertiaryRequirement.map(freeItem).getOrElse(0.0d)
          }
        case "percentage" =>
          def percentOff(requirement: Requirement) = {
            val items = getCartItemsForRequirement(requirement, cartWrapper, coupon)
            val percent = coupon.save.amount / 100
            if (coupon.allowMultipleOffers) {
              items.map(_.price).map(_ * percent).sum
            } else {
              if (items.nonEmpty) {
                items.map(_.price).max * percent
              } else 0.0d
            }
          }
          coupon.save.whichItem match {
            case 1 =>
              percentOff(coupon.primaryRequirement)
            case 2 =>
              coupon.secondaryRequirement.map(percentOff).getOrElse(0.0d)
            case 3 =>
              coupon.tertiaryRequirement.map(percentOff).getOrElse(0.0d)
          }
        case _ => 0.0d
      }
    }

    rule match {
      case "or_or" =>
        if (isFirstReq || isSecondReq || isThirdReq) {
          applyCoupon(coupon, cartWrapper)
        } else 0.0d
      case "and_and" =>
        if (isFirstReq && isSecondReq && isThirdReq) {
          applyCoupon(coupon, cartWrapper)
        } else 0.0d
      case "and_or" =>
        if (isFirstReq && (isSecondReq || isThirdReq)) {
          applyCoupon(coupon, cartWrapper)
        } else 0.0d
      case "meet_first_requirement" =>
        val doesMeetSecond = {
          val reqWith2ndFamily = secondReq.map(req2 => firstReq.copy(companyPrefix = req2.companyPrefix, familyCode = req2.familyCode))
          reqWith2ndFamily.exists(req => computeRequirement(req, coupon, cartWrapper, log).isMet)
        }
        val doesMeetThird = {
          val reqWith3rdFamily = thirdReq.map(req3 => firstReq.copy(companyPrefix = req3.companyPrefix, familyCode = req3.familyCode))
          reqWith3rdFamily.exists(req => computeRequirement(req, coupon, cartWrapper, log).isMet)
        }
        if (isFirstReq || isSecondReq || doesMeetSecond || isThirdReq || doesMeetThird) {
          applyCoupon(coupon, cartWrapper)
        } else 0.0d
    }
  }

  def computeDiscounts(cartWrapper: CartWrapper, log: Boolean = false): Option[Double] = {
    /**
      Qualification Flow:
      1. Do any of the coupons correspond to any items in the cart? Check UPCs
      2. If yes, check requirement rules: or_or, and_and, and_or, meet_first_requirement
      3. If or_or, check if any of the requirements are met, and apply savings if yes
      4. If and_and, check if all of the requirements are met, and apply savings if yes
      5. If and_or:
      5a. Check if first requirement is met. If yes, continue.
      5b. Check if either second or third requirement is met. If yes, apply savings.
      6. If meet_first_requirement
      6a. Check if first requirement is met. If yes, apply savings.
      6b. Check if second requirement is met. If yes, apply savings.
      6c. Check if first requirement is met by products with second prefix/family. If yes, apply savings.
      6d. Check if third requirement is met. If yes, apply savings.
      6e. Check if first requirement is met by products with third prefix/family. If yes, apply savings.

      Validation Flow:
      Requirements contain "measurementUnit", "threshold", "companyPrefix" and "familyCode"
      The measurementUnit is one of "units", "totalQualifyingItem", "totalTransaction", "lbs", "kilograms"
      1. Get items matching the current companyPrefix-familyCode combo
      2. "units": Count the units of items that match the applicable UPCs
      3. "totalQualifyingItem": Count the units of items that match the applicable UPCs, multiply by their respective prices, and total
      4. "totalTransaction": Subtotal of cart before taxes, if applicable UPC is in cart.
      5. "lbs": Check applicable item "unitOfMeasure". If "lbs", then check "quantity" to see if it's over threshold.
      6. "kilograms": Check applicable item "unitOfMeasure". If "kilograms", then check "quantity" to see if it's over threshold.
    **/
    if (log) global.console.log(s"Scala: CouponCartAssistant.computeDiscounts($cartWrapper)")
    cartWrapper.coupons.map(_.map(computeCoupon(_, cartWrapper, log)).sum)
  }

  private def computeItems(cartWrapper: CartWrapper): CartWrapper = {
    cartWrapper.items.fold(cartWrapper) { items =>
      val itemsMap = scala.collection.mutable.Map[String, Item]()

      items.foreach { item =>
        itemsMap.get(item.upc).fold {
          val newLinePrice = item.price * item.quantity
          itemsMap.put(item.upc, item.copy(linePrice = newLinePrice))
        } { mapItem =>
          val newQuantity = mapItem.quantity + item.quantity
          val newLinePrice = mapItem.linePrice + (item.price * item.quantity)
          itemsMap.put(mapItem.upc, mapItem.copy(quantity = newQuantity, linePrice = newLinePrice))
        }
      }
      cartWrapper.copy(items = if (itemsMap.nonEmpty) Some(itemsMap.values.toSeq) else None)
    }
  }

  private def computeTotals(cartWrapper: CartWrapper, discount: Option[Double] = None, taxRate: Double = 0.13): CartWrapper = {
    val subTotal = cartWrapper.items.fold(0d)(_.foldLeft(0d)((acc, item) => acc + item.linePrice))
    val subTotalWithDiscounts = discount.fold(subTotal)(discount => Math.max(subTotal - discount, 0))
    val taxTotal = subTotalWithDiscounts * taxRate
    val grandTotal = subTotalWithDiscounts + taxTotal
    cartWrapper.copy(cart = cartWrapper.cart.copy(totals = Some(Totals(grandTotal, subTotal, taxTotal, discount))))
  }

  private def getCartItemsForRequirement(requirement: Requirement, cart: CartWrapper, coupon: Coupon): Seq[Item] = {
    val family = requirement.familyCode
    val prefix = requirement.companyPrefix
    val upcKey = prefix + "-" + family
    val upcs = coupon.families.headOption.flatMap(_.get(upcKey)).map(_.upcs.map(_.id)).getOrElse(Seq.empty[String])
    val filteredItems = cart.items.map(_.filter(item => upcs.contains(item.upc))).getOrElse(Seq.empty[Item])
    filteredItems
  }
}
