package project.airved;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoDatabase;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateOptions;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private Button logOutBtn, saveBtn;
    private EditText userName, userPhone, userCity, userDob, safeAqi;
    private TextView userMail;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userName = (EditText) view.findViewById(R.id.username);
        userMail = (TextView) view.findViewById(R.id.useremail);
        userMail.setText(Splash.client.getAuth().getUser().getProfile().getEmail());
        userPhone = (EditText) view.findViewById(R.id.userphone);
        userCity = (EditText)view.findViewById(R.id.usercity);
        userDob = (EditText)view.findViewById(R.id.userdob);
        safeAqi = (EditText) view.findViewById(R.id.safeaqi);
        saveBtn = (Button) view.findViewById(R.id.savebtn);
        logOutBtn = (Button) view.findViewById(R.id.logoutbtn);

        if(Dashboard.userProfile.isEmpty()){
            Toast.makeText(getContext(), "Please complete profile!", Toast.LENGTH_LONG).show();
            userName.setFocusable(View.FOCUSABLE);
            saveBtn.setText("Save Profile");
        }else {
            userName.setText(Dashboard.userProfile.get("name"));
            userPhone.setText(Dashboard.userProfile.get("phone"));
            userCity.setText(Dashboard.userProfile.get("city"));
            userDob.setText(Dashboard.userProfile.get("dob"));
            safeAqi.setText(Dashboard.userProfile.get("safeaqi"));
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Document filter = new Document("email",userMail.getText().toString());
                Document doc = new Document("name",userName.getText().toString());
                doc.append("email",userMail.getText().toString());
                doc.append("phone",userPhone.getText().toString());
                doc.append("city",userCity.getText().toString());
                doc.append("dob",userDob.getText().toString());
                doc.append("safeaqi",userDob.getText().toString());
                try {
                    Dashboard.db.getCollection("user_accounts").updateOne(filter, doc, new RemoteUpdateOptions().upsert(true));
                    saveBtn.setText("Edit Profile");
                }catch (Exception e){
                    Log.e("stitch-atlas","Insertion/Updation failed.");
                }

            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Splash.client.getAuth().logout();
                    Log.d("stich-auth","User logged out");
                    Toast.makeText(getContext(), "Airis Stopped!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getContext(),LoginActivity.class));
                    getActivity().finish();
                }catch (Exception e){
                    Log.e("stitch-auth","Log out failed!");
                    Toast.makeText(getContext(), "Airis wants you!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
