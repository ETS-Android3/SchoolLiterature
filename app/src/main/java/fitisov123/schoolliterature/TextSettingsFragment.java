package fitisov123.schoolliterature;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;

import java.util.logging.Logger;

public class TextSettingsFragment extends Fragment {
    private static Context context;
    private static View layout;
    private static Switch styleSwitch, alignmentSwitch;
    private static Button submitBN;
    private static ProgressBar loadingPB;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        Algorithm.logMessage("text settings fragment started creating");

        layout = inflater.inflate(R.layout.fragment_text_settings, container, false);
        context = getActivity().getApplicationContext();

        styleSwitch = (Switch) layout.findViewById(R.id.switch_style);
        alignmentSwitch = (Switch) layout.findViewById(R.id.switch_alignment);

        final Logger logger = Logger.getLogger(TextSettingsFragment.class.getName());

        TextAlignment alignment = CacheManager.getSettingsAlignmentMode(context);
        TextStyle style = CacheManager.getSettingsStyleMode(context);
        if(alignment.equals(TextAlignment.centerAlignment))
            alignmentSwitch.setChecked(true);
        if(style.equals(TextStyle.darkMode))
            styleSwitch.setChecked(true);

        alignmentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean leftAlignment = !alignmentSwitch.isChecked();
                TextAlignment alignment = leftAlignment ? TextAlignment.leftAlignment : TextAlignment.centerAlignment;
                CacheManager.setSettingsAlignmentMode(context, alignment);
            }
        });

        styleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean lightMode = !styleSwitch.isChecked();
                TextStyle style = lightMode ? TextStyle.lightMode : TextStyle.darkMode;
                CacheManager.setSettingsStyleMode(context, style);
            }
        });

        Algorithm.logMessage("Text settings created");

        return layout;
    }
}
