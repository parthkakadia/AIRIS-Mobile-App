package project.airved;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;

import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    public static StitchAppClient client;
    @Override
    protected void onStart() {
        super.onStart();
        if(client==null)
            client = Stitch.initializeDefaultAppClient("YOUR_STITCH_APP_ID");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(Splash.this, LoginActivity.class));
                    finish();
                }
            }, 2000);
    }
}
