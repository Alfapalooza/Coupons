package models.rows

import slick.sqlbase.BaseEntity

case class UPCRow(id: Long, familyId: Long, upc: String, name: String) extends BaseEntity
