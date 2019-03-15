package center.orbita.callcenter.contract

import center.orbita.callcenter.TestConstants.Companion.alicePublicKey
import center.orbita.callcenter.TestConstants.Companion.bobPublicKey
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.LinearState
import net.corda.testing.contracts.DummyState
import net.corda.testing.core.DummyCommandData
import net.corda.testing.dsl.TransactionDSL
import net.corda.testing.dsl.TransactionDSLInterpreter
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger

abstract class AbstractContactTests<S : LinearState> {

    abstract val inputStateForTest: S
    open val outputStateForTest: S get() = inputStateForTest

    abstract val contractClassName: String
    abstract val stateName: String
    abstract val createCommand: CommandData
    abstract val editCommand: CommandData
    abstract val removeCommand: CommandData

    protected open val createAction get() = "creating $stateName"
    protected open val editAction get() = "editing $stateName"
    protected open val removeAction get() = "removing $stateName"

    val ledgerServices = MockServices(listOf("center.orbita.callcenter.contract"))

    fun createCommandTransactionMustBeWellFormed() {
        createCommandTransactionMustBeWellFormed(createCommand, inputStateForTest)
    }

    fun createCommandTransactionMustBeWellFormed(command: CommandData, stateForTest: LinearState) {
        ledgerServices.ledger {

            // Wrong command
            transaction {
                createTransactionOutput()
                command(alicePublicKey, DummyCommandData)
                this.failsWith("")
            }

            // Input state present
            transaction {
                input(contractClassName, DummyState())
                command(alicePublicKey, command)
                createTransactionOutput()
                this.failsWith("No inputs should be consumed when $createAction")
            }

            // More than one output state present
            transaction {
                createTransactionOutput()
                createTransactionOutput()
                command(alicePublicKey, command)
                this.failsWith("One output should be produced when $createAction")
            }

            // Output of wrong class
            transaction {
                output(contractClassName, DummyState())
                command(alicePublicKey, command)
                this.failsWith("Output state should be an instance of ${stateForTest.javaClass.name}")
            }

            // Two signers
            transaction {
                createTransactionOutput()
                command(listOf(alicePublicKey, bobPublicKey), command)
                this.failsWith("There must be one signer")
            }

            // Everything is correct
            transaction {
                createTransactionOutput()
                command(alicePublicKey, command)
                this.verifies()
            }
        }
    }

    fun modifyCommandTransactionMustBeWellFormed() {
        modifyCommandTransactionMustBeWellFormed(editCommand)
    }

    fun modifyCommandTransactionMustBeWellFormed(command: CommandData) {
        println(command)

        ledgerServices.ledger {

            // Wrong command
            transaction {
                createTransactionOutput()
                command(alicePublicKey, DummyCommandData)
                this.failsWith("")
            }

            // No input state present
            transaction {
                command(alicePublicKey, command)
                createTransactionOutput()
                this.failsWith("One input should be consumed when $editAction")
            }

            // More than one input state present
            transaction {
                createTransactionInput()
                createTransactionInput()
                command(alicePublicKey, command)
                this.failsWith("One input should be consumed when $editAction")
            }

            // Input of wrong class
            transaction {
                input(contractClassName, DummyState())
                createTransactionOutput()
                command(alicePublicKey, command)
                this.failsWith("Input state should be an instance of ${inputStateForTest.javaClass.name}")
            }

            // No output state present
            transaction {
                createTransactionInput()
                command(alicePublicKey, command)
                this.failsWith("One output should be produced when $editAction")
            }

            // More than one output state present
            transaction {
                createTransactionInput()
                createTransactionOutput()
                createTransactionOutput()
                command(alicePublicKey, command)
                this.failsWith("One output should be produced when $editAction")
            }

            // Output of wrong class
            transaction {
                createTransactionInput()
                output(contractClassName, DummyState())
                command(alicePublicKey, command)
                this.failsWith("Output state should be an instance of ${inputStateForTest.javaClass.name}")
            }

            // Two signers
            transaction {
                createTransactionInput()
                createTransactionOutput()
                command(listOf(alicePublicKey, bobPublicKey), command)
                this.failsWith("There must be one signer")
            }

            // Everything is correct
            transaction {
                createTransactionInput()
                createTransactionOutput()
                command(alicePublicKey, command)
                this.verifies()
            }
        }
    }

    fun removeCommandTransactionMustBeWellFormed() {
        removeCommandTransactionMustBeWellFormed(removeCommand)
    }

    fun removeCommandTransactionMustBeWellFormed(command: CommandData) {
        ledgerServices.ledger {

            // Wrong command
            transaction {
                createTransactionInput()
                command(alicePublicKey, DummyCommandData)
                this.failsWith("")
            }

            // No input state
            transaction {
                command(alicePublicKey, command)
                createTransactionOutput()
                this.failsWith("One input should be consumed when $removeAction")
            }

            // No Output state
            transaction {
                createTransactionInput()
                createTransactionOutput()
                command(alicePublicKey, command)
                this.failsWith("No output states should be produced when $removeAction")
            }

            // Input of wrong class
            transaction {
                input(contractClassName, DummyState())
                command(alicePublicKey, command)
                this.failsWith("Input state should be an instance of ${inputStateForTest.javaClass.name}")
            }

            // Two signers
            transaction {
                createTransactionInput()
                command(listOf(alicePublicKey, bobPublicKey), command)
                this.failsWith("There must be one signer")
            }

            // Everything is correct
            transaction {
                createTransactionInput()
                command(alicePublicKey, command)
                this.verifies()
            }
        }
    }

    protected fun TransactionDSL<TransactionDSLInterpreter>.createTransactionInput() {
        input(contractClassName, inputStateForTest)
    }

    protected fun TransactionDSL<TransactionDSLInterpreter>.createTransactionOutput() {
        output(contractClassName, outputStateForTest)
    }
}