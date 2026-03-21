package io.github.helpigstar.aisolver2048.data.onboarding.repository.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.helpigstar.aisolver2048.data.onboarding.datasource.disk.OnboardingDiskSource
import io.github.helpigstar.aisolver2048.data.onboarding.repository.OnboardingRepository
import io.github.helpigstar.aisolver2048.data.onboarding.repository.OnboardingRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnboardingRepositoryModule {

    @Provides
    @Singleton
    fun provideOnboardingRepository(
        onboardingDiskSource: OnboardingDiskSource,
    ): OnboardingRepository =
        OnboardingRepositoryImpl(
            onboardingDiskSource = onboardingDiskSource,
        )
}