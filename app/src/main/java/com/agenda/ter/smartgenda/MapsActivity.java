package com.agenda.ter.smartgenda;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

/**
 * Activité permettant la recherche d'un lieu à l'aide de Google Map API
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * latitude et longitude d'un lieu
     */
    double latitude = 0, longitude = 0;;

    /**
     * chercherBtn : bouton pour lancer la recherche
     * saveLocationButon : bouton pour sauvegarder le lieu
     * destinationEditText : champ de texte pour chercher un lieu
     */
    Button chercherBtn, saveLocationButon;
    EditText destinationEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        // LES WIDGETS DE L'ACITIVTE MAPS
        chercherBtn = (Button) findViewById(R.id.maps_chercherLieu_bouton_id);
        destinationEditText = (EditText) findViewById(R.id.maps_destination_edittext_id);
        saveLocationButon = (Button) findViewById(R.id.maps_saveLocation_bouton_id);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){

            @Override
            public boolean onMyLocationButtonClick() {

                try {
                    LatLng latlng = new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,10.0f));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Attente de géolocalisation. Pensez à activer votre GPS...", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    /**
     * Permet la recherche d'un lieu dans la carte
     * @param v
     */
    public void onSearch (View v){
        String location = destinationEditText.getText().toString();
        List<Address> addressList = null;
        if(location == null || !location.equals("")){
            Geocoder geocoder = new Geocoder(this);
            try{
                addressList = geocoder.getFromLocationName(location,1);
            }catch (Exception e){

            }
            Address address = addressList.get(0);
            latitude = address.getLatitude() ; longitude = address.getLongitude();
            LatLng latlng = new LatLng(latitude,longitude);
            mMap.addMarker(new MarkerOptions().position(latlng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,12));
        }
    }


    /**
     * Récupere la latitude et longitude depuis un lieu
     * @param place
     */
    public void getLatLongFromPlace(String place) {
        try {
            Geocoder selected_place_geocoder = new Geocoder(this);
            List<android.location.Address> address;

            address = selected_place_geocoder.getFromLocationName(place, 5);

            if (address == null) {
                //d.dismiss();
            } else {
                android.location.Address location = address.get(0);
                double lat= location.getLatitude();
                double lng = location.getLongitude();
                LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title(""));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                latitude = lat ; longitude = lng;
                Log.d("tag", "getLatLongFromPlace: " + latitude+ " , " +longitude);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Envoyer une localisation a EventActivity
     * @param view
     */
    public void saveLocation(View view) {
        if(destinationEditText.getText().toString()==null || destinationEditText.getText().toString().equals("")){
            Toast.makeText(this, "Entrez une destination !", Toast.LENGTH_SHORT).show();
            return;
        }else {
            getLatLongFromPlace(destinationEditText.getText().toString());
            Intent intent = new Intent(this, EventActivity.class);
            intent.putExtra(EventActivity.EXTRA_LATITUDE, latitude);
            intent.putExtra(EventActivity.EXTRA_LONGITUDE, longitude);
            intent.putExtra(EventActivity.EXTRA_LOCALISATION_NAME, destinationEditText.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
