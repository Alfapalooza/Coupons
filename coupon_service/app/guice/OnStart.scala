package guice

import com.google.inject.Inject
import models.rows.{FamilyRow, UPCRow}

import scala.concurrent.ExecutionContext

class OnStart @Inject()(modules: Modules)(implicit ec: ExecutionContext) {
  private def start() = {
    def loadDatabase() = {
      // SC Johnson & Windex Company and Family prefix
      val scJohnsonCompanyPrefix = 19800L
      val scJohnsonWindexFamilyPrefix = 100L

      val scJohnsonWindexFamily = FamilyRow(0, scJohnsonWindexFamilyPrefix, scJohnsonCompanyPrefix, "Save on Windex!", "https://scjdmcdn.azureedge.net/~/media/scj-pro/products/scjp-ammoniated-glass-cleaner/windex-original-glass-cleaner-23oz-trigger-679592.gif?h=0&w=0&la=en-US&hash=8D64C900B43EFD13D1DBB808C0CDD4444E0F0D53")
      val scJohnsonWindexFamilyUPCs = Seq(
        UPCRow(0, 0, "059200007722", "Windex")
      )

      // P&G & H&S Company and Family prefix
      val procterAndGambleCompanyPrefix = 37000L
      val procterAndGambleHeadAndShoulderFamilyPrefix = 515L

      val procterAndGambleHeadAndShoulderFamily = FamilyRow(0, procterAndGambleHeadAndShoulderFamilyPrefix, procterAndGambleCompanyPrefix, "Save on Head&Shoulder!", "http://whatsyourdeal.com/grocery-coupons/wp-content/uploads/2015/07/Capture138.png")
      val procterAndGambleHeadAndShoulderUPCs = Seq(
        UPCRow(0, 0, "037000142553", "Head&Shoulder Conditioner"),
        UPCRow(0, 0, "037000062417", "Head&Shoulder Shampoo")
      )
      // General Mills & Cereal Company and Family prefix
      val generalMillsCompanyPrefix = 16000L
      val generalMillsCerealFamilyPrefix = 700L

      val generalMillsCerealFamily = FamilyRow(0, generalMillsCerealFamilyPrefix, generalMillsCompanyPrefix, "Save on General Mills Cereal!", "http://couponkarma.com/wp-content/uploads/2015/07/general-mills-cereal.jpg")
      val generalMillsCerealUPCs = Seq(
        UPCRow(0, 0, "065633279575", "Honey Nut Cheerios"),
        UPCRow(0, 0, "065633279315", "Cinnamon Toast Crunch"),
        UPCRow(0, 0, "064100108028", "Raisin Bran")
      )

      //Windex
      upsertFamilyUPCS(scJohnsonCompanyPrefix, scJohnsonWindexFamilyPrefix, scJohnsonWindexFamily, scJohnsonWindexFamilyUPCs)
      //Head & Shoulder
      upsertFamilyUPCS(procterAndGambleCompanyPrefix, procterAndGambleHeadAndShoulderFamilyPrefix, procterAndGambleHeadAndShoulderFamily, procterAndGambleHeadAndShoulderUPCs)
      //Cereal
      upsertFamilyUPCS(generalMillsCompanyPrefix, generalMillsCerealFamilyPrefix, generalMillsCerealFamily, generalMillsCerealUPCs)

      modules.persistence.familyDal.getAllFamilies.map(_.foreach { test =>
        // Check that one of the company / family was inserted
        println(test)
      })
    }

    loadDatabase()
  }

  private def upsertFamilyUPCS(companyPrefix: Long, familyPrefix: Long, familyRow: FamilyRow, upcs: Seq[UPCRow]) = {
    modules.persistence.familyDal.getFamilyUPCSByCompanyPrefixAndFamily(companyPrefix, familyPrefix).map(_.fold {
      for {
        family <- modules.persistence.familyDal.insert(familyRow)
        _ <- modules.persistence.upcDal.inserts(upcs.map(_.copy(familyId = family.id)))
      } yield 1
    } { family =>
      val missingUPCs = upcs.collect {
        case productUPC if !family.upcs.exists(upc => upc.upc == productUPC.upc) =>
          productUPC
      }
      modules.persistence.upcDal.inserts(missingUPCs.map(_.copy(familyId = family.familyCodeRow.id))).map(_ => 1)
    })
  }

  start()
}
