package fitisov123.schoolliterature;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends Fragment {

    private static Context context;
    private static View layout;
    private TabSettingsAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static ProfileSettingsFragment profileSettingsFragment = null;
    private static TextSettingsFragment textSettingsFragment = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        profileSettingsFragment = new ProfileSettingsFragment();
        textSettingsFragment = new TextSettingsFragment();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        layout = inflater.inflate(R.layout.fragment_settings, container, false);

        viewPager = (ViewPager) layout.findViewById(R.id.viewPager);
        tabLayout = (TabLayout) layout.findViewById(R.id.tabLayout);
        adapter = new TabSettingsAdapter(getActivity().getSupportFragmentManager());
        Algorithm.logMessage("onCreate1");
        adapter.addFragment(profileSettingsFragment, "profile");
        adapter.addFragment(textSettingsFragment, "text");
        Algorithm.logMessage("onCreate2");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_person_bn);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_text_bn);

        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        Algorithm.logMessage("settings fragment on detach");
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        Algorithm.logMessage("settings fragment on destroy");
        super.onDestroy();
    }

    @Override
    public void onPause() {
        Algorithm.logMessage("settings fragment on pause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Algorithm.logMessage("settings fragment on stop");

        super.onStop();
    }

    @Override
    public void onStart() {
        Algorithm.logMessage("settings fragment on start");
        super.onStart();
    }

    @Override
    public void onResume() {
        Algorithm.logMessage("settings fragment on resume");
        super.onResume();
    }
}
