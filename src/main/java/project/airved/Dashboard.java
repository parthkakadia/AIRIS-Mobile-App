package project.airved;

import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import android.location.Address;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoDatabase;

import org.bson.Document;

public class Dashboard extends AppCompatActivity implements LocationListener {

    private Toolbar mainToolbar;
    private BottomNavigationView navigation;
    private LocationManager locationManager;
    static String fullAddress, city, state;
    Location location;
    static RemoteMongoClient mongoClient = Splash.client.getServiceClient(RemoteMongoClient.factory,"mongodb-atlas");
    static RemoteMongoDatabase db = mongoClient.getDatabase("YOUR_DATABASE");
    static HashMap<String,String> aqiData = new HashMap<>();
    static HashMap<String,String> userProfile = new HashMap<>();

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mainToolbar = (Toolbar) findViewById(R.id.maintoolbar);
        mainToolbar.setTitle("Airis: Home");
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        try {
            locationService();
            fetchAqi(city.toLowerCase());
            fetchProfile(Splash.client.getAuth().getUser().getProfile().getEmail());
        }catch (Exception e){
            Log.e("Dashboard","Error in Dashboard"+e.getMessage());
        }
        setFragment(new HomeFragment());

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainframe, fragment);
        fragmentTransaction.disallowAddToBackStack();
        fragmentTransaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    setFragment(new DashFragment());
                    mainToolbar.setTitle("Airis: My Dashboard");
                    return true;

                case R.id.navigation_profile:
                    setFragment(new ProfileFragment());
                    mainToolbar.setTitle("Airis: My Profile");
                    return true;

                case R.id.navigation_home:
                    setFragment(new HomeFragment());
                    mainToolbar.setTitle("Airis: Home");
                    return true;
            }
            return false;
        }
    };

    private void locationService(){
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if(fullAddress == null) {
                location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 6000,200 , this);
        }
        catch(Exception e) {
            Log.e("location-service","location-service module error!");
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //uLocation = new StringBuilder("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            fullAddress = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            Log.d("location-service",fullAddress);
        }catch(Exception e)
        {
            Log.e("location-service","location-service module error!");
            e.printStackTrace();
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(Dashboard.this, "Turn On Mobile Data and GPS.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    public static void fetchAqi(String city){
        List<Document> foundDocs = new ArrayList<>();
        Document query = new Document("city",city);
        Dashboard.db.getCollection("YOUR_COLLECTION").find(query).into(foundDocs).addOnCompleteListener(new OnCompleteListener<List<Document>>() {
            @Override
            public void onComplete(@NonNull Task<List<Document>> task) {
                if(!(task.getResult().isEmpty())) {
                    aqiData.put("aqi", task.getResult().get(0).get("aqi").toString());
                    aqiData.put("temp", task.getResult().get(0).get("temp").toString());
                    aqiData.put("humid", task.getResult().get(0).get("humid").toString());
                    aqiData.put("co", task.getResult().get(0).get("co").toString());
                    aqiData.put("pm25", task.getResult().get(0).get("pm25").toString());
                    Log.d("stitch-atlas", "Docs Found." + aqiData);
                }
            }
        });
    }

    public static void fetchProfile(String email) {
        try {
            List<Document> foundDocs = new ArrayList<>();
            Document query = new Document("email", email);
            Dashboard.db.getCollection("YOUR_COLLECTION").find(query).into(foundDocs).addOnCompleteListener(new OnCompleteListener<List<Document>>() {
                @Override
                public void onComplete(@NonNull Task<List<Document>> task) {
                    if(!(task.getResult().isEmpty())) {
                        userProfile.put("name", task.getResult().get(0).get("name").toString());
                        userProfile.put("phone", task.getResult().get(0).get("phone").toString());
                        userProfile.put("city",task.getResult().get(0).get("city").toString());
                        userProfile.put("dob",task.getResult().get(0).get("dob").toString());
                        userProfile.put("safeaqi",task.getResult().get(0).get("safeaqi").toString());
                        Log.d("stitch-atlas", "Profile Found." + userProfile);
                    }
                }
            });
        }catch (Exception e){
            Log.e("stitch-atlas","User not found.");
        }
    }
}