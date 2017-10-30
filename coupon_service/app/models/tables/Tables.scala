package models.tables

import models.rows.{FamilyRow, UPCRow}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, Index}
import slick.tables.TablesA

class Tables(override val dbConfig: DatabaseConfig[JdbcProfile]) extends TablesA(dbConfig) {
  import profile.api._

  class Family(_tableTag: Tag) extends BaseTableA[FamilyRow](_tableTag, "family") {
    def * = (id, family_code, company_prefix, image_url, coupon_name) <> (FamilyRow.tupled, FamilyRow.unapply)

    def family_code: Rep[Long] = column[Long]("family_code")
    def company_prefix: Rep[Long] = column[Long]("company_prefix")
    def image_url: Rep[String] = column[String]("image_url")
    def coupon_name: Rep[String] = column[String]("coupon_name")

    val familyCodeIndex: Index = index("family_code_idx", family_code)
    val companyPrefixIndex: Index = index("company_prefix_idx", company_prefix)
  }
  lazy val FamilyTQ = new TableQuery(tag => new Family(tag))

  class UPC(_tableTag: Tag) extends BaseTableA[UPCRow](_tableTag, "upc") {
    def * = (id, familyId, upc, name) <> (UPCRow.tupled, UPCRow.unapply)

    def familyId: Rep[Long] = column[Long]("family_id")
    def upc: Rep[String] = column[String]("upc")
    def name: Rep[String] = column[String]("name")

    val familyIdIndex: Index = index("family_id_idx", familyId)

    lazy val familyCodeFK: ForeignKeyQuery[Tables.this.Family, FamilyRow] = foreignKey(
      "fk_family_code",
      familyId,
      FamilyTQ
    )(r => r.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Restrict)
  }
  lazy val UPCTQ = new TableQuery(tag => new UPC(tag))
}
