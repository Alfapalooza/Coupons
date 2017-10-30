package persistence

import models.rows.{FamilyRow, FamilyUPCS, UPCRow}
import models.tables.Tables
import play.api.Logger
import slick.basic.DatabaseConfig
import slick.dao.BaseDAONoStreamA
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class Persistence(config: DatabaseConfig[JdbcProfile])(implicit ec: ExecutionContext) {
  protected implicit val tables = new Tables(config)

  val logger = Logger(this.getClass)

  import tables._
  import tables.profile.api._

  lazy val schema: tables.profile.DDL = Array(FamilyTQ.schema, UPCTQ.schema).reduceLeft(_ ++ _)

  val createSchema: Future[Try[Unit]] = db.run(schema.create.asTry)

  lazy val familyDal = new BaseDAONoStreamA[tables.Family, FamilyRow](FamilyTQ) {
    def getAllFamilies: Future[Seq[FamilyRow]] =
      db.run((for {
        families <- FamilyTQ
      } yield families).result)

    def getFamilyByCompanyPrefixAndFamilyCode(companyPrefix: Long, familyCode: Long): Future[Option[FamilyRow]] =
      findByFilter { family =>
        family.company_prefix === companyPrefix &&
        family.family_code === familyCode
      } map (_.headOption)

    def getFamilyUPCSByCompanyPrefixAndFamily(companyPrefix: Long, familyCode: Long): Future[Option[FamilyUPCS]] =
      db.run((for {
        (family, upc) <- FamilyTQ.filter(
          family => family.company_prefix === companyPrefix && family.family_code === familyCode
        ) joinLeft UPCTQ on (_.id === _.familyId)
      } yield (family, upc)).result) map { seq =>
        seq.groupBy(e => e._1).mapValues(e => e.flatMap(x => x._2).toSet).headOption.map(FamilyUPCS.apply)
      }
  }
  lazy val upcDal = new BaseDAONoStreamA[tables.UPC, UPCRow](UPCTQ) {}
}
