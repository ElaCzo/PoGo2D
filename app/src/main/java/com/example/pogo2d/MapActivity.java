package com.example.pogo2d;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.compat.GeoDataClient;
import com.google.android.libraries.places.compat.PlaceDetectionClient;
import com.google.android.libraries.places.compat.Places;

import java.util.ArrayList;
import java.util.stream.Collectors;

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
    private Location mLastKnownLocation; /* Location android.Location.location à vérif sinon
    suppr l'import et essayer un autre */

    private ArrayList<LocatedPokemon> locatedPokemons = new ArrayList<>();

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
        //mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap = googleMap;

        // Do other setup activities here too, as described elsewhere in this tutorial.

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
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
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                            // Ajout des marqueurs pokémon
                            locatedPokemons = computePokemonsOnMap(mLastKnownLocation,
                                    3,
                                    0.006);

                            Log.e("locate ", locatedPokemons.size() + "");

                            addSashaOnMap(1.6);
                            addPokemonsOnMap(1.6);

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


        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        for (int i = 0; i < nombre; i++) {
            int alea = (int) (Math.random() * nombrePokeExistants);
            LatLng ll = computeRandomInRangeWhichDoesntAlreadyExist(range, points, location);

            /*final double newLatitude = computeRandomInRange(latitude, range);
            final double newLongitude = computeRandomInRange(longitude, range);

            LatLng ll = new LatLng(newLatitude, newLongitude);*/

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
                        Log.i("DIST POKE", dist+"");
                        return Math.abs(dist);
                    }).anyMatch(e-> (e < 0.1));

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

    private void addSashaOnMap(double scale) {
        String cheminSasha = Sasha.getFichier().getAbsolutePath();
        Bitmap imageSasha = BitmapFactory.decodeFile(cheminSasha);

        imageSasha = Bitmap
                .createScaledBitmap(imageSasha,
                        (int) (imageSasha.getWidth() * scale),
                        (int) (imageSasha.getHeight() * scale),
                        false);


        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(
                        mLastKnownLocation.getLatitude(),
                        mLastKnownLocation.getLongitude()))
                .icon(BitmapDescriptorFactory.fromBitmap(imageSasha)));
    }
}
