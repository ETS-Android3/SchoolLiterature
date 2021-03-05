package fitisov123.schoolliterature;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationActivity extends AppCompatActivity {

    private static Spinner grade;
    private static EditText new_username_et;
    private static EditText new_password_et;
    private static EditText name_et;
    private static EditText surname_et;
    private static Button reg_btn;
    private Context context = this;
    private RelativeLayout layout;
    private static TextView grade_tv;
    private static ProgressBar progressBar;
    private static boolean connected;

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        String[] classes = {"5", "6", "7", "8", "9", "10", "11"};

        grade = (Spinner) findViewById(R.id.grade);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_spinner, classes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grade.setAdapter(adapter);
        grade.setSelection(0);
        new_username_et = (EditText) findViewById(R.id.new_username);
        new_password_et = (EditText) findViewById(R.id.new_password);
        name_et = (EditText) findViewById(R.id.name);
        surname_et = (EditText) findViewById(R.id.surname);
        reg_btn = (Button) findViewById(R.id.reg);
        grade_tv = (TextView) findViewById(R.id.grade_tv);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        layout = (RelativeLayout) findViewById(R.id.registration_form);

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.hasFocus();

                if(!isEmpty(new_username_et) && !isEmpty(new_password_et) && !isEmpty(name_et) && !isEmpty(surname_et))
                {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    startLoading();
                    connected = isConnected();
                    if(!connected)
                    {
                        stopLoading();
                        Toast.makeText(context, "Необходимо подключение к интернету", Toast.LENGTH_LONG).show();
                        return;
                    }
                    String newLogin = new_username_et.getText().toString();
                    String newPassword = new_password_et.getText().toString();
                    String newName = name_et.getText().toString();
                    String newSurname = surname_et.getText().toString();
                    String newGrade = grade.getSelectedItem().toString();
                    if(!FieldChecker.correctField(newLogin, true)
                            || !FieldChecker.correctField(newPassword, true)
                            || !FieldChecker.correctField(newName, true)
                            || !FieldChecker.correctField(newSurname, true)){
                        stopLoading();
                        Toast.makeText(context, FieldChecker.userDataWrongToastString, Toast.LENGTH_LONG).show();
                        return;
                    }
                    CacheManager.setUserDataLogin(context, newLogin);
                    CacheManager.setUserDataPassword(context, newPassword);
                    new UserDataRequest(context).execute("add_user", newLogin, newPassword, newName, newSurname, newGrade);
                }
                else
                {
                    Toast.makeText(context, "Необходимо заполнить все поля формы!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public static void startLoading(){
        new_username_et.setVisibility(View.GONE);
        new_password_et.setVisibility(View.GONE);
        name_et.setVisibility(View.GONE);
        surname_et.setVisibility(View.GONE);
        grade.setVisibility(View.GONE);
        grade_tv.setVisibility(View.GONE);
        reg_btn.setVisibility(View.GONE);

        new_username_et.setClickable(false);
        new_password_et.setClickable(false);
        name_et.setClickable(false);
        surname_et.setClickable(false);
        grade.setClickable(false);
        reg_btn.setClickable(false);

        progressBar.setVisibility(View.VISIBLE);
    }

    public static void stopLoading(){
        new_username_et.setVisibility(View.VISIBLE);
        new_password_et.setVisibility(View.VISIBLE);
        name_et.setVisibility(View.VISIBLE);
        surname_et.setVisibility(View.VISIBLE);
        grade.setVisibility(View.VISIBLE);
        grade_tv.setVisibility(View.VISIBLE);
        reg_btn.setVisibility(View.VISIBLE);

        new_username_et.setClickable(true);
        new_password_et.setClickable(true);
        name_et.setClickable(true);
        surname_et.setClickable(true);
        grade.setClickable(true);
        reg_btn.setClickable(true);

        progressBar.setVisibility(View.GONE);
    }
}
