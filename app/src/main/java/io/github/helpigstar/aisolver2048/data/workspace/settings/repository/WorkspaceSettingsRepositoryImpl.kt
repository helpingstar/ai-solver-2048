package io.github.helpigstar.aisolver2048.data.workspace.settings.repository

import io.github.helpigstar.aisolver2048.data.workspace.settings.datasource.disk.WorkspaceSettingsDiskSource
import io.github.helpigstar.aisolver2048.data.workspace.settings.repository.model.WorkspaceSettings
import javax.inject.Inject

class WorkspaceSettingsRepositoryImpl @Inject constructor(
    private val workspaceSettingsDiskSource: WorkspaceSettingsDiskSource,
) : WorkspaceSettingsRepository {
    override fun getWorkspaceSettings(): WorkspaceSettings =
        workspaceSettingsDiskSource.getWorkspaceSettings()

    override fun storeWorkspaceSettings(workspaceSettings: WorkspaceSettings) {
        workspaceSettingsDiskSource.storeWorkspaceSettings(workspaceSettings = workspaceSettings)
    }
}
