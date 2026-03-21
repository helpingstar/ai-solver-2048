package io.github.helpigstar.aisolver2048.data.onboarding.datasource.disk

import android.content.SharedPreferences
import io.github.helpigstar.aisolver2048.core.data.repository.util.bufferedMutableSharedFlow
import io.github.helpigstar.aisolver2048.core.data.util.decodeFromStringOrNull
import io.github.helpigstar.aisolver2048.data.datasource.disk.BaseDiskSource
import io.github.helpigstar.aisolver2048.data.onboarding.datasource.disk.model.OnboardingStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.serialization.json.Json

private const val ONBOARDING_STATUS_KEY = "onboardingStatus"

class OnboardingDiskSourceImpl(
    private val json: Json,
    sharedPreferences: SharedPreferences,
) : BaseDiskSource(sharedPreferences = sharedPreferences)
    ,OnboardingDiskSource {

    private val mutableOnboardingStatusFlowMap =
        mutableMapOf<String, MutableSharedFlow<OnboardingStatus?>>()

    override fun getOnboardingStatus(userId: String): OnboardingStatus? {
        return getString(key = ONBOARDING_STATUS_KEY.appendIdentifier(userId))?.let {
            json.decodeFromStringOrNull(it)
        }
    }

    override fun storeOnboardingStatus(userId: String, onboardingStatus: OnboardingStatus?) {
        putString(
            key = ONBOARDING_STATUS_KEY.appendIdentifier(userId),
            value = onboardingStatus?.let { json.encodeToString(it) },
        )
        getMutableOnboardingStatusFlow(userId = userId).tryEmit(onboardingStatus)
    }

    override fun getOnboardingStatusFlow(userId: String): Flow<OnboardingStatus?> {
        return getMutableOnboardingStatusFlow(userId = userId)
            .onSubscription { emit(getOnboardingStatus(userId = userId)) }
    }

    private fun getMutableOnboardingStatusFlow(
        userId: String,
    ): MutableSharedFlow<OnboardingStatus?> =
        mutableOnboardingStatusFlowMap.getOrPut(userId) {
            bufferedMutableSharedFlow(replay = 1)
        }


}
