package fitisov123.schoolliterature;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserDataRequest extends AsyncTask<String, Integer, String> {

    private final String URL_SERVER = "https://salty-peak-15288.herokuapp.com/index.php";
    private Context context;
    private String queryType;
    public UserDataRequest(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... arg) {
        String queryStr = "";
        queryType = arg[0];
        switch (queryType)
        {
            case "login":
                queryStr = "username=" + arg[1] + "&password=" + arg[2];
                break;
            case "login_with_id":
                queryStr = "user_id=" + arg[1];
                break;
            case "add_user":
                queryStr = "new_username=" + arg[1] + "&new_password=" + arg[2] +  "&name=" + arg[3] + "&surname=" + arg[4] + "&grade=" + arg[5];
                break;
            case "update_user":
                queryStr = "user_id=" + arg[1] + "&edit_name=" + arg[2] + "&edit_surname=" + arg[3] + "&edit_grade=" + arg[4];
                break;
            case "get_texts":
                queryStr = "grade=" + arg[1];
                break;
            case "update_notes":
                queryStr = "user_id=" + arg[1] + "&new_note_str=" + arg[2];
                break;
        }

        return performPostCall(queryStr);
    }

    @Override
    protected void onPostExecute(String data) {
        super.onPostExecute(data);

        final String wrongData = "Wrong username or password!";
        final String alreadyExists = "User with this username already exists!";
        final String error = "error";
        switch (data){
            case wrongData:
                Toast.makeText(context, "Неправильный логин или пароль!", Toast.LENGTH_LONG).show();
                LoginActivity.stopLoading();
                break;
            case alreadyExists:
                Toast.makeText(context, "Пользователь с таким логином уже существует!", Toast.LENGTH_LONG).show();
                RegistrationActivity.stopLoading();
                break;
            case error:
                Toast.makeText(context, "Ошибка при выполнении http запроса! Проверьте Интернет подключение.", Toast.LENGTH_LONG).show();
                break;
            default: {
                switch (queryType) {
                    case "login": {
                        JsonDecoder.personDecode(data);
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                    }
                    case "login_with_id": {
                        JsonDecoder.personDecode(data);
                        MainActivity.updateHeader();
                        break;
                    }
                    case "add_user": {
                        JsonDecoder.personDecode(data);
                        new UserDataRequest(context).execute("get_texts", DataStorage.getGrade());
                        break;
                    }
                    case "get_texts": {
                        JsonDecoder.bookDecode(data);
                        BookListFragment.afterBooksLoaded();
                        break;
                    }
                    case "update_notes":{
                        break;
                    }
                    case "update_user":{
                        ProfileSettingsFragment.stopLoading();
                        break;
                    }
                    default: {
                        Toast.makeText(context, "Неизвестная ошибка", Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }
        }
    }

    public String performPostCall(String queryStr) {
        URL url;
        String response = "";
        try{
            url = new URL(URL_SERVER);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(120000);
            connection.setConnectTimeout(120000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(queryStr);

            writer.flush();
            writer.close();

            os.close();

            int responseCode = connection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();
                while(line != null) {
                    response += line;
                    line = reader.readLine();
                }
            }
            else{
                response = connection.getResponseMessage();
            }

        } catch (Exception e) {
            Algorithm.logMessage(e.getMessage());
            response = "error";
        }
        return response;
    }
}
