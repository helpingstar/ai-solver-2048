package io.github.helpigstar.aisolver2048.data.workspace.manager.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.helpigstar.aisolver2048.data.workspace.inference.WorkspaceInferenceRunner
import io.github.helpigstar.aisolver2048.data.workspace.manager.WorkspaceManager
import io.github.helpigstar.aisolver2048.data.workspace.manager.WorkspaceManagerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkspaceManagerModule {

    @Provides
    @Singleton
    fun provideWorkspaceManager(
        workspaceInferenceRunner: WorkspaceInferenceRunner,
    ): WorkspaceManager =
        WorkspaceManagerImpl(
            workspaceInferenceRunner = workspaceInferenceRunner,
        )
}
