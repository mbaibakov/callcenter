package center.orbita.callcenter.util

import net.corda.core.flows.FlowException

/**
 * Returns the single element, or throws an exception if the list is empty or has more than one element.
 */
fun <T> List<T>.singleOrException(name: String): T {
    return when (size) {
        0 -> throw FlowException("Unable to find $name: list is empty")
        1 -> this[0]
        else -> throw FlowException("Unable to find $name: list  has more than one element")
    }
}

fun <T> List<T>.noneOrSingleOrException(name: String): T? {
    return when (size) {
        0 -> null
        1 -> this[0]
        else -> throw IllegalArgumentException("Failed to verify uniqueness of $name: list has more than one element")
    }
}