package leo.me.la.w2d1

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.SphericalUtil
import kotlinx.android.synthetic.main.activity_maps.distancetv
import kotlinx.android.synthetic.main.activity_maps.root
import kotlin.math.roundToInt


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val traveledPoints = mutableListOf<LatLng>()
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.lastLocation?.let {
                LatLng(it.latitude, it.longitude).let { latlng ->
                    mMap.apply {
                        animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f))
                        clear()
                        addMarker(
                            MarkerOptions()
                                .position(latlng)
                                .title("You are at ${latlng.latitude}, ${latlng.longitude}")
                        )
                    }
                    traveledPoints.add(latlng)
                    showDistance()
                }
            }
        }
    }

    private fun showDistance() {
        var distance = 0.0
        traveledPoints.forEachIndexed { index, latLng ->
            if (index != traveledPoints.size - 1) {
                distance += SphericalUtil.computeDistanceBetween(latLng, traveledPoints[index + 1])
            }
        }
        distancetv.text = "Traveled: ${distance.roundToInt()} m"
    }

    private val locationRequest = LocationRequest().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
            .getMapAsync(this)
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Snackbar.make(root, "Map loaded successfully", Snackbar.LENGTH_INDEFINITE)
            .apply {
                setAction("Dismiss") {
                    dismiss()
                }
                show()
            }
        if (isLocationPermissionGranted()) {
            startLocationUpdates()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            291 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (checkLocationPermission()) {
                        startLocationUpdates()
                    }
                }
                return
            }
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        if (!checkLocationPermission()) {
            // Should we show an explanation?
            if (
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Permission required")
                    .setMessage("This app requires location permissions")
                    .setPositiveButton("ok") { dialog, _ ->
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            this@MapsActivity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            291
                        )
                        dialog.dismiss()
                    }
                    .create()
                    .show()


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    291
                )
            }
            return false
        } else {
            return true
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startLocationUpdates() {
        if (checkLocationPermission()) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        }
    }
}
