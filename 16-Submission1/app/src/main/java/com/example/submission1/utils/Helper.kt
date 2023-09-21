package com.example.submission1.utils

import android.content.Context
import android.location.Geocoder
import android.os.Build
import com.example.submission1.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object Helper {
    fun getToolbarHeight(context: Context?): Int? {
        val styledAttributes =
            context?.theme?.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
        val toolbarHeight = styledAttributes?.getDimension(0, 0f)?.toInt()
        styledAttributes?.recycle()
        return toolbarHeight
    }

    fun generateToken(token: String): String = "Bearer $token"

    private fun generateDiffTime(stringDate: String): ArrayList<Pair<String, Long>> {
        val currentDate = Date()
        val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        val oldDate = SimpleDateFormat(dateFormat, Locale.getDefault()).parse(stringDate)
        val currentTime = currentDate.time
        val oldTime = oldDate?.time
        val diffTime = currentTime - oldTime!!
        val seconds = diffTime / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val weeks = days / 7
        val months = days / 30
        val years = days / 365
        return arrayListOf(
            Pair("s", seconds),
            Pair("m", minutes),
            Pair("h", hours),
            Pair("d", days),
            Pair("w", weeks),
            Pair("mo", months),
            Pair("y", years)
        )
    }

    fun generateExactDiffTime(context: Context, stringDate: String): String {
        val diffTimeList = generateDiffTime(stringDate)
        val sortedDiffTimeList = diffTimeList.reversed()
        var found = false
        var timeString = "s"
        var timeNumber = 0L
        for (sortedDiffTime in sortedDiffTimeList) {
            val (timeStringTemp, timeNumberTemp) = sortedDiffTime
            if (timeNumberTemp != 0L && !found) {
                timeString = timeStringTemp
                timeNumber = timeNumberTemp
                found = true
            }
        }
        return when (timeString) {
            "s" -> context.getString(R.string.diff_time_seconds, timeNumber)
            "m" -> context.getString(R.string.diff_time_minutes, timeNumber)
            "h" -> context.getString(R.string.diff_time_hours, timeNumber)
            "d" -> context.getString(R.string.diff_time_days, timeNumber)
            "w" -> context.getString(R.string.diff_time_weeks, timeNumber)
            "mo" -> context.getString(R.string.diff_time_months, timeNumber)
            "y" -> context.getString(R.string.diff_time_years, timeNumber)
            else -> context.getString(R.string.diff_time_seconds, 0)
        }
    }

    fun generateLocation(context: Context, lat: Double, long: Double, city: (String) -> Unit) {
        var cityName: String?
        Geocoder(context, Locale("in"))
            .getAddress(lat, long) { address: android.location.Address? ->
                if (address != null) {
                    cityName = getCityFromAddressLine(address.getAddressLine(0))
                    city(cityName ?: "city")
                }
            }
    }

    private fun getCityFromAddressLine(addressLine: String): String {
        val arrayAddressLine = addressLine.split(',').toTypedArray()
        val reversedArrayAddressLine = arrayAddressLine.reversed()
        return reversedArrayAddressLine[2]
    }

    private fun Geocoder.getAddress(
        latitude: Double,
        longitude: Double,
        address: (android.location.Address?) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getFromLocation(latitude, longitude, 1) { address(it.firstOrNull()) }
        } else {
            try {
                @Suppress("DEPRECATION") address(
                    getFromLocation(
                        latitude,
                        longitude,
                        1
                    )?.firstOrNull()
                )
            } catch (e: Exception) {
                address(null)
            }
        }
    }
}