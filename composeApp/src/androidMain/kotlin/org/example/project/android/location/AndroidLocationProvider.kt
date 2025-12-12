package org.example.project.android.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine
import org.example.project.data.geo.Location
import org.example.project.data.geo.LocationProvider
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AndroidLocationProvider(private val context: Context) : LocationProvider {
    @SuppressLint("MissingPermission")
    override suspend fun getLastKnownLocation(): Location? = suspendCancellableCoroutine { cont ->
        val client = LocationServices.getFusedLocationProviderClient(context)
        client.lastLocation
            .addOnSuccessListener { loc ->
                if (cont.isActive) cont.resume(loc?.let { Location(it.latitude, it.longitude) })
            }
            .addOnFailureListener { e ->
                if (cont.isActive) cont.resumeWithException(e)
            }
    }
}
