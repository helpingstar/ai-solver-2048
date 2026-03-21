package io.github.helpigstar.aisolver2048.data.datasource.disk

import android.content.SharedPreferences
import androidx.core.content.edit

abstract class BaseDiskSource(
    private val sharedPreferences: SharedPreferences
) {
    protected fun putString(
        key: String,
        value: String?,
    ): Unit = sharedPreferences.edit() { putString(key.withBase(), value) }

    protected fun getString(
        key: String,
    ): String? = sharedPreferences.getString(key.withBase(), null)

    protected fun String.appendIdentifier(identifier: String): String = "${this}_$identifier"
}

private fun String.withBase(): String = "asPreferencesStorage:$this"