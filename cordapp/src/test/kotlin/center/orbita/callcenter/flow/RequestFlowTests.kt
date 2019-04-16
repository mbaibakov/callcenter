package center.orbita.callcenter.flow

import center.orbita.callcenter.TestConstants
import center.orbita.callcenter.contract.RequestContract
import center.orbita.callcenter.state.RequestState
import center.orbita.callcenter.structure.RequestModel
import net.corda.core.utilities.getOrThrow
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Assert
import org.junit.Test

class RequestFlowTests : BasePreparedFlowTest<RequestState>() {
    override fun createTestState() = TestConstants.requestModel.convertToState(listOf(aParty))

    override fun getContractId() = RequestContract.CONTRACT_ID

    override fun getCommand() = RequestContract.Create()

    @Test
    fun `CreateRequestFlow works correctly`() {
        val flow = CreateRequestFlow(TestConstants.requestModel)
        val future = a.startFlow(flow)
        network.runNetwork()

        val signedTx = future.getOrThrow()
        signedTx.verifyRequiredSignatures()

        val models = queryModels()
        Assert.assertThat(models.size, CoreMatchers.`is`(1))

        verify(TestConstants.requestModel, models[0])
    }

    @Test
    fun `ModifyRequestFlow works correctly`() {
        val state = prepareDataForTest()
        val modifiedState = state.copy(msisdn = "new msisdn")
        val modifiedModel = modifiedState.convertToModel()
        val flow = ModifyRequestFlow(modifiedModel)
        val future = a.startFlow(flow)
        network.runNetwork()

        val signedTx = future.getOrThrow()
        signedTx.verifyRequiredSignatures()

        val models = queryModels()
        Assert.assertThat(models.size, CoreMatchers.`is`(1))

        verify(modifiedModel, models[0])
    }

    @Test
    fun `DeleteRequestFlow works correctly`() {
        val state = prepareDataForTest()

        val flow = RemoveRequestFlow(state.linearId)
        val future = a.startFlow(flow)
        network.runNetwork()

        val signedTx = future.getOrThrow()
        signedTx.verifyRequiredSignatures()

        val models = queryModels()
        MatcherAssert.assertThat(models.size, CoreMatchers.`is`(0))
    }

    @Test
    fun `GetRequestByIdFlow works correctly`() {
        val state = prepareDataForTest()

        val flow = GetRequestByIdFlow(state.linearId)
        val future = a.startFlow(flow)
        network.runNetwork()

        verify(TestConstants.requestModel, future.getOrThrow())
    }

    @Test
    fun `ListRequestFlow works correctly`() {
        prepareDataForTest()

        val flow = ListRequestFlow()
        val future = a.startFlow(flow)
        network.runNetwork()

        val list = future.getOrThrow()
        Assert.assertThat(list.size, CoreMatchers.`is`(1))

        verify(TestConstants.requestModel, list[0])
    }

    @Test
    fun `SearchRequestByMsisdnFlow works correctly`() {
        prepareDataForTest()

        val flow = SearchRequestByMsisdnFlow(TestConstants.requestState.msisdn)
        val future = a.startFlow(flow)
        network.runNetwork()

        val list = future.getOrThrow()
        Assert.assertThat(list.size, CoreMatchers.`is`(1))

        verify(TestConstants.requestModel, list[0])
    }

    private fun queryModels() = queryStates(RequestState::class.java).map { (it.state.data as RequestState).convertToModel() }

    fun prepareDataForTest(): RequestState {
        val testState = createTestState()
        val tx = createTestTransaction(testState)
        val stx = a.services.signInitialTransaction(tx)
        a.transaction {
            a.services.recordTransactions(listOf(stx))
        }
        return testState
    }

    private fun verify(inputModel: RequestModel, outputModel: RequestModel) {
        Assert.assertEquals(outputModel, inputModel.copy(id = outputModel.id))
    }
}