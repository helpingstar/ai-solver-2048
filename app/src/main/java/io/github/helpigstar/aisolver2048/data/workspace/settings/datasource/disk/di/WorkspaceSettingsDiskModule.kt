package io.github.helpigstar.aisolver2048.data.workspace.settings.datasource.disk.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.helpigstar.aisolver2048.data.workspace.settings.datasource.disk.WorkspaceSettingsDiskSource
import io.github.helpigstar.aisolver2048.data.workspace.settings.datasource.disk.WorkspaceSettingsDiskSourceImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkspaceSettingsDiskModule {
    @Provides
    @Singleton
    fun provideWorkspaceSettingsDiskSource(
        sharedPreferences: SharedPreferences,
    ): WorkspaceSettingsDiskSource =
        WorkspaceSettingsDiskSourceImpl(
            sharedPreferences = sharedPreferences,
        )
}
