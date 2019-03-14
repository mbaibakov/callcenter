package center.orbita.callcenter.flow

import center.orbita.callcenter.TestConstants
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.transactions.LedgerTransaction
import net.corda.core.transactions.TransactionBuilder
import java.io.FileInputStream

abstract class BasePreparedFlowTest<S : ContractState> : BaseFlowTest() {

    abstract fun createTestState(): S

    protected open fun prepareAdditionalData() {
        // This is template method. Should be overriden if additional preparation needed
    }

    fun createTestTransaction(s: S): TransactionBuilder {
        return createOneNodeTransaction(s)
    }

    fun createOneNodeTransaction(s: S): TransactionBuilder {
        prepareAdditionalData()

        return TransactionBuilder(notary = notaryParty)
                .addOutputState(s, TestContract.CONTRACT_ID)
                .addCommand(TestContract.Prepare(), listOf(aParty.owningKey))
    }

    fun queryStates(clazz: Class<out LinearState>): List<StateAndRef<LinearState>> {
        return a.transaction { a.services.vaultService.queryBy(clazz).states }
    }

    protected fun insureTestAttachmentUploaded() {
        a.transaction {
            if (!a.services.attachments.hasAttachment(TestConstants.testAttachmentId)) {
                val inputStream = FileInputStream(TestConstants.testAttachmentFilePath)
                inputStream.use {
                    a.services.attachments.importAttachment(jar = it, uploader = "some nice uploader", filename = null)
                }
            }
        }
    }
}

class TestContract : Contract {
    companion object {
        @JvmStatic
        val CONTRACT_ID = TestContract::class.qualifiedName!!
    }

    override fun verify(tx: LedgerTransaction) {
        // verifying everything...
    }

    interface Commands : CommandData

    class Prepare : Commands, TypeOnlyCommandData()
}
