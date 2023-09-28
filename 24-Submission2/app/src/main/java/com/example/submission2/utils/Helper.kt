package com.example.submission2.utils

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.airbnb.lottie.BuildConfig
import com.example.submission2.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
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
        var timeNumber = 0
        for (sortedDiffTime in sortedDiffTimeList) {
            val (timeStringTemp, timeNumberTemp) = sortedDiffTime
            if (timeNumberTemp != 0L && !found) {
                timeString = timeStringTemp
                timeNumber = timeNumberTemp.toInt()
                found = true
            }
        }
        return when (timeString) {
            "s" -> context.resources.getQuantityString(
                R.plurals.diff_time_seconds,
                timeNumber, timeNumber
            )

            "m" -> context.resources.getQuantityString(
                R.plurals.diff_time_minutes,
                timeNumber, timeNumber
            )

            "h" -> context.resources.getQuantityString(
                R.plurals.diff_time_hours,
                timeNumber, timeNumber
            )

            "d" -> context.resources.getQuantityString(
                R.plurals.diff_time_days,
                timeNumber,
                timeNumber
            )

            "w" -> context.resources.getQuantityString(
                R.plurals.diff_time_weeks,
                timeNumber, timeNumber
            )

            "mo" -> context.resources.getQuantityString(
                R.plurals.diff_time_months,
                timeNumber, timeNumber
            )

            "y" -> context.resources.getQuantityString(
                R.plurals.diff_time_years,
                timeNumber, timeNumber
            )

            else -> context.resources.getQuantityString(R.plurals.diff_time_seconds, 0, 0)
        }
    }

    object ImageFile {
        private var timeStamp: String =
            SimpleDateFormat(Constant.IMAGE_FILENAME_FORMAT, Locale.getDefault()).format(Date())

        fun getImageUri(context: Context): Uri {
            timeStamp =
                SimpleDateFormat(Constant.IMAGE_FILENAME_FORMAT, Locale.getDefault()).format(Date())
            var uri: Uri? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
                }
                uri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )
            }
            return uri ?: getImageUriForPreQ(context)
        }

        private fun getImageUriForPreQ(context: Context): Uri {
            val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")
            if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
            return FileProvider.getUriForFile(
                context,
                "${BuildConfig.LIBRARY_PACKAGE_NAME}.fileprovider",
                imageFile
            )
        }

        fun uriToFile(imageUri: Uri, context: Context): File {
            val myFile = createCustomTempFile(context)
            val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
            val outputStream = FileOutputStream(myFile)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(
                buffer,
                0,
                length
            )
            outputStream.close()
            inputStream.close()
            return myFile.reduceFileImage()
        }

        private fun createCustomTempFile(context: Context): File {
            val filesDir = context.externalCacheDir
            return File.createTempFile(timeStamp, ".jpg", filesDir)
        }

        private fun File.reduceFileImage(): File {
            val file = this
            val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)
            var compressQuality = 100
            var streamLength: Int
            do {
                val bmpStream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
                val bmpPicByteArray = bmpStream.toByteArray()
                streamLength = bmpPicByteArray.size
                compressQuality -= 5
            } while (streamLength > Constant.IMAGE_MAX_SIZE)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
            return file
        }

        private fun Bitmap.getRotatedBitmap(file: File): Bitmap? {
            val orientation = ExifInterface(file).getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
            )
            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
                ExifInterface.ORIENTATION_NORMAL -> this
                else -> this
            }
        }

        private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(
                source, 0, 0, source.width, source.height, matrix, true
            )
        }
    }

    object Location {
        fun generateLocation(context: Context, lat: Double, long: Double, city: (String) -> Unit) {
            var cityName: String?
            Geocoder(context, Locale.getDefault())
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
            return reversedArrayAddressLine[2].trim()
        }

        fun checkPermissions(context: Context): Boolean {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
            return false
        }

        fun isLocationEnabled(context: Context): Boolean {
            val locationManager: LocationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
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
}