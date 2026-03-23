package io.github.helpigstar.aisolver2048.data.workspace.inference.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.helpigstar.aisolver2048.data.workspace.inference.LiteRtWorkspaceInferenceRunner
import io.github.helpigstar.aisolver2048.data.workspace.inference.WorkspaceInferenceRunner
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkspaceInferenceModule {

    @Provides
    @Singleton
    fun provideWorkspaceInferenceRunner(
        application: Application,
    ): WorkspaceInferenceRunner =
        LiteRtWorkspaceInferenceRunner(
            application = application,
        )
}
