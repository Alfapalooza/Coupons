package controllers

import javax.inject._

import guice.Modules
import models.Databar
import play.api.cache.Cached
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class Coupons @Inject()(modules: Modules, cache: Cached)(implicit ec: ExecutionContext) extends Controller {
  def getCoupon(databar: Databar): EssentialAction =
    cache(databar) {
      Action.async {
        modules.couponService.computeCouponDatabarToCoupon(databar).map(_.map(c => Ok(Json.toJson(c))).getOrElse(BadRequest(Json.obj("message" -> "Failed to compute coupon"))))
      }
    }
}
