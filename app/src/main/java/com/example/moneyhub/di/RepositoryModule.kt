package com.example.moneyhub.di


import com.example.moneyhub.data.repository.auth.AuthRepository
import com.example.moneyhub.data.repository.auth.TestAuthRepository
import com.example.moneyhub.activity.postonboard.PostRepository
import com.example.moneyhub.activity.postonboard.PostRepository1
import com.example.moneyhub.data.repository.board.BoardRepository
import com.example.moneyhub.data.repository.board.TestBoardRepository
import com.example.moneyhub.data.repository.group.GroupRepository
import com.example.moneyhub.data.repository.group.TestGroupRepository
import com.example.moneyhub.data.repository.transaction.TestTransactionRepository
import com.example.moneyhub.data.repository.transaction.TransactionRepository
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

    @Binds
    @Singleton
    abstract fun bindPostRepository(
        postRepository: PostRepository1
    ): PostRepository

    @Binds
    @Singleton
    abstract fun bindBoardRepository(
        boardRepository: TestBoardRepository
    ): BoardRepository

    @Binds
    @Singleton
    abstract fun bindGroupRepository(
        groupRepository: TestGroupRepository
    ): GroupRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        transactionRepository: TestTransactionRepository
    ): TransactionRepository

}