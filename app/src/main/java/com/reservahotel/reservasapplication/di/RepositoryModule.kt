package com.reservahotel.reservasapplication.di

import com.reservahotel.reservasapplication.data.repository.AuthRepositoryImpl
import com.reservahotel.reservasapplication.data.repository.CategoryRepositoryImpl
import com.reservahotel.reservasapplication.data.repository.HabitacionRepositoryImpl
import com.reservahotel.reservasapplication.domain.repository.AuthRepository
import com.reservahotel.reservasapplication.domain.repository.CategoryRepository
import com.reservahotel.reservasapplication.domain.repository.HabitacionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl,
    ): AuthRepository

    @Binds @Singleton
    abstract fun bindHabitacionRepository(
        impl: HabitacionRepositoryImpl,
    ): HabitacionRepository

    @Binds @Singleton
    abstract fun bindCategoryRepository(
        impl: CategoryRepositoryImpl,
    ): CategoryRepository
}
