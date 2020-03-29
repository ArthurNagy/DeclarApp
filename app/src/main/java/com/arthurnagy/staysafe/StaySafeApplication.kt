package com.arthurnagy.staysafe

import android.app.Application
import androidx.room.Room
import com.arthurnagy.staysafe.core.PreferenceManager
import com.arthurnagy.staysafe.core.db.StaySafeDatabase
import com.arthurnagy.staysafe.feature.DocumentIdentifier
import com.arthurnagy.staysafe.feature.DocumentType
import com.arthurnagy.staysafe.feature.documentdetail.DocumentDetailViewModel
import com.arthurnagy.staysafe.feature.home.HomeViewModel
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.newdocument.certificate.employeedata.CertificateEmployeeDataViewModel
import com.arthurnagy.staysafe.feature.newdocument.certificate.employerdata.CertificateEmployerDataViewModel
import com.arthurnagy.staysafe.feature.newdocument.certificate.routedata.CertificateRouteDataViewModel
import com.arthurnagy.staysafe.feature.newdocument.signature.SignatureViewModel
import com.arthurnagy.staysafe.feature.newdocument.statement.personaldata.StatementPersonalDataViewModel
import com.arthurnagy.staysafe.feature.newdocument.statement.routedata.StatementRouteDataViewModel
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
        single { Room.databaseBuilder(androidContext(), StaySafeDatabase::class.java, "stay-safe-db").build() }
        factory { get<StaySafeDatabase>().statementDao() }
        factory { get<StaySafeDatabase>().certificateDao() }

        viewModel { HomeViewModel(statementDao = get(), certificateDao = get()) }

        viewModel { (documentType: DocumentType) -> NewDocumentViewModel(documentType, certificateDao = get(), statementDao = get()) }

        viewModel { (newDocumentViewModel: NewDocumentViewModel) -> StatementPersonalDataViewModel(newDocumentViewModel) }
        viewModel { (newDocumentViewModel: NewDocumentViewModel) -> StatementRouteDataViewModel(newDocumentViewModel, stringProvider = get()) }

        viewModel { (newDocumentViewModel: NewDocumentViewModel) -> CertificateEmployerDataViewModel(newDocumentViewModel) }
        viewModel { (newDocumentViewModel: NewDocumentViewModel) -> CertificateEmployeeDataViewModel(newDocumentViewModel) }
        viewModel { (newDocumentViewModel: NewDocumentViewModel) -> CertificateRouteDataViewModel(newDocumentViewModel) }

        viewModel { (newDocumentViewModel: NewDocumentViewModel) -> SignatureViewModel(newDocumentViewModel, stringProvider = get()) }

        viewModel { (documentIdentifier: DocumentIdentifier) -> DocumentDetailViewModel(documentIdentifier, statementDao = get(), certificateDao = get()) }
    }
}