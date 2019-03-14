package center.orbita.callcenter.util

import co.paralleluniverse.fibers.Suspendable

/**
 * This interface designed to avoid necessity to add @Suspendable annotation to all methods those call subflows
 * Implementations of SuspendableWrapper must add @Suspendable to call() method
 */
interface SuspendableWrapper<out T> {
    @Suspendable
    fun call(): T
}