package center.orbita.callcenter

import center.orbita.callcenter.structure.RequestModel
import net.corda.core.identity.CordaX500Name
import net.corda.core.node.services.AttachmentId
import net.corda.testing.core.ALICE_NAME
import net.corda.testing.core.BOB_NAME
import net.corda.testing.core.TestIdentity
import java.util.Date

class TestConstants {
    companion object {
        val megaCorpIdentity = TestIdentity(CordaX500Name("MegaCorp", "London", "GB"))
        val megaCorpPublicKey = megaCorpIdentity.publicKey

        val alicePublicKey = TestIdentity(ALICE_NAME).publicKey
        val bobPublicKey = TestIdentity(BOB_NAME).publicKey

        val testAttachmentFilePath = "src/test/resources/test-attachment.jar"
        val testAttachmentId = AttachmentId.parse("E411054D627AF798B52188A84AC691F56D405B3D5AE322717486BD919391F34F")

        val requestModel = RequestModel(
                msisdn = "msisdn",
                creationDate = Date()
        )
        val requestState = requestModel.convertToState(listOf(megaCorpIdentity.party))
    }
}