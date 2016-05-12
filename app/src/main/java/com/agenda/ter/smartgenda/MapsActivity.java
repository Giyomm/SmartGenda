package com.agenda.ter.smartgenda;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.agenda.ter.map.DirectionFinder;
import com.agenda.ter.map.DirectionFinderListener;
import com.agenda.ter.map.Route;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    //LAT ET LNG
    double latitude = 0, longitude = 0;

    Intent intentFromCalendar;

    // LES WIDGETS DE L'ACTIVITE MAPS
    Button chercherBtn, saveLocationButon;
    EditText destinationEditText, departEditText;

    private LocationManager locationManager;
    private String provider;


    //JE SAIS PAS ENCORE
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

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
        //departEditText = (EditText) findViewById(R.id.maps_origin_edittext_id);
        saveLocationButon = (Button) findViewById(R.id.maps_saveLocation_bouton_id);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){

            @Override
            public boolean onMyLocationButtonClick() {
                //double longitudeLocation = mMap.getMyLocation().getLongitude();
                //double latitudeLocation = mMap.getMyLocation().getLatitude();
                //Log.d("LOCATION", "location : "+  mMap.getMyLocation().getLatitude()+ " , "+mMap.getMyLocation().getLongitude());
                Log.d("Location par def", "" + mMap.getMyLocation());
                try {
                    //departEditText.setText(mMap.getMyLocation().getLatitude() + " , " + mMap.getMyLocation().getLongitude());
                    LatLng latlng = new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,10.0f));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Attente de géolocalisation. Pensez à activer votre GPS...", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    /*public void chercherItineraire(View v){
        sendRequest(); saveLocationButon.setEnabled(true);
    }

    private void sendRequest(){
        String destination = destinationEditText.getText().toString();
        String depart = departEditText.getText().toString();
        if (depart.isEmpty()) {
            Toast.makeText(this, "Entrez une adresse de départ!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Entrez une destination!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(new DirectionFinderListener() {
                @Override
                public void onDirectionFinderStart() {
                    MapsActivity.this.onDirectionFinderStart();
                }

                @Override
                public void onDirectionFinderSuccess(List<Route> route) {
                    MapsActivity.this.onDirectionFinderSuccess(route);
                }
            }, depart, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Patience",
                "Trouver la direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 5));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                   // .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                  //  .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));

            LatLngBounds.Builder b = new LatLngBounds.Builder();
            for (Marker m : originMarkers)
                b.include(m.getPosition());
            for (Marker m : destinationMarkers)
                b.include(m.getPosition());
            LatLngBounds bounds = b.build();

            int widthForBounds = getResources().getDisplayMetrics().widthPixels;
            int heightForBounds = getResources().getDisplayMetrics().heightPixels -
                    findViewById(R.id.maps_destination_layout_id).getHeight() -
                    findViewById(R.id.maps_origin_layout_id).getHeight() -
                    findViewById(R.id.maps_control_panel_layout_id).getHeight() -
                    findViewById(R.id.maps_saveLocation_bouton_id).getHeight();

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,widthForBounds,heightForBounds,25);
            mMap.animateCamera(cu);
        }
    }*/

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

    //ENVOYER UNE LOCALISATION A EVENTSACTIVITY
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
