package io.github.helpigstar.aisolver2048.ui.platform.feature.rootnav

import android.os.Parcelable
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.helpigstar.aisolver2048.data.onboarding.datasource.disk.model.OnboardingStatus
import io.github.helpigstar.aisolver2048.data.onboarding.repository.OnboardingRepository
import io.github.helpigstar.aisolver2048.data.onboarding.repository.model.UserState
import io.github.helpigstar.aisolver2048.ui.platform.base.BaseViewModel
import kotlinx.parcelize.Parcelize
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RootNavViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
) : BaseViewModel<RootNavState, Unit, RootNavAction>(
    initialState = RootNavState.Splash
) {
    init {
        onboardingRepository
            .userStateFlow
            .map { userState ->
                RootNavAction.Internal.UserStateUpdateReceive(userState = userState)
            }
            .onEach(::sendAction)
            .launchIn(viewModelScope)
    }

    override fun handleAction(action: RootNavAction) {
        when (action) {
            is RootNavAction.Internal.UserStateUpdateReceive -> {
                val nextState = when (action.userState.onboardingStatus) {
                    OnboardingStatus.COMPLETE -> RootNavState.Workspace
                    else -> RootNavState.Onboarding
                }
                mutableStateFlow.update { nextState }
            }
        }
    }
}


sealed class RootNavState : Parcelable {
    @Parcelize
    data object Splash : RootNavState()

    @Parcelize
    data object Onboarding : RootNavState()

    @Parcelize
    data object Workspace : RootNavState()
}

sealed class RootNavAction {
    sealed class Internal : RootNavAction() {
        data class UserStateUpdateReceive(
            val userState: UserState,
        ) : Internal()
    }

}