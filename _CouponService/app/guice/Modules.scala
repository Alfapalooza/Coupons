package guice

import com.google.inject.Inject
import persistence.Persistence
import play.api.Configuration
import services.CouponService
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext

class Modules @Inject()(configuration: Configuration)(implicit ec: ExecutionContext) {
  private lazy val dbConfiguration: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("db")
  lazy val persistence = new Persistence(dbConfiguration)
  lazy val couponService = new CouponService(persistence)
}
