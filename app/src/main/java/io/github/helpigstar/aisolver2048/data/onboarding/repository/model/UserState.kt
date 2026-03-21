package io.github.helpigstar.aisolver2048.data.onboarding.repository.model

import io.github.helpigstar.aisolver2048.data.onboarding.datasource.disk.model.OnboardingStatus

data class UserState(
    val onboardingStatus: OnboardingStatus = OnboardingStatus.NOT_STARTED
)