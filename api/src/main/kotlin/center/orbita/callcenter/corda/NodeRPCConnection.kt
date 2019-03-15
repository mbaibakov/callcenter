package center.orbita.callcenter.corda

import net.corda.client.rpc.CordaRPCClient
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.utilities.NetworkHostAndPort

/**
 * Wraps a node RPC proxy.
 *
 * The RPC proxy is configured based on the properties in `application.yml`.
 *
 * @param host The host of the node we are connecting to.
 * @param rpcPort The RPC port of the node we are connecting to.
 * @param username The username for logging into the RPC client.
 * @param password The password for logging into the RPC client.
 * @property proxy The RPC proxy.
 */
open class NodeRPCConnection(val host: String, val username: String, val password: String, val rpcPort: Int) {
    val proxy: CordaRPCOps

    init {
        val rpcAddress = NetworkHostAndPort(host, rpcPort)
        val rpcClient = CordaRPCClient(rpcAddress)
        val rpcConnection = rpcClient.start(username, password)
        proxy = rpcConnection.proxy
    }
}