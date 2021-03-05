package fitisov123.schoolliterature;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

public class ProfileSettingsFragment extends Fragment {

    private static String name, surname, grade, userId;
    private static String[] classes = {"5", "6", "7", "8", "9", "10", "11"};
    private static Context context;
    private static Button submit_b;
    private static EditText name_et;
    private static EditText surname_et;
    private static Spinner grade_sp;
    private static ProgressBar pbsaving;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Algorithm.logMessage("profile fragment started creating");

        final View layout = inflater.inflate(R.layout.fragment_profile_settings, container, false);
        context = getActivity().getApplicationContext();

        submit_b = (Button) layout.findViewById(R.id.editProfileSubmit);
        name_et = (EditText) layout.findViewById(R.id.editName);
        surname_et = (EditText) layout.findViewById(R.id.editSurname);
        grade_sp = (Spinner) layout.findViewById(R.id.editGradeSpinner);
        pbsaving = (ProgressBar) layout.findViewById(R.id.progressBarSaveProfileSettings);

        name = DataStorage.getName();
        surname = DataStorage.getSurname();
        grade = DataStorage.getGrade();
        userId = DataStorage.getUserId();

        name_et.setText(name);
        surname_et.setText(surname);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.item_spinner, classes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grade_sp.setAdapter(adapter);
        grade_sp.setSelection(Integer.parseInt(grade) - 5);

        name_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    name_et.setCursorVisible(true);
            }
        });

        surname_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    surname_et.setCursorVisible(true);
            }
        });

        submit_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit_b.setClickable(false);
                submit_b.setVisibility(View.GONE);
                pbsaving.setVisibility(View.VISIBLE);
                name = name_et.getText().toString();
                surname = surname_et.getText().toString();
                grade = grade_sp.getSelectedItem().toString();
                if(FieldChecker.correctField(name, true) && FieldChecker.correctField(surname, true)) {
                    new UserDataRequest(context).execute("update_user", userId, name, surname, grade);
                    DataStorage.setName(name);
                    DataStorage.setSurname(surname);
                    DataStorage.setGrade(grade);
                    MainActivity.updateHeader();
                } else {
                    stopLoading();
                    Toast.makeText(context, FieldChecker.userDataWrongToastString, Toast.LENGTH_LONG).show();
                }
            }
        });

        Algorithm.logMessage("Profile created");

        return layout;
    }

    public static void stopLoading(){
        submit_b.setClickable(true);
        submit_b.setVisibility(View.VISIBLE);
        pbsaving.setVisibility(View.GONE);
    }

    @Override
    public void onDetach() {
        Algorithm.logMessage("profile fragment on detach");
        super.onDetach();
    }

    @Override
    public void onPause() {
        Algorithm.logMessage("profile fragment on pause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Algorithm.logMessage("profile fragment on stop");
        super.onStop();
    }

    @Override
    public void onStart() {
        Algorithm.logMessage("profile fragment on start");
        super.onStart();
    }

    @Override
    public void onResume() {
        Algorithm.logMessage("profile fragment on resume");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Algorithm.logMessage("profile fragment is destroyed");
        super.onDestroy();
    }
}
