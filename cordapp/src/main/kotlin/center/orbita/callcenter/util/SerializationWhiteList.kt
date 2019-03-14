package center.orbita.callcenter.util

import net.corda.core.serialization.SerializationWhitelist
import java.util.Date

class SerializationWhiteList : SerializationWhitelist {
    override val whitelist = listOf(Date::class.java)
}