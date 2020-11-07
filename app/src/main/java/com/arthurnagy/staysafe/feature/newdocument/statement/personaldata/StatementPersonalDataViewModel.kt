package com.arthurnagy.staysafe.feature.newdocument.statement.personaldata

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.arthurnagy.staysafe.feature.newdocument.NewDocumentViewModel
import com.arthurnagy.staysafe.feature.shared.formatToDate
import com.arthurnagy.staysafe.feature.shared.mediatorLiveData

class StatementPersonalDataViewModel(private val newDocumentViewModel: NewDocumentViewModel) : ViewModel() {

    private val pendingStatement: LiveData<NewDocumentViewModel.PendingStatement> get() = newDocumentViewModel.pendingStatement
    val lastName = mediatorLiveData("", pendingStatement) { currentValue, pendingStatement ->
        if (pendingStatement.lastName != currentValue) {
            pendingStatement.lastName
        } else {
            currentValue
        }
    }
    val firstName = mediatorLiveData("", pendingStatement) { currentValue, pendingStatement ->
        if (pendingStatement.firstName != currentValue) {
            pendingStatement.firstName
        } else {
            currentValue
        }
    }
    val location = mediatorLiveData("", pendingStatement) { currentValue, pendingStatement ->
        if (pendingStatement.location != currentValue) {
            pendingStatement.location
        } else {
            currentValue
        }
    }
    val currentLocation = mediatorLiveData("", pendingStatement) { currentValue, pendingStatement ->
        if (pendingStatement.currentLocation != currentValue) {
            pendingStatement.currentLocation
        } else {
            currentValue
        }
    }
    val birthdayLocation = mediatorLiveData("", pendingStatement) { currentValue, pendingStatement ->
        if (pendingStatement.birthdayLocation != currentValue) {
            pendingStatement.birthdayLocation
        } else {
            currentValue
        }
    }
    val birthDate: LiveData<Long?> = pendingStatement.map { it.birthDate }
    val birthDateFormatted: LiveData<String> = birthDate.map { it?.let { formatToDate(it) } ?: "" }
    val isNextEnabled: LiveData<Boolean> = pendingStatement.map(::areStatementPersonalDataValid)

    init {
        lastName.observeForever {
            if (pendingStatement.value?.lastName != it) {
                newDocumentViewModel.updateStatement { copy(lastName = it) }
            }
        }
        firstName.observeForever {
            if (pendingStatement.value?.firstName != it) {
                newDocumentViewModel.updateStatement { copy(firstName = it) }
            }
        }
        location.observeForever {
            if (pendingStatement.value?.location != it) {
                newDocumentViewModel.updateStatement { copy(location = it) }
            }
        }
        currentLocation.observeForever {
            if (pendingStatement.value?.currentLocation != it) {
                newDocumentViewModel.updateStatement { copy(currentLocation = it) }
            }
        }
        birthdayLocation.observeForever {
            if (pendingStatement.value?.birthdayLocation != it) {
                newDocumentViewModel.updateStatement { copy(birthdayLocation = it) }
            }
        }
    }

    private fun areStatementPersonalDataValid(pendingStatement: NewDocumentViewModel.PendingStatement) =
        !pendingStatement.firstName.isNullOrEmpty() && !pendingStatement.lastName.isNullOrEmpty() &&
            !pendingStatement.location.isNullOrBlank() && !pendingStatement.currentLocation.isNullOrBlank() &&
            pendingStatement.birthDate != null && !pendingStatement.birthdayLocation.isNullOrBlank()

    fun onBirthDateSelected(timestamp: Long) {
        newDocumentViewModel.updateStatement { copy(birthDate = timestamp) }
    }
}