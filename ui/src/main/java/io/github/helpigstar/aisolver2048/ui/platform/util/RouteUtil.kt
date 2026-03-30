package io.github.helpigstar.aisolver2048.ui.platform.util

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

fun <T : Any> T.toObjectNavigationRoute(): String = this::class.toObjectKClassNavigationRoute()

@OptIn(InternalSerializationApi::class)
fun <T : Any> KClass<T>.toObjectKClassNavigationRoute(): String =
    this.serializer().descriptor.serialName
