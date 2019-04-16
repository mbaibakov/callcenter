package center.orbita.callcenter.flow

import net.corda.core.identity.Party
import net.corda.testing.common.internal.testNetworkParameters
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkParameters
import net.corda.testing.node.StartedMockNode
import net.corda.testing.node.TestCordapp
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
        network = MockNetwork(MockNetworkParameters(cordappsForAllNodes = listOf(TestCordapp.findCordapp("center.orbita.callcenter")), networkParameters = testNetworkParameters(minimumPlatformVersion = 4)))

        a = network.createPartyNode()
        aParty = a.info.legalIdentities.single()

        b = network.createPartyNode()
        bParty = b.info.legalIdentities.single()

        notary = network.defaultNotaryNode
        notaryParty = network.defaultNotaryIdentity

        listOf(a, b).forEach{it.registerInitiatedFlow(BaseReceivingFlow::class.java)}

        network.runNetwork()
    }

    @After
    fun tearDown() {
        network.stopNodes()
    }
}