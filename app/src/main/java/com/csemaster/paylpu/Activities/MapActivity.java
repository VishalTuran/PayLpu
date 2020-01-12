package com.csemaster.paylpu.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.csemaster.paylpu.AddressActivity;
import com.csemaster.paylpu.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.api.Context;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, MaterialSearchBar.OnSearchActionListener {
    MaterialSearchBar materialSearchBar;
    GoogleMap googleMap;
    Geocoder geocoder;
    FloatingActionButton currentLocationButton;
    ExtendedFloatingActionButton addAddressButton;
    List<Address> addresses = null;

    FusedLocationProviderClient locationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps_fragment);
        locationProviderClient= LocationServices.getFusedLocationProviderClient(this);

        currentLocationButton=findViewById(R.id.currentLocation);

        if(isGooglePlayAvailable())
        {
            hideSoftKeyBoard();
            assert supportMapFragment != null;
            supportMapFragment.getMapAsync(this);

            materialSearchBar = findViewById(R.id.searchBar);
            materialSearchBar.setOnSearchActionListener(this);
            materialSearchBar.setCardViewElevation(10);
            materialSearchBar.setSpeechMode(false);

            materialSearchBar.enableSearch();
            materialSearchBar.hideSuggestionsList();
            materialSearchBar.setHint("Enter Address");

            addAddressButton=findViewById(R.id.fab);

            geocoder= new Geocoder(MapActivity.this,Locale.getDefault());

            addAddressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(MapActivity.this, AddressActivity.class);
                    if(addresses!=null)
                    {
                        intent.putExtra("city",addresses.get(0).getLocality());
                        intent.putExtra("state",addresses.get(0).getAdminArea());
                        intent.putExtra("pincode",addresses.get(0).getPostalCode());


                    }

                    String fullAddress=materialSearchBar.getText();

                    if(!TextUtils.isEmpty(fullAddress))
                    {
                        intent.putExtra("address",fullAddress);
                    }

                    startActivity(intent);

                }
            });
        }

        currentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isGPSEnabled())
                {
                    goToCurrentLocation();

                }
            }
        });
    }

    private void goToCurrentLocation() {
        locationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful())
                {
                    Location location=task.getResult();

                    CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),15);
                    googleMap.animateCamera(cameraUpdate);
                    googleMap.clear();

                    addMarker(new LatLng(location.getLatitude(),location.getLongitude()));

                    mapDrag();

                    setTextOnSearchBar(new LatLng(location.getLatitude(),location.getLongitude()));


                }
            }
        });
    }

    private boolean isGPSEnabled()
    {
        LocationManager locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean providerEnabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(providerEnabled)
        {
            return true;
        }
        else
        {
            AlertDialog alertDialog=new AlertDialog.Builder(this)
                    .setTitle("GPS Permissions")
                    .setMessage("GPS is required for this app to work.Please Enable GPS.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent,156);
                        }
                    }).setCancelable(false).show();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==156 && resultCode==RESULT_OK && data!=null)
        {
            if(isGPSEnabled())
            {
                Toast.makeText(this, "GPS is Enabled", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Please Enable GPS", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isGooglePlayAvailable() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApiAvailability.isUserResolvableError(result)) {
            Dialog dialog = googleApiAvailability.getErrorDialog(this, result, 9002, task -> {
                Toast.makeText(this, "Dialog is cancelled by User!", Toast.LENGTH_SHORT).show();
            });
            dialog.show();
        } else {
            Toast.makeText(this, "Google Play Service are required by App", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        if(googleMap!=null)
        {
            this.googleMap=googleMap;
            boolean success=googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.mapstyle));

            LatLng lpuLatLng=new LatLng(31.2536,75.7037);

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lpuLatLng,15));
            if(!success)
            {

            }
        }

        assert googleMap != null;
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
                public void onMapClick(LatLng latLng) {

                try {
                        addresses=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                assert addresses != null;
                materialSearchBar.setText(addresses.get(0).getAddressLine(0));

                googleMap.clear();

                addMarker(latLng);

                mapDrag();

                }
        });
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        Toast.makeText(MapActivity.this, "onSearchConfirmed Worked:"+text, Toast.LENGTH_SHORT).show();
        Geocoder geocoder=new Geocoder(this, Locale.getDefault());

        List<Address> addressList= null;
        try {
            addressList = geocoder.getFromLocationName(text.toString(),1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert addressList != null;
        if(addressList.size()>0)
        {
            Address address=addressList.get(0);
            LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());
            CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(latLng,15);
            googleMap.animateCamera(cameraUpdate);

            googleMap.clear();

            addMarker(latLng);

            mapDrag();

        }
    }



    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode) {
            case MaterialSearchBar.BUTTON_NAVIGATION:
                break;
            case MaterialSearchBar.BUTTON_BACK:
                materialSearchBar.disableSearch();
                break;
            case MaterialSearchBar.BUTTON_SPEECH:
                break;
        }
    }

    private void hideSoftKeyBoard()
    {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            }
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void addMarker(LatLng latlng)
    {
        MarkerOptions markerOptions=new MarkerOptions()
                .position(latlng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.l_marker))
                .draggable(true);

        googleMap.addMarker(markerOptions);
    }

    public void mapDrag()
    {
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {
                CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude,marker.getPosition().longitude),15);
                googleMap.animateCamera(cameraUpdate);
                googleMap.getUiSettings().setZoomControlsEnabled(false);


                try {
                    materialSearchBar.setText(geocoder.getFromLocation(marker.getPosition().latitude,marker.getPosition().longitude,1).get(0).getAddressLine(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }
        });
    }

    public void setTextOnSearchBar(LatLng latLng)
    {
        try {
            materialSearchBar.setText(geocoder.getFromLocation(latLng.latitude,latLng.longitude,1).get(0).getAddressLine(0));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
