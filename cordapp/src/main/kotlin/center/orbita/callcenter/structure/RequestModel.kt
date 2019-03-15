package center.orbita.callcenter.structure

import center.orbita.callcenter.state.RequestState
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import net.corda.core.identity.Party
import net.corda.core.schemas.PersistentState
import net.corda.core.serialization.CordaSerializable
import java.util.Date
import java.util.UUID
import javax.persistence.Entity
import javax.persistence.Table

@CordaSerializable
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
data class RequestModel(
    val id: UUID? = null,
    val msisdn: String,
    val creationDate: Date
) : OrbitaModel, ConvertibleOrbitaModel<RequestModel, RequestState> {

    constructor(requestState: RequestState) : this(
            id = requestState.linearId.id,
            msisdn = requestState.msisdn,
            creationDate = requestState.creationDate
    )

    override fun convertFromState(state: RequestState) = RequestModel(state)

    override fun convertToState(participants: List<Party>) = RequestState(this, participants)
}

@Entity
@Table(name = "request_entity")
class RequestEntity(
    val id: UUID? = null,
    val msisdn: String? = null,
    val creationDate: Date? = null
) : PersistentState(), OrbitaEntity {

    constructor(requestState: RequestState) : this(
            id = requestState.linearId.id,
            msisdn = requestState.msisdn,
            creationDate = requestState.creationDate
    )
}
