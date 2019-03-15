package center.orbita.callcenter.corda.service

import center.orbita.callcenter.corda.NodeRPCConnection
import center.orbita.callcenter.flow.CreateRequestFlow
import center.orbita.callcenter.flow.GetRequestByIdFlow
import center.orbita.callcenter.flow.ListRequestFlow
import center.orbita.callcenter.flow.ModifyRequestFlow
import center.orbita.callcenter.flow.RemoveRequestFlow
import center.orbita.callcenter.structure.RequestModel
import org.springframework.stereotype.Service

@Service
class RequestService(override val rpc: NodeRPCConnection) : AbstractStateManager<RequestModel>(
        list = ListRequestFlow::class.java,
        getById = GetRequestByIdFlow::class.java,
        create = CreateRequestFlow::class.java,
        remove = RemoveRequestFlow::class.java,
        modify = ModifyRequestFlow::class.java
)