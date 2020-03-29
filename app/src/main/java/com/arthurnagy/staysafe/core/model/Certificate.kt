package com.arthurnagy.staysafe.core.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Certificate(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "employer_first_name") val employerFirstName: String,
    @ColumnInfo(name = "employer_last_name") val employerLastName: String,
    @ColumnInfo(name = "employer_job_title") val employerJobTitle: String,
    @ColumnInfo(name = "company_name") val companyName: String,
    @ColumnInfo(name = "company_address") val companyAddress: String,
    @ColumnInfo(name = "employee_first_name") val employeeFirstName: String,
    @ColumnInfo(name = "employee_last_name") val employeeLastName: String,
    @ColumnInfo(name = "employee_job_title") val employeeJobTitle: String,
    @ColumnInfo(name = "employee_address") val employeeAddress: String,
    @ColumnInfo(name = "birth_date") val birthDate: Long,
    @ColumnInfo(name = "route") val route: String,
    @ColumnInfo(name = "transportation") val transportationMethod: String,
    @ColumnInfo(name = "from_date") val fromDate: Long,
    @ColumnInfo(name = "to_date") val toDate: Long,
    @ColumnInfo(name = "signature") val signaturePath: String
) : Document