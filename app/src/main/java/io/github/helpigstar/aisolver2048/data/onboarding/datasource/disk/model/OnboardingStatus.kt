package io.github.helpigstar.aisolver2048.data.onboarding.datasource.disk.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class OnboardingStatus {
    @SerialName("notStarted")
    NOT_STARTED,

    @SerialName("complete")
    COMPLETE
}