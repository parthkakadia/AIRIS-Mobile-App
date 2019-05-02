package project.airved;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoDatabase;
import com.squareup.picasso.Picasso;

import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DashFragment extends Fragment {
    private TextView address, updateTime, cityBox, aqi, aqiText, temp, humid, colevel, pmlevel;
    int aqindex;
    GraphView graph;
    private ImageView aqiAnim;

    public DashFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dash, container, false);

        address = (TextView) view.findViewById(R.id.address);
        updateTime = (TextView) view.findViewById(R.id.updatetime);
        cityBox = (TextView) view.findViewById(R.id.citybox);
        aqi = (TextView) view.findViewById(R.id.aqi);
        aqiText = (TextView) view.findViewById(R.id.aqitext);
        aqiAnim = (ImageView) view.findViewById(R.id.anim);
        //legengBtn = (ImageButton) view.findViewById(R.id.legendbtn);
        //legendImage = (ImageView) view.findViewById(R.id.aqichart);
        temp = (TextView) view.findViewById(R.id.temp);
        humid = (TextView) view.findViewById(R.id.humid);
        colevel = (TextView) view.findViewById(R.id.co_gas);
        pmlevel = (TextView) view.findViewById(R.id.pm2510);
        graph = (GraphView) view.findViewById(R.id.graph);
        SimpleDateFormat sdf = new SimpleDateFormat("E dd MM yyyy', 'hh:mm a");
        updateTime.setText("Last updated on: "+sdf.format(new Date()));
        address.setText("Your current location: "+Dashboard.city+", "+Dashboard.state);
        //legendImage.setVisibility(View.GONE);
        cityBox.setText(Dashboard.city);
        Dashboard.fetchAqi(Dashboard.city.toLowerCase());
        if (!Dashboard.aqiData.isEmpty()){
            aqindex = Integer.parseInt(Dashboard.aqiData.get("aqi"));
            aqi.setText(Dashboard.aqiData.get("aqi"));
            temp.setText("Temp\n"+Dashboard.aqiData.get("temp")+" \u00B0C");
            humid.setText("Humid\n"+Dashboard.aqiData.get("humid")+" %");
            colevel.setText("CO\n"+Dashboard.aqiData.get("co")+" \u00B5g/m\u00B3");
            pmlevel.setText("PM 2.5\n"+Dashboard.aqiData.get("pm25")+" \u00B5g/m\u00B3");

            if(aqindex<=50) {
                Picasso.with(getContext()).load(Uri.parse("IMAGE_RESOURCE_LINK")).into(aqiAnim);aqiText.setText(R.string.aqigood);
                view.setBackgroundColor(getContext().getColor(R.color.aqigood));
            }else if(aqindex>50 && aqindex <=100) {
                Picasso.with(getContext()).load(Uri.parse("IMAGE_RESOURCE_LINK")).into(aqiAnim);aqiText.setText(R.string.aqisatisfactory);
                view.setBackgroundColor(getContext().getColor(R.color.aqisatisfactory));
            }else if(aqindex>100 && aqindex <=200){
                Picasso.with(getContext()).load(Uri.parse("IMAGE_RESOURCE_LINK")).into(aqiAnim);aqiText.setText(R.string.aqimoderate);
                view.setBackgroundColor(getContext().getColor(R.color.aqimoderate));
            }else if(aqindex>200 && aqindex <=300) {
                Picasso.with(getContext()).load(Uri.parse("IMAGE_RESOURCE_LINK")).into(aqiAnim);aqiText.setText(R.string.aqipoor);
                view.setBackgroundColor(getContext().getColor(R.color.aqipoor));
            }else if(aqindex>300 && aqindex <=400) {
                Picasso.with(getContext()).load(Uri.parse("IMAGE_RESOURCE_LINK")).into(aqiAnim);aqiText.setText(R.string.aqiunhealthy);
                view.setBackgroundColor(getContext().getColor(R.color.aqiunhealthy));
            }else if(aqindex>400) {
                Picasso.with(getContext()).load(Uri.parse("IMAGE_RESOURCE_LINK")).into(aqiAnim);
                aqiText.setText(R.string.aqihazardous);
                view.setBackgroundColor(getContext().getColor(R.color.aqihazardous));
            }else
                aqiText.setText("Not Valid");
        }else {
            Toast.makeText(getContext(), "AQI not found for this region!", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(9, 100),
                new DataPoint(11, 125),
                new DataPoint(13, 55),
                new DataPoint(15, 300),
                new DataPoint(17, 425),
                new DataPoint(19, 225)
        });
        graph.addSeries(series);
        // use static labels for horizontal and vertical labels
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"9", "11", "13", "15", "17", "19", "21"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        graph.getViewport().setScrollable(true);

        // styling
        /*series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });*/

        series.setSpacing(20);

        // draw values on top
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.WHITE);
        //series.setValuesOnTopSize(50);

    }
}

        /*legengBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (legendImage.getVisibility() == View.GONE)
                    legendImage.setVisibility(View.VISIBLE);
                else
                    legendImage.setVisibility(View.GONE);
            }
        });*/
