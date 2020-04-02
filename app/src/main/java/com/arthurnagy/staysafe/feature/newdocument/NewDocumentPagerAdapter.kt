package com.arthurnagy.staysafe.feature.newdocument

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.arthurnagy.staysafe.feature.newdocument.signature.SignatureFragment
import com.arthurnagy.staysafe.feature.newdocument.statement.personaldata.StatementPersonalDataFragment
import com.arthurnagy.staysafe.feature.newdocument.statement.routedata.StatementRouteDataFragment

class NewDocumentPagerAdapter(fragmentManager: FragmentManager) :
    FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment = when (position) {
        STATEMENT_PERSONAL_DATA_INDEX -> StatementPersonalDataFragment()
        STATEMENT_ROUTE_DATA_INDEX -> StatementRouteDataFragment()
        STATEMENT_SIGNATURE_INDEX -> SignatureFragment()
        else -> throw IllegalStateException("No other pages supported")
    }

    override fun getCount(): Int = STATEMENT_PAGE_COUNT

    companion object {
        const val STATEMENT_PAGE_COUNT = 3
        const val STATEMENT_PERSONAL_DATA_INDEX = 0
        const val STATEMENT_ROUTE_DATA_INDEX = 1
        const val STATEMENT_SIGNATURE_INDEX = 2
        const val CERTIFICATE_EMPLOYER_DATA_INDEX = 0
    }
}