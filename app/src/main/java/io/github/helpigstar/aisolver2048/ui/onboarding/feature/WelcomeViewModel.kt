package io.github.helpigstar.aisolver2048.ui.onboarding.feature

import android.os.Parcelable
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.helpigstar.aisolver2048.data.onboarding.datasource.disk.model.OnboardingStatus
import io.github.helpigstar.aisolver2048.data.onboarding.repository.OnboardingRepository
import io.github.helpigstar.aisolver2048.ui.R.string.welcome_title_2
import io.github.helpigstar.aisolver2048.ui.platform.base.BaseViewModel
import io.github.helpigstar.aisolver2048.ui.platform.resource.AisolverDrawable
import io.github.helpigstar.aisolver2048.ui.platform.resource.AisolverString
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) : BaseViewModel<WelcomeState, WelcomeEvent, WelcomeAction>(
    initialState = WelcomeState(
        index = 0,
        pages = listOf(
            WelcomeState.WelcomeCard.CardOne,
            WelcomeState.WelcomeCard.CardTwo,
            WelcomeState.WelcomeCard.CardThree,
        )
    ),
) {

    override fun handleAction(action: WelcomeAction) {
        when (action) {
            is WelcomeAction.PagerSwipe -> handlePagerSwipe(action)
            is WelcomeAction.DotClick -> handleDotClick(action)
            WelcomeAction.StartClick -> handleStartClick()
        }
    }

    private fun handlePagerSwipe(action: WelcomeAction.PagerSwipe) {
        mutableStateFlow.update { it.copy(index = action.index) }
    }

    private fun handleDotClick(action: WelcomeAction.DotClick) {
        mutableStateFlow.update { it.copy(index = action.index) }
        sendEvent(WelcomeEvent.UpdatePager(index = action.index))
    }

    private fun handleStartClick() {
        onboardingRepository.setOnboardingStatus(OnboardingStatus.COMPLETE)
    }
}

@Parcelize
data class WelcomeState(
    val index: Int,
    val pages: List<WelcomeCard>
) : Parcelable {
    sealed class WelcomeCard : Parcelable {
        abstract val imageRes: Int
        abstract val titleRes: Int
        abstract val messageRes: Int

        @Parcelize
        data object CardOne : WelcomeCard() {
            override val imageRes: Int get() = AisolverDrawable.ill_1
            override val titleRes: Int get() = AisolverString.welcome_title_1
            override val messageRes: Int get() = AisolverString.welcome_message_1
        }

        @Parcelize
        data object CardTwo : WelcomeCard() {
            override val imageRes: Int get() = AisolverDrawable.ill_2
            override val titleRes: Int get() = welcome_title_2
            override val messageRes: Int get() = AisolverString.welcome_message_2
        }

        @Parcelize
        data object CardThree : WelcomeCard() {
            override val imageRes: Int get() = AisolverDrawable.ill_3
            override val titleRes: Int get() = AisolverString.welcome_title_3
            override val messageRes: Int get() = AisolverString.welcome_message_3
        }
    }
}

sealed class WelcomeEvent {
    data class UpdatePager(
        val index: Int,
    ) : WelcomeEvent()
}

sealed class WelcomeAction {
    data class PagerSwipe(
        val index: Int
    ) : WelcomeAction()

    data class DotClick(val index: Int) : WelcomeAction()

    data object StartClick : WelcomeAction()
}
