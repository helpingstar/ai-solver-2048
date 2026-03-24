package io.github.helpigstar.aisolver2048.data.workspace.settings.repository.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.helpigstar.aisolver2048.data.workspace.settings.datasource.disk.WorkspaceSettingsDiskSource
import io.github.helpigstar.aisolver2048.data.workspace.settings.repository.WorkspaceSettingsRepository
import io.github.helpigstar.aisolver2048.data.workspace.settings.repository.WorkspaceSettingsRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkspaceSettingsRepositoryModule {
    @Provides
    @Singleton
    fun provideWorkspaceSettingsRepository(
        workspaceSettingsDiskSource: WorkspaceSettingsDiskSource,
    ): WorkspaceSettingsRepository =
        WorkspaceSettingsRepositoryImpl(
            workspaceSettingsDiskSource = workspaceSettingsDiskSource,
        )
}
