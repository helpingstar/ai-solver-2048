package io.github.helpigstar.aisolver2048.data.workspace.settings.repository

import io.github.helpigstar.aisolver2048.data.workspace.settings.repository.model.WorkspaceSettings

interface WorkspaceSettingsRepository {
    fun getWorkspaceSettings(): WorkspaceSettings

    fun storeWorkspaceSettings(workspaceSettings: WorkspaceSettings)
}
