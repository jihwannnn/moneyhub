package com.example.moneyhub.di


import com.example.moneyhub.data.repository.auth.AuthRepository
import com.example.moneyhub.data.repository.auth.AuthRepositoryImpl
import com.example.moneyhub.data.repository.auth.TestAuthRepository
import com.example.moneyhub.api.clovaocr.ClovaOcrApi
import com.example.moneyhub.data.repository.board.BoardRepository
import com.example.moneyhub.data.repository.board.TestBoardRepository
import com.example.moneyhub.data.repository.camera.OcrRepository
import com.example.moneyhub.data.repository.camera.OcrRepositoryImpl
import com.example.moneyhub.data.repository.group.GroupRepository
import com.example.moneyhub.data.repository.group.GroupRepositoryImpl
import com.example.moneyhub.data.repository.group.TestGroupRepository
import com.example.moneyhub.data.repository.transaction.TestTransactionRepository
import com.example.moneyhub.data.repository.transaction.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepository: AuthRepositoryImpl
    ): AuthRepository


    @Binds
    @Singleton
    abstract fun bindBoardRepository(
        boardRepository: TestBoardRepository
    ): BoardRepository

    @Binds
    @Singleton
    abstract fun bindGroupRepository(
        groupRepository: GroupRepositoryImpl
    ): GroupRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        transactionRepository: TestTransactionRepository
    ): TransactionRepository

}

@Module
@InstallIn(SingletonComponent::class)
abstract class OcrModule {
    @Binds
    abstract fun bindOcrRepository(
        impl: OcrRepositoryImpl
    ): OcrRepository
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://0uqgp61nqk.apigw.ntruss.com/custom/v1/36533/94b3814bb3fdbb6a5a7be1f643a4718bbacdcca2a0fda7d8a5166b176fb1502a/infer/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideClovaOcrApi(retrofit: Retrofit): ClovaOcrApi {
        return retrofit.create(ClovaOcrApi::class.java)
    }
}