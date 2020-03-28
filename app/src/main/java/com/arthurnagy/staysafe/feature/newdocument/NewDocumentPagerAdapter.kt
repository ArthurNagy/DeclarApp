package com.arthurnagy.staysafe.feature.newdocument

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.arthurnagy.staysafe.feature.DocumentType
import com.arthurnagy.staysafe.feature.newdocument.certificate.employeedata.CertificateEmployeeDataFragment
import com.arthurnagy.staysafe.feature.newdocument.certificate.routedata.CertificateRouteDataFragment
import com.arthurnagy.staysafe.feature.newdocument.signature.SignatureFragment
import com.arthurnagy.staysafe.feature.newdocument.statement.personaldata.StatementPersonalDataFragment
import com.arthurnagy.staysafe.feature.newdocument.statement.routedata.StatementRouteDataFragment

class NewDocumentPagerAdapter(fragmentManager: FragmentManager, private val documentType: DocumentType) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment = when (documentType) {
        DocumentType.STATEMENT -> when (position) {
            STATEMENT_PERSONAL_DATA_INDEX -> StatementPersonalDataFragment()
            STATEMENT_ROUTE_DATA_INDEX -> StatementRouteDataFragment()
            STATEMENT_SIGNATURE_INDEX -> SignatureFragment()
            else -> throw IllegalStateException("No other pages supported")
        }
        DocumentType.CERTIFICATE -> when (position) {
            CERTIFICATE_EMPLOYER_DATA_INDEX -> CertificateEmployeeDataFragment()
            CERTIFICATE_EMPLOYEE_DATA_INDEX -> CertificateEmployeeDataFragment()
            CERTIFICATE_ROUTE_DATA_INDEX -> CertificateRouteDataFragment()
            CERTIFICATE_SIGNATURE_INDEX -> SignatureFragment()
            else -> throw IllegalStateException("No other pages supported")
        }
    }

    override fun getCount(): Int = when (documentType) {
        DocumentType.STATEMENT -> STATEMENT_PAGE_COUNT
        DocumentType.CERTIFICATE -> CERTIFICATE_PAGE_COUNT
    }

    companion object {
        private const val STATEMENT_PAGE_COUNT = 3
        private const val CERTIFICATE_PAGE_COUNT = 4
        private const val STATEMENT_PERSONAL_DATA_INDEX = 0
        private const val STATEMENT_ROUTE_DATA_INDEX = 1
        private const val STATEMENT_SIGNATURE_INDEX = 2
        private const val CERTIFICATE_EMPLOYER_DATA_INDEX = 0
        private const val CERTIFICATE_EMPLOYEE_DATA_INDEX = 1
        private const val CERTIFICATE_ROUTE_DATA_INDEX = 2
        private const val CERTIFICATE_SIGNATURE_INDEX = 3
    }
}