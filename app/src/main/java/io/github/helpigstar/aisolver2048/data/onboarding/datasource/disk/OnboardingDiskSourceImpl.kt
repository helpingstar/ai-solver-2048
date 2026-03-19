package io.github.helpigstar.aisolver2048.data.onboarding.datasource.disk

import io.github.helpigstar.aisolver2048.data.onboarding.datasource.disk.model.OnboardingStatus
import kotlinx.serialization.json.Json

private const val ONBOARDING_STATUS_KEY = "onboardingStatus"

class OnboardingDiskSourceImpl(
    private val json: Json
) : OnboardingDiskSource {
    override fun storeOnboardingStatus(userId: String, onboardingStatus: OnboardingStatus?) {
        putString(
            key = ONBOARDING_STATUS_KEY.appendIdentifier(userId),
            value = onboardingStatus?.let { json.encodeToString(it) },
        )
        getMutableOnboardingStatusFlow(userId = userId).tryEmit(onboardingStatus)
    }
}