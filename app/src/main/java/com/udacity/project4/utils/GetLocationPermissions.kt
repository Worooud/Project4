package com.udacity.project4.utils

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.BuildConfig
import com.udacity.project4.R



    private val REQUEST_LOCATION_PERMISSION = 1

    fun Activity.gotBaseLocationPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }
    fun Activity.requestLocationPermissions() {
        val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION)

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        ActivityCompat.requestPermissions(this,
            permissions.toTypedArray(), REQUEST_LOCATION_PERMISSION)
    }
    fun Activity.LocationRequest(resolve: Boolean = true): Task<LocationSettingsResponse> {
        val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve){
                try { exception.startResolutionForResult(this,
                    1)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d("LocSettingsResponse", "Error getting location settings resolution: " + sendEx.message)
                }
            } else {
                LocationRequest()
            }
        }
        return locationSettingsResponseTask
    }

    fun Activity.hasBaseLocationPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    fun Activity.showPermissionSnackBar(view: View){
        Snackbar.make(
            view,
            R.string.permission_denied_explanation, Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.settings) {
                // Displays App settings screen.
                startActivity(Intent().apply {
                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }.show()
    }

@TargetApi(Build.VERSION_CODES.R)
 fun gotAndroidRPermissions(activity: Activity): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return true
    return when {
        ActivityCompat.checkSelfPermission(activity,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED -> {
            true
        }
        ActivityCompat.shouldShowRequestPermissionRationale(activity,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION) -> {
            false
        }
        else -> false
    }
}


@TargetApi(Build.VERSION_CODES.Q)
public   fun gotAndroidQPermissions(activity: Activity): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return true
    return when {
        ActivityCompat.checkSelfPermission(activity,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED -> {
            true
        }
        else -> false
    }
}

