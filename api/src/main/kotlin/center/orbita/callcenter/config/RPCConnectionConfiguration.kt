package center.orbita.callcenter.config

import center.orbita.callcenter.corda.NodeRPCConnection
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class RPCConnectionConfiguration {

    @Bean("agent-node")
    open fun createAgentNodeRPC(@Value("\${$NODE_PREFIX.$CORDA_NODE_HOST}") host: String,
        @Value("\${$NODE_PREFIX.$CORDA_USER_NAME}") username: String,
        @Value("\${$NODE_PREFIX.$CORDA_USER_PASSWORD}") password: String,
        @Value("\${$NODE_PREFIX.$CORDA_RPC_PORT}") rpcPort: Int): NodeRPCConnection {
        return NodeRPCConnection(host, username, password, rpcPort)
    }
}