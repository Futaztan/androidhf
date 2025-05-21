package com.androidhf.data.datatypes

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Company(
    val id: Long = 0,
    val companyName: String,
    val companyCode: String
) {
    fun toEntity() : CompanyEntity {
        return CompanyEntity(
            id = this.id,
            companyName = this.companyName,
            companyCode = this.companyCode,
        )
    }
}
@Entity(tableName = "Companies")
data class CompanyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val companyName: String,
    val companyCode: String
){
    fun toDomain() : Company
    {
        return Company(
            id = this.id,
            companyName = this.companyName,
            companyCode = this.companyCode,
        )
    }
}