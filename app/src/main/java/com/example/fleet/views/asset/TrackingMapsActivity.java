package com.example.fleet.views.asset;

import android.content.pm.PackageManager;
import android.location.Location;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.fleet.R;
import com.example.fleet.utils.BaseActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class TrackingMapsActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnGroundOverlayClickListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private GroundOverlay mGroundOverlay;
    private GroundOverlay mGroundOverlayRotated;
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tracking_maps;
    }

    @Override
    protected void initViews() {

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

       /* mGroundOverlayRotated = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(mImages.get(1)).anchor(0, 1)
                .position(NEAR_NEWARK, 4300f, 3025f)
                .bearing(60)
                .clickable(((CheckBox) findViewById(R.id.toggleClickability)).isChecked()));*/

        /*// Add a large overlay at Newark on top of the smaller overlay.
        mGroundOverlay = mMap.addGroundOverlay(new GroundOverlayOptions()
                .image(mImages.get(mCurrentEntry)).anchor(0, 1)
                .position(NEWARK, 8600f, 6500f));*/

        LatLngBounds seasiaBounds = new LatLngBounds(
                new LatLng(30.710515, 76.708934),       // South west corner
                new LatLng(30.710680, 76.709151));      // North east corner

        GroundOverlayOptions buildingMap = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.floor_plan_north))
                .positionFromBounds(seasiaBounds);

        GroundOverlay imageOverlay = mMap.addGroundOverlay(buildingMap);


        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(new LatLng(30.71091, 76.70944),
                        new LatLng(30.71075, 76.70958),
                        new LatLng(30.71081, 76.70968),
                        new LatLng(30.71098, 76.70954),
                        new LatLng(30.71098, 76.70954)));

        // Instantiates a new Polygon object and adds points to define a rectangle
        PolygonOptions rectOptions = new PolygonOptions()
                .add(new LatLng(30.71091, 76.70944),
                        new LatLng(30.71075, 76.70958),
                        new LatLng(30.71081, 76.70968),
                        new LatLng(30.71098, 76.70954),
                        new LatLng(30.71098, 76.70954));

// Get back the mutable Polygon
        //Polygon polygon = mMap.addPolyline(polyline1);

        showMarkerWithInfoWindow();
        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localised.
        mMap.setContentDescription("Google Map with ground overlay.");
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(new LatLng( 30.710515,
                        76.708934));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

    }

    public void showMarkerWithInfoWindow() {

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(30.710515, 76.708934))
                .title("Warehouse")
              //  .snippet("Snippet")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.marker_small)));

        marker.showInfoWindow();
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                          //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                          //          new LatLng(mLastKnownLocation.getLatitude(),
                         //                   mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            // Log.d(TAG, "Current location is null. Using defaults.");
                            // Log.e(TAG, "Exception: %s", task.getException());
                           // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                          //  mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    @Override
    public void onGroundOverlayClick(GroundOverlay groundOverlay) {


    }
}
