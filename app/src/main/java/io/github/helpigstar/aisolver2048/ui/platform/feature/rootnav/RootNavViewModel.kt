package io.github.helpigstar.aisolver2048.ui.platform.feature.rootnav

import android.os.Parcelable
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.helpigstar.aisolver2048.ui.platform.base.BaseViewModel
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

@HiltViewModel
class RootNavViewModel @Inject constructor(

) : BaseViewModel<RootNavState, Unit, RootNavAc>


sealed class RootNavState : Parcelable {
    @Parcelize
    data object Splash : RootNavState()
}