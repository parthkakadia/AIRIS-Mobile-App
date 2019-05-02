package project.airved;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoDatabase;

import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBUtils{

    /*HashMap<String,String> findDocs(String key, String value){
        List<Document> foundDocs = new ArrayList<>();
        final HashMap<String,String> map = new HashMap<>();
        Document query = new Document(key,value);
        db.getCollection("aqi_region").find(query).into(foundDocs).addOnCompleteListener(new OnCompleteListener<List<Document>>() {
            @Override
            public void onComplete(@NonNull Task<List<Document>> task) {
                map.put("aqi",task.getResult().get(0).get("aqi").toString());
                map.put("temp",task.getResult().get(0).get("temp").toString());
                map.put("humid",task.getResult().get(0).get("humid").toString());
                map.put("co",task.getResult().get(0).get("co").toString());
                map.put("pm25",task.getResult().get(0).get("pm25").toString());
                Log.d("Stitch-Atlas", "Docs Found." + map);
            }
        });
        return map;
    }*/
}
