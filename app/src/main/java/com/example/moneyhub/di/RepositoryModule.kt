package com.example.moneyhub.di


import com.example.moneyhub.data.repository.auth.AuthRepository
import com.example.moneyhub.data.repository.auth.TestAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepository: TestAuthRepository
    ): AuthRepository
}