package com.example.pogo2d;

import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.compat.GeoDataClient;
import com.google.android.libraries.places.compat.PlaceDetectionClient;
import com.google.android.libraries.places.compat.Places;

import java.util.ArrayList;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {


    public final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int DEFAULT_ZOOM = 15;
    private static final String TAG = MapActivity.class.getSimpleName();
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private GoogleMap mMap;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    //private Location mLastKnownLocation;

    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Location mLocation;

    private ArrayList<LocatedPokemon> locatedPokemons = new ArrayList<>();

    private Marker markerAsh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        mSettingsClient = LocationServices.getSettingsClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.i(TAG, "ENTERED LocationCallback::onLocationResult");
                super.onLocationResult(locationResult);
                mLocation = locationResult.getLastLocation();
                updateMap();
                // capture à faire ici
                Log.i("Location1", mLocation.toString());
            }
        };

        // set up the request. note that you can use setNumUpdates(1) and
        // setInterval(0) to get one request.
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
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
        //updateLocationUI();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        /* à réactiver et moduler les dimensions des sprites en fonction du zoom */
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(false);

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        startLocationUpdates();
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
                mLocation = null;
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
                            mLocation = (Location) task.getResult();
                            initMap();

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private ArrayList<LocatedPokemon> computePokemonsOnMap(
            Location location,
            int nombre,
            double range) {

        ArrayList<LocatedPokemon> pokemons = new ArrayList<>();
        ArrayList<LatLng> points = new ArrayList<>();

        int nombrePokeExistants = Pokemon.getPokemons().size();

        for (int i = 0; i < nombre; i++) {
            int alea = (int) (Math.random() * nombrePokeExistants);
            LatLng ll = computeRandomInRangeWhichDoesntAlreadyExist(range, points, location);

            points.add(ll);
            pokemons.add(new LocatedPokemon(
                    alea,
                    ll.latitude,
                    ll.longitude));
        }

        return pokemons;
    }


    private LatLng computeRandomInRangeWhichDoesntAlreadyExist(
            double range,
            ArrayList<LatLng> points,
            Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        LatLng ll;
        Boolean estTropPres = false;
        do {
            final double newLatitude = computeRandomInRange(latitude, range);
            final double newLongitude = computeRandomInRange(longitude, range);

            estTropPres = points.stream()
                    .map(e -> {
                        double dist = (e.latitude * e.latitude + e.longitude * e.longitude) -
                                (newLatitude * newLatitude + newLongitude * newLongitude);
                        Log.i("DIST POKE", dist + "");
                        return Math.abs(dist);
                    }).anyMatch(e -> (e < 0.1));

            ll = new LatLng(newLatitude, newLongitude);
        } while (estTropPres);

        return ll;
    }


    private double computeRandomInRange(double n, double range) {
        return (n - range) + (Math.random() * (2. * range));
    }

    private void addPokemonsOnMap(double scale) {
        for (LocatedPokemon p : locatedPokemons) {
            String cheminImageDuPokemon = p.getFichier().getAbsolutePath();
            Bitmap imageDuPokemon = BitmapFactory.decodeFile(cheminImageDuPokemon);
            imageDuPokemon = Bitmap
                    .createScaledBitmap(imageDuPokemon,
                            (int) (imageDuPokemon.getWidth() * scale),
                            (int) (imageDuPokemon.getHeight() * scale),
                            false);

            mMap.addMarker(new MarkerOptions()
                    .title(p.getNom())
                    .position(new LatLng(
                            p.getLatitude(),
                            p.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromBitmap(imageDuPokemon)));
        }
    }

    private void addAshOnMap(double scale) {
        String ashPath = Ash.getFichier().getAbsolutePath();
        Bitmap ashImg = BitmapFactory.decodeFile(ashPath);

        ashImg = Bitmap
                .createScaledBitmap(ashImg,
                        (int) (ashImg.getWidth() * scale),
                        (int) (ashImg.getHeight() * scale),
                        false);

        markerAsh = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(
                        mLocation.getLatitude(),
                        mLocation.getLongitude()))
                .icon(BitmapDescriptorFactory.fromBitmap(ashImg)));
    }

    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "Success: All location settings are met.");
                        mFusedLocationProviderClient.requestLocationUpdates(
                                mLocationRequest,
                                mLocationCallback,
                                Looper.myLooper()
                        );
                        if (mLocation == null) {
                            Log.i(TAG, "mLocation WAS NULL");
                        } else {
                            //TODO/NOTE - this is never called
                            Log.i("Location", mLocation.toString());

                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    int requestCheckSettings = 100;  //?
                                    rae.startResolutionForResult(MapActivity.this, requestCheckSettings);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(MapActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void initMap() {

        // Set the map's camera position to the current location of the device.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLocation.getLatitude(),
                        mLocation.getLongitude()), DEFAULT_ZOOM));

        // Ajout des marqueurs pokémon
        locatedPokemons = computePokemonsOnMap(
                mLocation,
                10, // default : 3
                0.01); // default : 0.006

        addAshOnMap(1.6);
        addPokemonsOnMap(1.6);
    }

    private void updateMap() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLocation.getLatitude(),
                        mLocation.getLongitude()), DEFAULT_ZOOM));

        markerAsh.setPosition(new LatLng(
                mLocation.getLatitude(),
                mLocation.getLongitude()));

        addPokemonsInAshArea();
    }
}
