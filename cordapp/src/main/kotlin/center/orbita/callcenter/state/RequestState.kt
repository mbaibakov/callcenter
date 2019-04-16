package center.orbita.callcenter.state

import center.orbita.callcenter.contract.RequestContract
import center.orbita.callcenter.structure.ConvertibleOrbitaState
import center.orbita.callcenter.structure.RequestEntity
import center.orbita.callcenter.structure.RequestModel
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.serialization.CordaSerializable
import java.util.Date
import java.util.UUID

@CordaSerializable
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@BelongsToContract(RequestContract::class)
data class RequestState(
    override val linearId: UniqueIdentifier,
    override val participants: List<AbstractParty>,
    val msisdn: String,
    val creationDate: Date,
    val responseData: String
) : ConvertibleOrbitaState<RequestModel, RequestEntity, RequestState> {

    constructor(requestModel: RequestModel, participants: List<AbstractParty>) : this(
            linearId = UniqueIdentifier(id = requestModel.id ?: UUID.randomUUID()),
            participants = participants,
            msisdn = requestModel.msisdn,
            creationDate = requestModel.creationDate,
            responseData = requestModel.responseData
    )

    object RequestSchemaV1 : MappedSchema(RequestState::class.java, 1, listOf(RequestEntity::class.java))

    override fun convertToModel() = RequestModel(this)

    override fun convertFromModel(model: RequestModel, participants: List<Party>) = model.convertToState(participants)

    override fun convertToEntity() = RequestEntity(this)

    override fun generateMappedObject(schema: MappedSchema) = convertToEntity()

    override fun supportedSchemas() = listOf(RequestSchemaV1)
}