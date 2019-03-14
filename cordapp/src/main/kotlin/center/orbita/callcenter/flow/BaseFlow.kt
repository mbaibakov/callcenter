package center.orbita.callcenter.flow

import center.orbita.callcenter.util.singleOrException
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.StateAndRef
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.CollectSignaturesFlow
import net.corda.core.flows.FinalityFlow
import net.corda.core.flows.FlowException
import net.corda.core.flows.FlowLogic
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria.LinearStateQueryCriteria
import net.corda.core.node.services.vault.QueryCriteria.VaultQueryCriteria
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import java.util.UUID

abstract class BaseFlow<out T> : FlowLogic<T>() {
    companion object {
        object CHECKING : ProgressTracker.Step("Checking required parameters")
        object QUERYING : ProgressTracker.Step("Querying the vault for existing states")
        object BUILDING : ProgressTracker.Step("Building transaction")
        object VERIFYING : ProgressTracker.Step("Verifying contract constraints")
        object SIGNING : ProgressTracker.Step("Signing transaction")

        object GATHERING_SIGS : ProgressTracker.Step("Gathering the counterparty's signature") {
            override fun childProgressTracker() = CollectSignaturesFlow.tracker()
        }

        object FINALISING : ProgressTracker.Step("Finalising transaction") {
            override fun childProgressTracker() = FinalityFlow.tracker()
        }

        fun tracker() = ProgressTracker(BUILDING, CHECKING, QUERYING, VERIFYING, SIGNING, GATHERING_SIGS, FINALISING)
    }

    override val progressTracker: ProgressTracker = tracker()

    protected inline fun <reified T : ContractState> getOutputStateByIdOrThrow(linearId: String): StateAndRef<T> {
        return getOutputStateByIdOrThrow(UniqueIdentifier.fromString(linearId))
    }

    protected inline fun <reified T : ContractState> getOutputStateByIdOrThrow(linearId: UniqueIdentifier): StateAndRef<T> {
        val criteria = LinearStateQueryCriteria(linearId = listOf(linearId))
        return serviceHub.vaultService.queryBy<T>(criteria).states
                .singleOrException("${T::class.java.simpleName} with linearId = $linearId")
    }

    protected inline fun <reified T : ContractState> getOutputStateByIdOrThrow(id: UUID): StateAndRef<T> {
        val criteria = LinearStateQueryCriteria(uuid = listOf(id))
        return serviceHub.vaultService.queryBy<T>(criteria).states
                .singleOrException("${T::class.java.simpleName} with linearId = $id")
    }

    protected inline fun <reified T : ContractState> getAllStatesById(linearId: String): List<StateAndRef<T>> {
        return getAllStatesById(UniqueIdentifier.fromString(linearId))
    }

    protected inline fun <reified T : ContractState> getAllStatesById(linearId: UniqueIdentifier): List<StateAndRef<T>> {
        val linearStateCriteria = LinearStateQueryCriteria(linearId = listOf(linearId), status = Vault.StateStatus.ALL)
        val vaultCriteria = VaultQueryCriteria(status = Vault.StateStatus.ALL)
        return serviceHub.vaultService.queryBy<T>(linearStateCriteria and vaultCriteria).states
    }

    protected inline fun <reified T : ContractState> isStatePresentedInTx(tx: TransactionBuilder): Boolean {
        return tx.outputStates().map { it.data }.filterIsInstance(T::class.java).isNotEmpty()
    }

    protected fun getNotary() = serviceHub.networkMapCache.notaryIdentities.first()

    protected fun getPartiesByNames(names: List<String>): List<Party> {
        return names.map { getPartyByNameOrThrow(it) }
    }

    protected fun getPartyByNameOrThrow(name: String): Party {
        val everyone = serviceHub.networkMapCache.allNodes.flatMap { it.legalIdentities }
        return everyone.singleOrNull { it.name == CordaX500Name.parse(name) }
                ?: throw FlowException("Can not find party with name $name")
    }
}