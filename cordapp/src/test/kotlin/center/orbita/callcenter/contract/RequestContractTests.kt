package center.orbita.callcenter.contract

import center.orbita.callcenter.TestConstants
import center.orbita.callcenter.state.RequestState
import net.corda.core.contracts.CommandData
import org.junit.Test

class RequestContractTests : AbstractContactTests<RequestState>() {
    override val inputStateForTest = TestConstants.requestState

    override val contractClassName = RequestContract.CONTRACT_ID

    override val stateName = RequestContract.stateName

    override val createCommand: CommandData = RequestContract.Create()

    override val editCommand: CommandData = RequestContract.Modify()

    override val removeCommand: CommandData = RequestContract.Remove()

    @Test
    fun createRequestTransactionMustBeWellFormed() {
        super.createCommandTransactionMustBeWellFormed()
    }

    @Test
    fun modifyRequestTransactionMustBeWellFormed() {
        super.modifyCommandTransactionMustBeWellFormed()
    }

    @Test
    fun removeRequestTransactionMustBeWellFormed() {
        super.removeCommandTransactionMustBeWellFormed()
    }
}