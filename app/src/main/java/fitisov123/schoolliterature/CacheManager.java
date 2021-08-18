package fitisov123.schoolliterature;

import android.content.Context;
import android.content.SharedPreferences;

public class CacheManager {
    private static final String positionCache = "SchoolLiteraturePositionCache";
    private static final String settingsCache = "SchoolLiteratureSettingsCache";
    private static final String userDataCache = "SchoolLiteratureUserDataCache";
    private static final String bookListPositionCache = "SchoolLiteratureBookListPositionCache";

    private static final String defaultNoteString = "g5=#g6=#g7=#g8=#g9=#g10=#g11=#";

    public static int getTextLastPage(Context context, String textName){
        SharedPreferences sp = context.getSharedPreferences(positionCache, Context.MODE_PRIVATE);
        int lastPage = sp.getInt(textName + "lastPage", -1);
        return lastPage;
    }

    public static void setTextLastPage(Context context, String textName, int lastPageNumber){
        SharedPreferences sp = context.getSharedPreferences(positionCache, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(textName + "lastPage", lastPageNumber);
        ed.commit();
    }

    public static int getTextCurPage(Context context, String textName){
        SharedPreferences sp = context.getSharedPreferences(positionCache, Context.MODE_PRIVATE);
        int curPage = sp.getInt(textName + "curPage", 0);
        return curPage;
    }

    public static void setTextCurPage(Context context, String textName, int pageNum){
        SharedPreferences sp = context.getSharedPreferences(positionCache, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(textName + "curPage", pageNum);
        ed.commit();
    }

    public static int getTextStartIndexOnPage(Context context, String textName, int pageNum){
        SharedPreferences sp = context.getSharedPreferences(positionCache, Context.MODE_PRIVATE);
        String query = textName + "startIndex" + String.valueOf(pageNum);
        int startIndex = sp.getInt(query, 0);
        return startIndex;
    }

    public static void setTextStartIndexOnPage(Context context, String textName, int pageNum, int indexStart){
        SharedPreferences sp = context.getSharedPreferences(positionCache, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(textName + "startIndex" + String.valueOf(pageNum), indexStart);
        ed.commit();
    }

    public static boolean doesPageExist(Context context, String textName, int pageNum) {
        SharedPreferences sp = context.getSharedPreferences(positionCache, Context.MODE_PRIVATE);
        return sp.contains(textName + "startIndex" + String.valueOf(pageNum));
    }

    public static TextStyle getSettingsStyleMode(Context context){
        SharedPreferences sp = context.getSharedPreferences(settingsCache, Context.MODE_PRIVATE);
        boolean var = sp.getBoolean("styleMode", false);
        TextStyle style = (var ? TextStyle.darkMode : TextStyle.lightMode); // false == light, true == dark
        return style;
    }

    public static void setSettingsStyleMode(Context context, TextStyle style){
        SharedPreferences sp = context.getSharedPreferences(settingsCache, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        boolean var;
        if(style.equals(TextStyle.lightMode))
            var = false;
        else
            var = true;
        ed.putBoolean("styleMode", var);
        ed.commit();
    }

    public static TextAlignment getSettingsAlignmentMode(Context context){
        SharedPreferences sp = context.getSharedPreferences(settingsCache, Context.MODE_PRIVATE);
        boolean var = sp.getBoolean("alignmentMode", false);
        TextAlignment alignment = (var ? TextAlignment.centerAlignment : TextAlignment.leftAlignment); // false == left, true == center
        return alignment;
    }

    public static void setSettingsAlignmentMode(Context context, TextAlignment alignment){
        SharedPreferences sp = context.getSharedPreferences(settingsCache, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        boolean var;
        if(alignment.equals(TextAlignment.leftAlignment))
            var = false;
        else
            var = true;
        ed.putBoolean("alignmentMode", var);
        ed.commit();
    }

    public static String getUserDataLogin(Context context){
        SharedPreferences sp = context.getSharedPreferences(userDataCache, Context.MODE_PRIVATE);
        if(!sp.contains("login"))
            return null;
        return sp.getString("login", "");
    }

    public static void setUserDataLogin(Context context, String login){
        SharedPreferences sp = context.getSharedPreferences(userDataCache, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.remove("login");
        ed.putString("login", login);
        ed.commit();
    }

    public static String getUserDataPassword(Context context){
        SharedPreferences sp = context.getSharedPreferences(userDataCache, Context.MODE_PRIVATE);
        if(!sp.contains("password"))
            return null;
        return sp.getString("password", "");
    }

    public static void setUserDataPassword(Context context, String password){
        SharedPreferences sp = context.getSharedPreferences(userDataCache, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.remove("password");
        ed.putString("password", password);
        ed.commit();
    }

    public static void clearUserData(Context context){
        SharedPreferences sp = context.getSharedPreferences(userDataCache, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.remove("password");
        ed.remove("login");
        ed.commit();
    }

    public static Integer getBookListPosition(Context context, String grade){
        SharedPreferences sp = context.getSharedPreferences(bookListPositionCache, Context.MODE_PRIVATE);
        return sp.getInt(grade, 0);
    }

    public static void setBookListPosition(Context context, String grade, Integer position){
        SharedPreferences sp = context.getSharedPreferences(bookListPositionCache, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt(grade, position);
        ed.commit();
    }

    public static void setNotes(Context context, String notes) {
        SharedPreferences sp = context.getSharedPreferences(userDataCache, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("notes", notes);
        ed.commit();
    }

    public static String getNotes(Context context){
        SharedPreferences sp = context.getSharedPreferences(userDataCache, Context.MODE_PRIVATE);
        if(!sp.contains("notes"))
            return defaultNoteString;
        return sp.getString("notes", defaultNoteString);
    }
}
