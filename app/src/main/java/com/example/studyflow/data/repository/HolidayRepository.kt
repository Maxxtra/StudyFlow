package com.example.studyflow.data.repository

import com.example.studyflow.data.api.HolidayApiService
import com.example.studyflow.data.api.HolidayDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HolidayRepository(private val apiService: HolidayApiService) {

    private var cachedHolidays: List<HolidayDto> = emptyList()
    private var cacheYear: Int = 0

    suspend fun fetchHolidays(year: Int, countryCode: String = "RO"): Result<List<HolidayDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val holidays = apiService.getPublicHolidays(year, countryCode)
                cachedHolidays = holidays
                cacheYear = year
                Result.success(holidays)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun getTodayHoliday(): HolidayDto? {
        val today = LocalDate.now()
        val todayStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE)

        // Check if we need to fetch for current year
        if (cacheYear != today.year) {
            return null // Will be fetched by ViewModel
        }

        return cachedHolidays.find { it.date == todayStr }
    }

    fun isHolidayToday(): Boolean {
        return getTodayHoliday() != null
    }

    fun getCachedHolidays(): List<HolidayDto> = cachedHolidays
}
