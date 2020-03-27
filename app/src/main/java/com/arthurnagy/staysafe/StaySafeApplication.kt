package com.arthurnagy.staysafe

import android.app.Application
import androidx.room.Room
import com.arthurnagy.staysafe.core.PreferenceManager
import com.arthurnagy.staysafe.core.db.StaySafeDatabase
import com.arthurnagy.staysafe.feature.documentdetail.DocumentDetailViewModel
import com.arthurnagy.staysafe.feature.home.HomeViewModel
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.util.ThemeHelper
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class StaySafeApplication : Application() {

    private val preferenceManager: PreferenceManager by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@StaySafeApplication)
            modules(appModule)
        }
        preferenceManager.theme?.let { ThemeHelper.applyTheme(it) }
    }

    private val appModule = module {
        single { PreferenceManager(androidContext()) }
        single { Room.databaseBuilder(androidContext(), StaySafeDatabase::class.java, "stay-safe-db").build() }
        factory { get<StaySafeDatabase>().statementDao() }
        factory { get<StaySafeDatabase>().certificateDao() }

        viewModel { HomeViewModel() }
        viewModel { NewDocumentViewModel() }
        viewModel { DocumentDetailViewModel() }
    }
}