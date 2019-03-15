package center.orbita.callcenter.contract

import center.orbita.callcenter.state.RequestState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

class RequestContract : AbstractContract<RequestState, RequestContract.Commands>() {
    companion object {
        @JvmStatic
        val CONTRACT_ID: String = "center.orbita.callcenter.contract.RequestContract"

        const val stateName = "a request"
    }

    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<Commands>()

        when (command.value) {
            is Create -> requireThat {
                checkCreateCommand<RequestState>(tx, stateName)
                checkOneSigner(command)
            }

            is Modify -> requireThat {
                checkEditCommand<RequestState>(tx, stateName)
                checkOneSigner(command)
            }

            is Remove -> requireThat {
                checkRemoveCommand<RequestState>(tx, stateName)
                checkOneSigner(command)
            }
        }
    }

    interface Commands : CommandData

    class Create : Commands, TypeOnlyCommandData()
    class Modify : Commands, TypeOnlyCommandData()
    class Remove : Commands, TypeOnlyCommandData()
}