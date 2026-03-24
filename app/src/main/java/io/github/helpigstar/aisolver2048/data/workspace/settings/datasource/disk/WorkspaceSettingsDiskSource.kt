package io.github.helpigstar.aisolver2048.data.workspace.settings.datasource.disk

import io.github.helpigstar.aisolver2048.data.workspace.settings.repository.model.WorkspaceSettings

interface WorkspaceSettingsDiskSource {
    fun getWorkspaceSettings(): WorkspaceSettings

    fun storeWorkspaceSettings(workspaceSettings: WorkspaceSettings)
}
