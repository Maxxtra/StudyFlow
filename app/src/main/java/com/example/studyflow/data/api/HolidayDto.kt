package com.example.studyflow.data.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HolidayDto(
    val date: String,           // "2026-01-01"
    val localName: String,      // "Anul Nou"
    val name: String,           // "New Year's Day"
    val countryCode: String,    // "RO"
    val fixed: Boolean = true,
    val global: Boolean = true,
    val counties: List<String>? = null,
    val launchYear: Int? = null,
    val types: List<String>? = null
)
