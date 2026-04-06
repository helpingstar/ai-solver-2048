package io.github.helpigstar.aisolver2048.ui.onboarding.feature

import android.os.Parcelable
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.helpigstar.aisolver2048.data.onboarding.datasource.disk.model.OnboardingStatus
import io.github.helpigstar.aisolver2048.data.onboarding.repository.OnboardingRepository
import io.github.helpigstar.aisolver2048.ui.platform.base.BaseViewModel
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) : BaseViewModel<WelcomeState, Unit, WelcomeAction>(
    initialState = WelcomeState(
        title = "Welcome",
        description = "앱 소개 화면입니다.",
        buttonText = "시작하기",
    ),
) {

    override fun handleAction(action: WelcomeAction) {
        when (action) {
            WelcomeAction.StartClick -> handleStartClick()
        }
    }

    private fun handleStartClick() {
        onboardingRepository.setOnboardingStatus(OnboardingStatus.COMPLETE)
    }
}

@Parcelize
data class WelcomeState(
    val title: String,
    val description: String,
    val buttonText: String,
) : Parcelable

sealed class WelcomeAction {
    data object StartClick : WelcomeAction()
}