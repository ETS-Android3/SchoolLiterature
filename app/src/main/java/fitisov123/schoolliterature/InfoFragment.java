package fitisov123.schoolliterature;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class InfoFragment extends Fragment {

    private static Context context;
    private SharedPreferences sp;
    private static TextView info_tv;
    private static FrameLayout frameLayout;
    private static View layout;
    private static boolean inHere = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.fragment_info, container, false);
        context = getActivity().getApplicationContext();
        inHere = true;

        info_tv = layout.findViewById(R.id.info_tv);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "FontForShl.ttf");
        info_tv.setTypeface(typeface);
        frameLayout = (FrameLayout) layout.findViewById(R.id.info_layout);

        return layout;
    }

    /*public static void switchToNightMode(boolean toNight){
        if(inHere && info_tv != null)
        {
            if(toNight){
                info_tv.setTextColor(ContextCompat.getColor(context, R.color.DayModeBackground));
                frameLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.NightModeBackground));
                MainActivity.toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.NightModeAppBar));
            }
            else{
                info_tv.setTextColor(ContextCompat.getColor(context, R.color.NightModeBackground));
                frameLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.DayModeBackground));
                MainActivity.toolbar.setBackgroundColor(ContextCompat.getColor(context, R.color.mainBlue));
            }
        }
    }*/

    public static void setText_tvSize(int new_size) {
        if(inHere && info_tv != null)
            info_tv.setTextSize(new_size);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        inHere = false;
    }

    @Override
    public void onPause() {
        inHere = false;
        super.onPause();
    }
}
