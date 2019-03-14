package center.orbita.callcenter.structure

import net.corda.core.contracts.LinearState
import net.corda.core.identity.Party
import net.corda.core.schemas.QueryableState
import java.time.Instant
import java.util.Date
import kotlin.reflect.full.declaredMemberProperties

interface OrbitaModel {
    fun toHashMap(): HashMap<String, Any> {
        val fields = this::class.declaredMemberProperties
        val map = fields.map {
            it.name to get(it.getter.call(this) ?: "")
        }.toMap()
        return HashMap(map)
    }
}

fun get(any: Any): Any {
    return if (any is OrbitaModel) any.toHashMap()
    else if (any is Iterable<*>) any.filter { it != null }.map { get(it!!) }
    else if (any is Instant) Date.from(any)
    else any
}

interface OrbitaState : LinearState, QueryableState
interface OrbitaEntity

interface ConvertibleOrbitaModel<out M : OrbitaModel, S : OrbitaState> {
    fun convertFromState(state: S): M

    fun convertToState(participants: List<Party>): S {
        throw NotImplementedError("not implemented")
    }
}

interface ConvertibleOrbitaState<M : OrbitaModel, E : OrbitaEntity, out S : OrbitaState> : OrbitaState {
    fun convertToModel(): M
    fun convertFromModel(model: M, participants: List<Party>): S
    fun convertToEntity(): E
}