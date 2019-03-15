package center.orbita.callcenter.corda.service

import center.orbita.callcenter.corda.NodeRPCConnection
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.flows.FlowLogic
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.getOrThrow

interface StateManager<T> {
    fun list(): List<T>

    fun getById(id: UniqueIdentifier): T

    fun create(model: T): UniqueIdentifier

    fun batchCreate(models: List<T>)

    fun modify(model: T)

    fun remove(id: UniqueIdentifier)
}

abstract class AbstractStateManager<T>(
    val list: Class<out FlowLogic<List<T>>>,
    val getById: Class<out FlowLogic<T>>,
    val create: Class<out FlowLogic<SignedTransaction>>,
    val modify: Class<out FlowLogic<SignedTransaction>>,
    val remove: Class<out FlowLogic<SignedTransaction>>
) : StateManager<T> {
    protected abstract val rpc: NodeRPCConnection

    override fun getById(id: UniqueIdentifier): T {
        val flow = rpc.proxy.startFlowDynamic(getById, id)
        return flow.returnValue.getOrThrow()
    }

    override fun list(): List<T> {
        val flow = rpc.proxy.startFlowDynamic(list)
        return flow.returnValue.getOrThrow()
    }

    override fun create(model: T): UniqueIdentifier {
        val flow = rpc.proxy.startFlowDynamic(create, model)
        val result = flow.returnValue.getOrThrow()
        return (result.tx.outputs.first().data as LinearState).linearId
    }

    override fun batchCreate(models: List<T>) {
        models.forEach {
            val flow = rpc.proxy.startFlowDynamic(create, it)
            flow.returnValue.getOrThrow()
        }
    }

    override fun modify(model: T) {
        val flow = rpc.proxy.startFlowDynamic(modify, model)
        flow.returnValue.getOrThrow()
    }

    override fun remove(id: UniqueIdentifier) {
        val flow = rpc.proxy.startFlowDynamic(remove, id)
        flow.returnValue.getOrThrow()
    }
}