package io.github.helpigstar.aisolver2048.ui.workspace.feature.workspace

import android.os.Parcelable
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.helpigstar.aisolver2048.ui.platform.base.BaseViewModel
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

@HiltViewModel
class WorkspaceViewModel @Inject constructor() :
    BaseViewModel<WorkspaceState, Unit, WorkspaceAction>(
        initialState = WorkspaceState(
            title = "AI Solver 2048",
            description = "온보딩이 완료된 후 진입하는 메인 화면입니다.",
        ),
    ) {

    override fun handleAction(action: WorkspaceAction) = Unit
}

@Parcelize
data class WorkspaceState(
    val title: String,
    val description: String,
) : Parcelable

sealed class WorkspaceAction
