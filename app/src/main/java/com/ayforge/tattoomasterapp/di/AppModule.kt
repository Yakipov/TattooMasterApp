package com.ayforge.tattoomasterapp.di

import android.app.Application
import androidx.room.Room
import com.ayforge.tattoomasterapp.core.notifications.AppointmentReminderRescheduler
import com.ayforge.tattoomasterapp.core.session.SessionManager
import com.ayforge.tattoomasterapp.core.settings.LanguageManager
import com.ayforge.tattoomasterapp.core.settings.SettingsDataStore
import com.ayforge.tattoomasterapp.data.local.TattooMasterDatabase
import com.ayforge.tattoomasterapp.data.repository.*
import com.ayforge.tattoomasterapp.domain.repository.*
import com.ayforge.tattoomasterapp.domain.usecase.CheckClientExistsUseCase
import com.ayforge.tattoomasterapp.domain.usecase.GetCurrentUserUseCase
import com.ayforge.tattoomasterapp.presentation.appointment.AppointmentViewModel
import com.ayforge.tattoomasterapp.presentation.auth.SignInViewModel
import com.ayforge.tattoomasterapp.presentation.auth.SignUpViewModel
import com.ayforge.tattoomasterapp.presentation.clients.ClientDetailViewModel
import com.ayforge.tattoomasterapp.presentation.clients.ClientViewModel
import com.ayforge.tattoomasterapp.presentation.home.HomeViewModel
import com.ayforge.tattoomasterapp.presentation.income.IncomesViewModel
import com.ayforge.tattoomasterapp.presentation.profile.LanguageViewModel
import com.ayforge.tattoomasterapp.presentation.profile.NotificationSettingsViewModel
import com.ayforge.tattoomasterapp.presentation.profile.ProfileViewModel
import com.ayforge.tattoomasterapp.presentation.user.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Firebase
    single { FirebaseAuth.getInstance() }

    // Core
    single { SessionManager(get()) }
    single { SettingsDataStore(context = get()) }
    single { LanguageManager(get()) }

    // UserRepository
    single<UserRepository> {
        UserRepositoryImpl(
            firebaseAuth = get(),
            context = get()
        )
    }

    // Database
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
    single { get<TattooMasterDatabase>().incomeDao() }
    single { get<TattooMasterDatabase>().paymentMethodDao() }

    // Repositories
    single<ClientRepository> { ClientRepositoryImpl(get(), get()) }

    single<AppointmentRepository> {
        AppointmentRepositoryImpl(
            appointmentDao = get(),
            incomeDao = get(),
            sessionManager = get()
        )
    }

    single {
        AppointmentReminderRescheduler(
            context = get<Application>(),
            appointmentRepository = get(),
            settingsDataStore = get()
        )
    }



    single<IncomeRepository> { IncomeRepositoryImpl(get(), get()) }
    single<PaymentMethodRepository> { PaymentMethodRepositoryImpl(get(), get()) }

    // UseCases
    factory { GetCurrentUserUseCase(get()) }
    factory { CheckClientExistsUseCase(get()) }

    // ViewModels
    viewModel { UserViewModel(get()) }
    viewModel {
        HomeViewModel(
            userRepository = get(),
            reminderRescheduler = get()
        )
    }
    viewModel { SignUpViewModel(get(), get()) }
    viewModel { SignInViewModel(firebaseAuth = get(), sessionManager = get()) }
    viewModel {
        ProfileViewModel(
            firebaseAuth = get(),
            sessionManager = get(),
            settingsDataStore = get()
        )
    }
    viewModel { LanguageViewModel(get()) }
    viewModel { AppointmentViewModel(get(), get(), get()) }
    viewModel { ClientViewModel(get()) }
    viewModel { ClientDetailViewModel(get(), get()) }
    viewModel { IncomesViewModel(get()) }
    viewModel { NotificationSettingsViewModel(get()) }


}
