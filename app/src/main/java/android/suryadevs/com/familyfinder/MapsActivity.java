package android.suryadevs.com.familyfinder;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    String lonvalue,latvalue,placeName;
    float value1, value2;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        String clickedItem = extras.getString("clicked");
        if(clickedItem.equals("button"))
            setContentView(R.layout.enter_location);
        else if (clickedItem.equals("row"))
            setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Bundle extras = getIntent().getExtras();
        final int position = extras.getInt("position");
        final Firebase myFirebaseRef = new Firebase("https://familylist-suryadevs.firebaseio.com/");
        //This lines keeps the local copy synced with the remote copy in firebase
        myFirebaseRef.keepSynced(true);

        String clickedItem = extras.getString("clicked");
        if(clickedItem.equals("button")){
            Button postCoOrdinates = (Button)findViewById(R.id.button);
            postCoOrdinates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View focusView = null;
                    boolean cancel = false;

                    EditText latitude = (EditText) findViewById(R.id.editText);
                    EditText longitude = (EditText) findViewById(R.id.editText2);
                    TextView area = (EditText) findViewById(R.id.editText3);
                    latvalue = latitude.getText().toString();
                    lonvalue = longitude.getText().toString();
                    
                    if (!TextUtils.isDigitsOnly(latvalue)){
                        latitude.setError("Please enter digits only");
                        focusView = latitude;
                        cancel = true;
                    }
                    else if(TextUtils.isEmpty(latvalue)){
                        latitude.setError("Necessary field. Please enter numeric value");
                        focusView = latitude;
                        cancel = true;
                    }

                    if (!TextUtils.isDigitsOnly(lonvalue)){
                        longitude.setError("Please enter digits only");
                        focusView = longitude;
                        cancel = true;
                    }
                    else if(TextUtils.isEmpty(lonvalue)){
                        longitude.setError("Necessary field. Please enter numeric value");
                        focusView = longitude;
                        cancel = true;
                    }

                    if (cancel) {
                        // There was an error; don't attempt login and focus the first
                        // form field with an error.
                        focusView.requestFocus();
                    }
                    else {
                        value1 = Float.parseFloat(latvalue);
                        value2 = Float.parseFloat(lonvalue);
                        placeName = area.getText().toString();

                        myFirebaseRef.child("familyList").child(String.valueOf(position)).child("lat").setValue(latvalue);
                        myFirebaseRef.child("familyList").child(String.valueOf(position)).child("lon").setValue(lonvalue);
                        myFirebaseRef.child("familyList").child(String.valueOf(position)).child("place").setValue(placeName);

                        LatLng sydney = new LatLng(value1, value2);
                        mMap.addMarker(new MarkerOptions().position(sydney).title(placeName));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        Toast.makeText(MapsActivity.this, "Location saved", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else if (clickedItem.equals("row")){
            myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String lat = String.valueOf(dataSnapshot.child("familyList").child(String.valueOf(position)).child("lat").getValue());
                    String lon = String.valueOf(dataSnapshot.child("familyList").child(String.valueOf(position)).child("lon").getValue());
                    Log.d("Co Ords:", lat + " " + lon);
                    try {
                        value1 = Float.parseFloat(lat);
                        value2 = Float.parseFloat(lon);
                        LatLng sydney = new LatLng(value1, value2);
                        mMap.addMarker(new MarkerOptions().position(sydney).title(placeName));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        String place = String.valueOf(dataSnapshot.child("familyList").child(String.valueOf(position)).child("place").getValue());
                        Toast.makeText(MapsActivity.this, place, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(MapsActivity.this, "This family member hasn't yet saved location", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }

    }

}

