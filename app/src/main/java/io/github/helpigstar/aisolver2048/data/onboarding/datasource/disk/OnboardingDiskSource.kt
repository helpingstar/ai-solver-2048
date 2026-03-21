package io.github.helpigstar.aisolver2048.data.onboarding.datasource.disk

import io.github.helpigstar.aisolver2048.data.onboarding.datasource.disk.model.OnboardingStatus
import kotlinx.coroutines.flow.Flow

interface OnboardingDiskSource {

    fun getOnboardingStatus(userId: String): OnboardingStatus?
    fun getOnboardingStatusFlow(userId: String): Flow<OnboardingStatus?>

    fun storeOnboardingStatus(userId: String, onboardingStatus: OnboardingStatus?)
}