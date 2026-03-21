package io.github.helpigstar.aisolver2048.data.onboarding.datasource.disk.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.helpigstar.aisolver2048.data.onboarding.datasource.disk.OnboardingDiskSource
import io.github.helpigstar.aisolver2048.data.onboarding.datasource.disk.OnboardingDiskSourceImpl
import kotlinx.serialization.json.Json
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object OnboardingDiskModule {
    @Provides
    @Singleton
    fun provideOnboardingDiskSource(
        sharedPreferences: SharedPreferences,
        json: Json,
    ): OnboardingDiskSource =
        OnboardingDiskSourceImpl(
            json = json,
            sharedPreferences = sharedPreferences,
        )
}