package com.reservahotel.reservasapplication.di

import com.reservahotel.reservasapplication.data.repository.AuthRepositoryImpl
import com.reservahotel.reservasapplication.data.repository.ClienteRepositoryImpl
import com.reservahotel.reservasapplication.data.repository.HabitacionRepositoryImpl
import com.reservahotel.reservasapplication.data.repository.ReservaRepositoryImpl
import com.reservahotel.reservasapplication.data.repository.FacturaRepositoryImpl
import com.reservahotel.reservasapplication.data.repository.ServicioRepositoryImpl
import com.reservahotel.reservasapplication.data.repository.UsuarioRepositoryImpl
import com.reservahotel.reservasapplication.domain.repository.AuthRepository
import com.reservahotel.reservasapplication.domain.repository.ClienteRepository
import com.reservahotel.reservasapplication.domain.repository.HabitacionRepository
import com.reservahotel.reservasapplication.domain.repository.ReservaRepository
import com.reservahotel.reservasapplication.domain.repository.FacturaRepository
import com.reservahotel.reservasapplication.domain.repository.ServicioRepository
import com.reservahotel.reservasapplication.domain.repository.UsuarioRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindHabitacionRepository(impl: HabitacionRepositoryImpl): HabitacionRepository

    @Binds @Singleton
    abstract fun bindReservaRepository(impl: ReservaRepositoryImpl): ReservaRepository

    @Binds @Singleton
    abstract fun bindUsuarioRepository(impl: UsuarioRepositoryImpl): UsuarioRepository

    @Binds @Singleton
    abstract fun bindClienteRepository(impl: ClienteRepositoryImpl): ClienteRepository

    @Binds @Singleton
    abstract fun bindServicioRepository(impl: ServicioRepositoryImpl): ServicioRepository

    @Binds @Singleton
    abstract fun bindFacturaRepository(impl: FacturaRepositoryImpl): FacturaRepository
}
