package io.github.helpigstar.aisolver2048.data.onboarding.repository

import io.github.helpigstar.aisolver2048.data.onboarding.datasource.disk.model.OnboardingStatus
import io.github.helpigstar.aisolver2048.data.onboarding.repository.model.UserState
import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {
    val userStateFlow: Flow<UserState>
    fun setOnboardingStatus(status: OnboardingStatus)
}