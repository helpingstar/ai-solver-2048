package io.github.helpigstar.aisolver2048.data.workspace.settings.datasource.disk

import android.content.SharedPreferences
import io.github.helpigstar.aisolver2048.data.datasource.disk.BaseDiskSource
import io.github.helpigstar.aisolver2048.data.workspace.settings.repository.model.WorkspaceSettings

private const val SPAWN_TILE_ENABLED_KEY = "workspaceSettings_spawnTileEnabled"
private const val AUTO_ANALYZE_ENABLED_KEY = "workspaceSettings_autoAnalyzeEnabled"
private const val DEFAULT_SETTING_ENABLED = true

class WorkspaceSettingsDiskSourceImpl(
    sharedPreferences: SharedPreferences,
) : BaseDiskSource(sharedPreferences = sharedPreferences),
    WorkspaceSettingsDiskSource {

    override fun getWorkspaceSettings(): WorkspaceSettings =
        WorkspaceSettings(
            isSpawnTileEnabled = getBoolean(
                key = SPAWN_TILE_ENABLED_KEY,
                defaultValue = DEFAULT_SETTING_ENABLED,
            ),
            isAutoAnalyzeEnabled = getBoolean(
                key = AUTO_ANALYZE_ENABLED_KEY,
                defaultValue = DEFAULT_SETTING_ENABLED,
            ),
        )

    override fun storeWorkspaceSettings(workspaceSettings: WorkspaceSettings) {
        putBoolean(
            key = SPAWN_TILE_ENABLED_KEY,
            value = workspaceSettings.isSpawnTileEnabled,
        )
        putBoolean(
            key = AUTO_ANALYZE_ENABLED_KEY,
            value = workspaceSettings.isAutoAnalyzeEnabled,
        )
    }
}
