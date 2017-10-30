package models.rows

case class FamilyUPCS(familyCodeRow: FamilyRow, upcs: Set[UPCRow])

object FamilyUPCS {
  def apply(tupled: (FamilyRow, Set[UPCRow])): FamilyUPCS =
    FamilyUPCS(tupled._1, tupled._2)
}
