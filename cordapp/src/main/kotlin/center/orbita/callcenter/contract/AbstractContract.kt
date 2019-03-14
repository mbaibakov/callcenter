package center.orbita.callcenter.contract

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.CommandWithParties
import net.corda.core.contracts.Contract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

abstract class AbstractContract<S : LinearState, C : CommandData> : Contract {

    protected inline fun <reified T : S> getSingleInput(tx: LedgerTransaction): T = tx.inputsOfType<T>().single()
    protected inline fun <reified T : S> getSingleOutput(tx: LedgerTransaction): T = tx.outputsOfType<T>().single()

    protected fun checkOneSigner(command: CommandWithParties<C>) {
        requireThat {
            "There must be one signer" using (command.signers.toSet().size == 1)
        }
    }
}

inline fun <reified T : LinearState> checkCreateCommand(tx: LedgerTransaction, stateName: String) {
    requireThat {
        "No inputs should be consumed when creating $stateName" using (tx.inputs.isEmpty())
        "One output should be produced when creating $stateName" using (tx.outputs.size == 1)
        "Output state should be an instance of ${T::class.java.name}" using (tx.outputStates.first() is T)
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : LinearState> checkEditCommand(tx: LedgerTransaction, stateName: String) {
    requireThat {
        "One input should be consumed when editing $stateName" using (tx.inputs.size == 1)
        "One output should be produced when editing $stateName" using (tx.outputs.size == 1)
        "Input state should be an instance of ${T::class.java.name}" using (tx.inputStates.first() is T)
        val inState = tx.inputStates.first() as T
        "Output state should be an instance of ${T::class.java.name}" using (tx.outputStates.first() is T)
        val outState = tx.outputStates.first() as T
        "Input and output states should have same linearId" using (inState.linearId == outState.linearId)
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : LinearState> checkRemoveCommand(tx: LedgerTransaction, stateName: String) {
    requireThat {
        "One input should be consumed when removing $stateName" using (tx.inputs.size == 1)
        "No output states should be produced when removing $stateName" using (tx.outputs.isEmpty())
        "Input state should be an instance of ${T::class.java.name}" using (tx.inputStates.first() is T)
    }
}