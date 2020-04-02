package com.arthurnagy.staysafe

import android.app.Application
import androidx.room.Room
import com.arthurnagy.staysafe.core.PreferenceManager
import com.arthurnagy.staysafe.core.db.StaySafeDatabase
import com.arthurnagy.staysafe.feature.documentdetail.DocumentDetailViewModel
import com.arthurnagy.staysafe.feature.home.HomeViewModel
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.newdocument.signature.SignatureViewModel
import com.arthurnagy.staysafe.feature.newdocument.statement.personaldata.StatementPersonalDataViewModel
import com.arthurnagy.staysafe.feature.newdocument.statement.routedata.StatementRouteDataViewModel
import com.arthurnagy.staysafe.feature.newdocument.statement.routedata.motive.MotivePickerViewModel
import com.arthurnagy.staysafe.feature.shared.StringProvider
import com.arthurnagy.staysafe.feature.shared.ThemeHelper
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class StaySafeApplication : Application() {

    private val preferenceManager: PreferenceManager by inject()

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        startKoin {
            androidContext(this@StaySafeApplication)
            modules(appModule)
        }
        preferenceManager.theme?.let { ThemeHelper.applyTheme(it) }
    }

    private val appModule = module {
        factory { StringProvider(androidContext()) }

        single { PreferenceManager(androidContext()) }
        single { Room.databaseBuilder(androidContext(), StaySafeDatabase::class.java, "stay-safe-db").fallbackToDestructiveMigration().build() }
        factory { get<StaySafeDatabase>().statementDao() }

        viewModel { HomeViewModel(statementDao = get()) }

        factory { NewDocumentViewModel.Factory(statementDao = get()) }

        viewModel { (newDocumentViewModel: NewDocumentViewModel) -> StatementPersonalDataViewModel(newDocumentViewModel) }
        viewModel { (newDocumentViewModel: NewDocumentViewModel) -> StatementRouteDataViewModel(newDocumentViewModel, stringProvider = get()) }

        viewModel { (newDocumentViewModel: NewDocumentViewModel) -> MotivePickerViewModel(newDocumentViewModel) }

        viewModel { (newDocumentViewModel: NewDocumentViewModel) -> SignatureViewModel(newDocumentViewModel, stringProvider = get()) }

        viewModel { (documentId: Long) -> DocumentDetailViewModel(documentId, statementDao = get()) }
    }
}