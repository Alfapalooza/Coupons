package models.rows

import slick.sqlbase.BaseEntity

case class FamilyRow(id: Long, familyCode: Long, companyPrefix: Long, couponName: String, imageUrl: String) extends BaseEntity
