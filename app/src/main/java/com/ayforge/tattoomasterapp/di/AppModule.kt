package com.ayforge.tattoomasterapp.di

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ayforge.tattoomasterapp.core.session.SessionManager
import com.ayforge.tattoomasterapp.data.repository.UserRepositoryImpl
import com.ayforge.tattoomasterapp.domain.repository.UserRepository
import com.ayforge.tattoomasterapp.domain.usecase.GetCurrentUserUseCase
import com.ayforge.tattoomasterapp.presentation.auth.SignInViewModel
import org.koin.dsl.module
import com.ayforge.tattoomasterapp.presentation.user.UserViewModel
import com.ayforge.tattoomasterapp.presentation.home.HomeViewModel
import com.ayforge.tattoomasterapp.presentation.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.dsl.viewModel
import com.ayforge.tattoomasterapp.core.settings.LanguageManager
import com.ayforge.tattoomasterapp.core.settings.SettingsDataStore
import com.ayforge.tattoomasterapp.data.local.dao.AppointmentDao
import com.ayforge.tattoomasterapp.data.local.dao.ClientDao
import com.ayforge.tattoomasterapp.data.local.DateTimeConverters
import com.ayforge.tattoomasterapp.data.local.TattooMasterDatabase
import com.ayforge.tattoomasterapp.data.local.entity.AppointmentEntity
import com.ayforge.tattoomasterapp.data.local.entity.ClientEntity
import com.ayforge.tattoomasterapp.presentation.profile.LanguageViewModel
import com.ayforge.tattoomasterapp.data.repository.ClientRepositoryImpl
import com.ayforge.tattoomasterapp.domain.repository.ClientRepository
import com.ayforge.tattoomasterapp.domain.usecase.CheckClientExistsUseCase
import com.ayforge.tattoomasterapp.domain.repository.AppointmentRepository
import com.ayforge.tattoomasterapp.data.repository.AppointmentRepositoryImpl
import com.ayforge.tattoomasterapp.presentation.appointment.AppointmentViewModel

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single<UserRepository> { UserRepositoryImpl(firebaseAuth = get()) }
    single { SessionManager(get()) }
    single { SettingsDataStore(context = get()) }
    single { LanguageManager(context = get(), settingsDataStore = get()) }

    // Room database
    single {
        Room.databaseBuilder(
            get(),
            TattooMasterDatabase::class.java,
            "tattoo_master.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    // DAOs
    single { get<TattooMasterDatabase>().clientDao() }
    single { get<TattooMasterDatabase>().appointmentDao() }

    // Repositories
    single<ClientRepository> { ClientRepositoryImpl(get()) }
    single<AppointmentRepository> { AppointmentRepositoryImpl(get()) }

    // UseCases
    factory { GetCurrentUserUseCase(get()) }
    factory { CheckClientExistsUseCase(get()) }

    // ViewModels
    viewModel { UserViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { SignInViewModel(firebaseAuth = get(), sessionManager = get()) }
    viewModel { ProfileViewModel(firebaseAuth = get(), sessionManager = get()) }
    viewModel { LanguageViewModel(get()) }
    viewModel { AppointmentViewModel(get(), get()) }
}

