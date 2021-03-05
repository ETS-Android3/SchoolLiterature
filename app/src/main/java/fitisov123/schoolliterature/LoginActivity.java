package fitisov123.schoolliterature;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private static EditText username_et;
    private static EditText password_et;
    private static Button login_btn;
    private static Button reg_btn;
    private Context context = this;
    private RelativeLayout layout;
    private static ProgressBar progressBar;
    private static ImageView imageLogo;
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
        setContentView(R.layout.activity_login);

        username_et = (EditText) findViewById(R.id.username);
        password_et = (EditText) findViewById(R.id.password);
        login_btn = (Button) findViewById(R.id.login);
        reg_btn = (Button) findViewById(R.id.new_reg);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageLogo = (ImageView) findViewById(R.id.image_logo);
        layout = (RelativeLayout) findViewById(R.id.login_form);

        Intent intent = getIntent();
        if(intent.getStringExtra("from") == null || !intent.getStringExtra("from").equals("MainActivity")){
            String localLogin = CacheManager.getUserDataLogin(context);
            String localPassword = CacheManager.getUserDataPassword(context);
            if(localLogin != null && localPassword != null){
                startLoading();
                username_et.setText(localLogin);
                password_et.setText(localPassword);
                new UserDataRequest(context).execute("login", localLogin, localPassword);
                return;
            }
        }

        login_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view){
                layout.hasFocus();

                if(!isEmpty(username_et) && !isEmpty(password_et)) {
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
                    String localLogin = username_et.getText().toString();
                    String localPassword = password_et.getText().toString();
                    if(!FieldChecker.correctField(localLogin, true)
                            || !FieldChecker.correctField(localPassword, true)){
                        stopLoading();
                        Toast.makeText(context, FieldChecker.userDataWrongToastString, Toast.LENGTH_LONG).show();
                        return;
                    }
                    CacheManager.setUserDataLogin(context, localLogin);
                    CacheManager.setUserDataPassword(context, localPassword);
                    new UserDataRequest(context).execute("login", localLogin, localPassword);
                }
                else
                    Toast.makeText(context, "Необходимо заполнить все поля формы!", Toast.LENGTH_LONG).show();
            }
        });
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }

    public static void startLoading(){
        username_et.setVisibility(View.GONE);
        password_et.setVisibility(View.GONE);
        login_btn.setVisibility(View.GONE);
        reg_btn.setVisibility(View.GONE);
        imageLogo.setVisibility(View.GONE);

        username_et.setClickable(false);
        password_et.setClickable(false);
        login_btn.setClickable(false);
        reg_btn.setClickable(false);

        progressBar.setVisibility(View.VISIBLE);
    }

    public static void stopLoading(){
        username_et.setVisibility(View.VISIBLE);
        password_et.setVisibility(View.VISIBLE);
        login_btn.setVisibility(View.VISIBLE);
        reg_btn.setVisibility(View.VISIBLE);
        imageLogo.setVisibility(View.VISIBLE);

        username_et.setClickable(true);
        password_et.setClickable(true);
        login_btn.setClickable(true);
        reg_btn.setClickable(true);

        progressBar.setVisibility(View.GONE);
    }
}
