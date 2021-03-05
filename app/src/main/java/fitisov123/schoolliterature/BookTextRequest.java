package fitisov123.schoolliterature;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import fitisov123.fb2_decoder.FB2Decoder;

public class BookTextRequest extends AsyncTask<String, Integer, File> {

    private final String URL_SERVER = "https://salty-peak-15288.herokuapp.com/classes/";

    private Context context;
    private static boolean requestReady = false;
    public BookTextRequest(Context context){
        this.context = context;
    }

    public static boolean isRequestReady() {
        return requestReady;
    }

    @Override
    protected File doInBackground(String... arg) {
        requestReady = false;

        DataStorage.setGrade(arg[0]);
        DataStorage.setCurTextName(arg[1]);
        DataStorage.setCurTextAuthor(arg[2]);

        File file = new File(context.getFilesDir()
                + DataStorage.getCurTextName()
                + ".xml");

        if (file.length() == 0) {
            performPostCall();
        }

        return file;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        TextFragment.downloadingPB.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        CacheManager.setTextCurPage(context, DataStorage.getCurTextName(), 0);
        CacheManager.setTextStartIndexOnPage(context, DataStorage.getCurTextName(), 0, 0);
        DataStorage.decoder = new FB2Decoder(file);
        DataStorage.decoder.decodeFile();
        DataStorage.setCurText(DataStorage.decoder.getText());
        requestReady = true;
        DataStorage.textFragment.afterTextDownloading();
    }

    public void performPostCall() {
        String query = URL_SERVER + DataStorage.getGrade() + "/" + DataStorage.getCurTextName() + ".xml";
        query = query.replace(" ", "%20");

        String response = "";

        try {
            URL url = new URL(query);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(120000);
            connection.setConnectTimeout(120000);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            double fileSize = connection.getContentLength();

            InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);

            OutputStream outputStream = new FileOutputStream(context.getFilesDir()
                    + DataStorage.getCurTextName()
                    + ".xml");

            int responseCode = connection.getResponseCode();

            double downloadedSize = 0;

            byte data[] = new byte[1024];

            if (responseCode == HttpURLConnection.HTTP_OK) {
                int count = inputStream.read(data);
                while(count != 1) {
                    downloadedSize += count;
                    publishProgress((int) Math.floor((downloadedSize / fileSize) * 100));
                    outputStream.write(data, 0, count);
                    count = inputStream.read(data);
                }
            } else {
                response = connection.getResponseMessage();
            }

            outputStream.flush();

            outputStream.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            response = "error";
        }
    }
}
