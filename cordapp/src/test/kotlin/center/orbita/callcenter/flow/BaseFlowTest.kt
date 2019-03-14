package center.orbita.callcenter.flow

import net.corda.core.identity.Party
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.StartedMockNode
import org.junit.After
import org.junit.Before

abstract class BaseFlowTest {
    protected lateinit var network: MockNetwork
    protected lateinit var a: StartedMockNode
    protected lateinit var b: StartedMockNode
    protected lateinit var notary: StartedMockNode

    protected lateinit var aParty: Party
    protected lateinit var bParty: Party
    protected lateinit var notaryParty: Party

    @Before
    open fun setup() {
        network = MockNetwork(listOf("center.orbita.personaldata"))

        a = network.createPartyNode()
        aParty = a.info.legalIdentities.single()

        b = network.createPartyNode()
        bParty = b.info.legalIdentities.single()

        notary = network.defaultNotaryNode
        notaryParty = network.defaultNotaryIdentity

        network.runNetwork()
    }

    @After
    fun tearDown() {
        network.stopNodes()
    }
}