package fitisov123.schoolliterature;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonDecoder {
    public static void personDecode(String jsonData){
        try{
            JSONObject object = new JSONObject(jsonData);
            DataStorage.setUserId(object.getString("user_id"));
            DataStorage.setName(object.getString("name"));
            DataStorage.setSurname(object.getString("surname"));
            DataStorage.setGrade(object.getString("grade"));
            DataStorage.setNotes(object.getString("notes"));
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static void bookDecode(String jsonData){
        try{
            JSONObject object = new JSONObject(jsonData);
            DataStorage.setGrade(object.getString("grade"));
            DataStorage.setTextNames(object.getString("textNames"));
            DataStorage.setTextAuthors(object.getString("textAuthors"));
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }
}
