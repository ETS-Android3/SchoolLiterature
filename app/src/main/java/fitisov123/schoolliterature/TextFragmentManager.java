package fitisov123.schoolliterature;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class TextFragmentManager extends Fragment {

    private static View layout;
    private static Context context;
    private static BottomNavigationView bottomNavigationView;
    private static FragmentManager fragmentManager;
    private static FragmentTransaction fragmentTransaction;
    private static FragmentActivity fragmentActivity;
    public static ImageButton openText, openNotes;

    @Override
    public void onAttach(Activity activity) {
        fragmentActivity = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    public static void useTextFragment(){
        openNotes.setImageResource(R.drawable.ic_bn_notes);
        openText.setImageResource(R.drawable.ic_bn_text_checked);
        openNotes.setClickable(false);
        openText.setClickable(false);
        inflateFragment(DataStorage.textFragment = new TextFragment());
    }

    public static void useNotesFragment(){
        openNotes.setImageResource(R.drawable.ic_bn_notes_checked);
        openText.setImageResource(R.drawable.ic_bn_text);
        openNotes.setClickable(false);
        openText.setClickable(true);
        inflateFragment(DataStorage.notesFragment = new TextNotesFragment());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        layout = inflater.inflate(R.layout.fragment_text_manager, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        MainActivity.disallowOpeningMenu();

        openText = (ImageButton) layout.findViewById(R.id.open_text_bn);
        openNotes = (ImageButton) layout.findViewById(R.id.open_notes_bn);

        fragmentManager = fragmentActivity.getSupportFragmentManager();

        useTextFragment();

        openText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useTextFragment();
            }
        });

        openNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useNotesFragment();
            }
        });

        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        DataStorage.setCurText(null);
        DataStorage.setCurTextName(null);
        DataStorage.setCurTextAuthor(null);
        MainActivity.allowOpeningMenu();
        super.onDestroyView();
    }

    public static void inflateFragment(Fragment fragment){
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.managerFragment, fragment);
        fragmentTransaction.commit();
    }
}
