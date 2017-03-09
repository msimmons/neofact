package com.cinchfinancial.neofact.dsl

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Created by mark on 3/4/17.
 */
class InputDelegate<T, V>(val formula: () -> V) {

    lateinit var name: String

    operator fun provideDelegate(thisRef: T, prop: KProperty<*>): ReadOnlyProperty<T, V> {
        name = prop.name
        return object : ReadOnlyProperty<T, V> {
            override fun getValue(thisRef: T, property: KProperty<*>): V {
                return formula()
            }
        }
    }
}