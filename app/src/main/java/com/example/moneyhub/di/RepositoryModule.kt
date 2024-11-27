package com.example.moneyhub.di

import com.example.moneyhub.activity.postonboard.PostRepository
import com.example.moneyhub.activity.postonboard.PostRepository1
import com.example.moneyhub.data.repository.SignUpRepository
import com.example.moneyhub.data.repository.TestSignUpRepository
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
    abstract fun bindSignUpRepository(
        signUpRepository: TestSignUpRepository
    ): SignUpRepository

    @Binds
    @Singleton
    abstract fun bindPostRepository(
        postRepository: PostRepository1
    ): PostRepository
}