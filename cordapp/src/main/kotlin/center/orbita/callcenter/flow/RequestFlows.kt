package center.orbita.callcenter.flow

import center.orbita.callcenter.contract.RequestContract
import center.orbita.callcenter.state.RequestState
import center.orbita.callcenter.structure.RequestEntity
import center.orbita.callcenter.structure.RequestModel
import center.orbita.callcenter.util.SuspendableWrapper
import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.StartableByRPC
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.builder
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@StartableByRPC
class CreateRequestFlow(private val requestModel: RequestModel) : BaseInitiatingFlow<SignedTransaction>() {

    override fun getTransaction(): TransactionBuilder {
        return TransactionBuilder(notary = getNotary())
                .addOutputState(requestModel.convertToState(listOf(ourIdentity)), RequestContract.CONTRACT_ID)
                .addCommand(RequestContract.Create(), listOf(ourIdentity.owningKey))
    }

    override fun finalizeTransaction(signedTx: SignedTransaction): SuspendableWrapper<SignedTransaction> {
        return finalizeTransactionImpl(signedTx)
    }
}

@StartableByRPC
class ModifyRequestFlow(private val requestModel: RequestModel) : BaseInitiatingFlow<SignedTransaction>() {

    override fun getTransaction(): TransactionBuilder {
        val inputStateAndRef = getOutputStateByIdOrThrow<RequestState>(requestModel.id!!)

        val outputState = inputStateAndRef.state.data.copy(
                msisdn = requestModel.msisdn,
                creationDate = requestModel.creationDate,
                responseData = requestModel.responseData,
                linearId = UniqueIdentifier(null, this.requestModel.id)
        )

        return TransactionBuilder(notary = getNotary())
                .addInputState(inputStateAndRef)
                .addOutputState(outputState, RequestContract.CONTRACT_ID)
                .addCommand(RequestContract.Modify(), listOf(ourIdentity.owningKey))
    }

    override fun finalizeTransaction(signedTx: SignedTransaction): SuspendableWrapper<SignedTransaction> {
        return finalizeTransactionImpl(signedTx)
    }
}

@StartableByRPC
class RemoveRequestFlow(private val id: UniqueIdentifier) : BaseInitiatingFlow<SignedTransaction>() {
    override fun getTransaction(): TransactionBuilder {
        return TransactionBuilder(notary = getNotary())
                .addInputState(getOutputStateByIdOrThrow<RequestState>(id))
                .addCommand(RequestContract.Remove(), listOf(ourIdentity.owningKey))
    }

    override fun finalizeTransaction(signedTx: SignedTransaction): SuspendableWrapper<SignedTransaction> {
        return finalizeTransactionImpl(signedTx)
    }
}

@StartableByRPC
class ListRequestFlow : FlowLogic<List<RequestModel>>() {
    @Suspendable
    override fun call(): List<RequestModel> {
        val stateAndRefs = serviceHub.vaultService.queryBy<RequestState>().states
        return stateAndRefs.map { it.state.data.convertToModel() }
    }
}

@StartableByRPC
class GetRequestByIdFlow(private val linearId: UniqueIdentifier) : BaseFlow<RequestModel>() {
    @Suspendable
    override fun call(): RequestModel {
        return getOutputStateByIdOrThrow<RequestState>(linearId).state.data.convertToModel()
    }
}

@StartableByRPC
class SearchRequestByMsisdnFlow(private val msisdn: String) : FlowLogic<List<RequestModel>>() {
    @Suspendable
    override fun call(): List<RequestModel> {
        builder {
            val criteria = QueryCriteria.VaultCustomQueryCriteria(RequestEntity::msisdn.equal(msisdn))
            return serviceHub.vaultService.queryBy<RequestState>(criteria).states.map { it.state.data.convertToModel() }
        }
    }
}