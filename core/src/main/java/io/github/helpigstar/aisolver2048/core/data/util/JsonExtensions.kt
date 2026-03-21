package io.github.helpigstar.aisolver2048.core.data.util

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

inline fun <reified T> Json.decodeFromStringOrNull(
    string: String,
): T? =
    try {
        decodeFromString(string = string)
    } catch (_: SerializationException) {
        null
    } catch (_: IllegalArgumentException) {
        null
    }