package io.github.helpigstar.aisolver2048.data.onboarding.repository

import io.github.helpigstar.aisolver2048.data.onboarding.datasource.disk.model.OnboardingStatus

interface OnboardingRepository {
    fun setOnboardingStatus(status: OnboardingStatus) {

    }
}