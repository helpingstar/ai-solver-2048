package io.github.helpigstar.aisolver2048.data.onboarding.repository

import io.github.helpigstar.aisolver2048.data.onboarding.datasource.disk.OnboardingDiskSource
import io.github.helpigstar.aisolver2048.data.onboarding.datasource.disk.model.OnboardingStatus
import io.github.helpigstar.aisolver2048.data.onboarding.repository.model.UserState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val LOCAL_USER_ID = "offline-user"

class OnboardingRepositoryImpl @Inject constructor(
    private val onboardingDiskSource: OnboardingDiskSource,
) : OnboardingRepository {
    override val userStateFlow: Flow<UserState> =
        onboardingDiskSource
            .getOnboardingStatusFlow(userId = LOCAL_USER_ID)
            .map { status ->
                UserState(
                    onboardingStatus = status ?: OnboardingStatus.NOT_STARTED,
                )
            }
            .distinctUntilChanged()

    override fun setOnboardingStatus(status: OnboardingStatus) {
        onboardingDiskSource.storeOnboardingStatus(
            userId = LOCAL_USER_ID,
            onboardingStatus = status
        )
    }
}
