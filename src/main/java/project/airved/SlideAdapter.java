package project.airved;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SlideAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public int[] textHead = {R.string.aqigood, R.string.aqisatisfactory, R.string.aqimoderate, R.string.aqipoor, R.string.aqiunhealthy, R.string.aqihazardous};
    public int[] textBody = {R.string.goodinfo,R.string.satisfactoryinfo,R.string.moderateinfo,R.string.poorinfo,R.string.unhealthyinfo,R.string.hazardousinfo};

    public SlideAdapter(Context context){
        this.context=context;
    }
    @Override
    public int getCount() {
        return textHead.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view==(LinearLayout)o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider,container,false);
        TextView head = (TextView) view.findViewById(R.id.slidehead);
        TextView body = (TextView) view.findViewById(R.id.slidebody);
        head.setText(textHead[position]);
        body.setText(textBody[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
