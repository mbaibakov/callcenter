package center.orbita.callcenter.flow

import center.orbita.callcenter.flow.BaseFlow.Companion.BUILDING
import center.orbita.callcenter.flow.BaseFlow.Companion.FINALISING
import center.orbita.callcenter.flow.BaseFlow.Companion.GATHERING_SIGS
import center.orbita.callcenter.flow.BaseFlow.Companion.SIGNING
import center.orbita.callcenter.flow.BaseFlow.Companion.VERIFYING
import center.orbita.callcenter.util.SuspendableWrapper
import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.CollectSignaturesFlow
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.ReceiveFinalityFlow
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@InitiatingFlow
abstract class BaseInitiatingFlow<out T> : BaseFlow<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        progressTracker.currentStep = BUILDING
        val tx = getTransaction()

        progressTracker.currentStep = VERIFYING
        tx.verify(serviceHub)

        progressTracker.currentStep = SIGNING
        val signedTx = serviceHub.signInitialTransaction(tx)

        progressTracker.currentStep = FINALISING

        val commandSignersPublicKeys = signedTx.tx.commands.flatMap { it.signers }.distinct() - ourIdentity.owningKey
        val commandSignersParties = serviceHub.networkMapCache.allNodes
                .flatMap { it.legalIdentities }
                .filter { commandSignersPublicKeys.contains(it.owningKey) }
        return if (commandSignersParties.isEmpty()) {
            val session = initiateFlow(getNotary())
            subFlow(FinalityFlow(signedTx, session))
        } else {
            val flowSessions = commandSignersParties.map { initiateFlow(it) }
            val ftx = subFlow(CollectSignaturesFlow(signedTx, flowSessions, GATHERING_SIGS.childProgressTracker()))
            subFlow(FinalityFlow(ftx, flowSessions, FINALISING.childProgressTracker()))
        }
    }

    abstract fun getTransaction(): TransactionBuilder

    abstract fun finalizeTransaction(signedTx: SignedTransaction): SuspendableWrapper<T>

    protected fun finalizeTransactionImpl(signedTx: SignedTransaction): SuspendableWrapper<SignedTransaction> {
        return object : SuspendableWrapper<SignedTransaction> {
            @Suspendable
            override fun call(): SignedTransaction {
                val commandSignersPublicKeys = signedTx.tx.commands.flatMap { it.signers }.distinct() - ourIdentity.owningKey
                val commandSignersParties = serviceHub.networkMapCache.allNodes
                        .flatMap { it.legalIdentities }
                        .filter { commandSignersPublicKeys.contains(it.owningKey) }
                return if (commandSignersParties.isEmpty()) {
                    val session = initiateFlow(ourIdentity)
                    subFlow(FinalityFlow(signedTx, session))
                } else {
                    val flowSessions = commandSignersParties.map { initiateFlow(it) }
                    val ftx = subFlow(CollectSignaturesFlow(signedTx, flowSessions, GATHERING_SIGS.childProgressTracker()))
                    subFlow(FinalityFlow(ftx, flowSessions, FINALISING.childProgressTracker()))
                }
            }
        }
    }
}


@InitiatedBy(BaseInitiatingFlow::class)
class BaseReceivingFlow(private val otherSide: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        subFlow(ReceiveFinalityFlow(otherSide))
    }
}
