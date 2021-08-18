package fitisov123.schoolliterature;

import android.content.Context;
import android.util.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import fitisov123.fb2_decoder.FB2Decoder;

public class DataStorage {

    private static DataStorage instance;

    private static String name, surname, grade, userId;
    private static String textNames, textAuthors;
    private static String curTextName, curTextAuthor;
    private static StringBuilder curText;
    private static String curGradeNotesStr, fullNotesStr;
    private static boolean bookListInfoLoaded = false;
    private static HashMap<String, ArrayList<String> > notesHM = new HashMap<>();
    private static File textFile;
    private static float density, windowWidthDP, windowHeightDP;


    public static FB2Decoder decoder;

    public static TextFragment textFragment;
    public static TextNotesFragment notesFragment;

    public static float getWindowWidthDP() {
        return windowWidthDP;
    }

    public static void setWindowWidthDP(float windowWidthDP) {
        DataStorage.windowWidthDP = windowWidthDP;
    }

    public static float getWindowHeightDP() {
        return windowHeightDP;
    }

    public static void setWindowHeightDP(float windowHeightDP) {
        DataStorage.windowHeightDP = windowHeightDP;
    }

    public static boolean isBookListInfoLoaded() {
        return bookListInfoLoaded;
    }

    public static void setBookListInfoLoaded(boolean bookListInfoLoaded) {
        DataStorage.bookListInfoLoaded = bookListInfoLoaded;
    }

    public static File getTextFile() {
        return textFile;
    }

    public static void setTextFile(File textFile) {
        DataStorage.textFile = textFile;
    }

    public static float getDensity() {
        return density;
    }

    public static void setDensity(float density) {
        DataStorage.density = density;
    }

    private DataStorage(){}

    public static synchronized DataStorage getInstance(){
        if(instance == null){
            instance = new DataStorage();
        }
        return instance;
    }

    public static String getName() {
        return name;
    }

    public static String getSurname() {
        return surname;
    }

    public static String getGrade() {
        return grade;
    }

    public static String getUserId() {
        return userId;
    }

    public static String getCurTextAuthor() {
        return curTextAuthor;
    }

    public static String getTextNames() {
        return textNames;
    }

    public static String getTextAuthors() {
        return textAuthors;
    }

    public static StringBuilder getCurText() {
        return curText;
    }

    public static String getCurTextName() {
        return curTextName;
    }

    public static String getFullNotesStr() {
        return fullNotesStr;
    }

    public static String getCurGradeNotesStr() {
        return curGradeNotesStr;
    }

    public static HashMap<String, ArrayList<String>> getNotesHM() {
        return notesHM;
    }

    public static void setName(String name) {
        DataStorage.name = name;
    }

    public static void setSurname(String surname) {
        DataStorage.surname = surname;
    }

    public static void setGrade(String grade) {
        DataStorage.grade = grade;
    }

    public static void setUserId(String userId) {
        DataStorage.userId = userId;
    }

    public static void setTextNames(String textNames) {
        DataStorage.textNames = textNames;
    }

    public static void setTextAuthors(String textAuthors) {
        DataStorage.textAuthors = textAuthors;
    }

    public static void setCurText(StringBuilder curText) {
        DataStorage.curText = curText;
    }

    public static void setFullNotesStr(String fullNotesStr) {
        DataStorage.fullNotesStr = fullNotesStr;
    }

    public static boolean isPageInBoundaries(Context context, int curPageNumber){
        boolean leftOutOfBound = curPageNumber < 0;
        boolean rightOutOfBound = curPageNumber > CacheManager.getTextLastPage(context, DataStorage.getCurTextName());
        if(leftOutOfBound || rightOutOfBound)
            return false;

        return true;
    }

    public static Pair<Integer, String> decodeNote(Context context, String note){
        for(int i = 0; i < note.length(); i++){
            if(note.charAt(i) == '~'){
                String noteText = note.substring(0, i);
                int indexOfStart = Integer.valueOf(note.substring(i+1));
                int leftIndex = -1, rightIndex = CacheManager.getTextLastPage(context, DataStorage.getCurTextName()) + 1;
                while(rightIndex - leftIndex > 1){
                    int midIndex = (leftIndex + rightIndex) / 2;
                    if(CacheManager.getTextStartIndexOnPage(context, DataStorage.getCurTextName(), midIndex) <= indexOfStart)
                        leftIndex = midIndex;
                    else
                        rightIndex = midIndex;
                }
                int pageNumber = leftIndex;
                return new Pair<Integer, String>(pageNumber, noteText);
            }
        }
        return new Pair<Integer, String>(0, "");
    }

    public static void eraseFromGradeNotesStr(int position){
        String noteName = notesHM.get(curTextName).get(position);

        int st = Algorithm.kmp(curGradeNotesStr, noteName);

        if(st == -1)
            throw new IndexOutOfBoundsException();

        removeNoteFromHM(position);

        String res = curGradeNotesStr.substring(0, st);
        if(st + noteName.length() + 1 < curGradeNotesStr.length())
            res += curGradeNotesStr.substring(st + noteName.length() + 1);
        curGradeNotesStr = res;
    }

    public static void addToCurGradeNotesStr(String newNoteName, String newNoteStartIndex){
        String newNote = newNoteName + "~" + newNoteStartIndex + ";";

        DataStorage.addNoteToHM(newNote);

        int stName;
        boolean hasAnyInThisBook = false;
        boolean endReached = false;
        for(int i = 0; i < curGradeNotesStr.length(); i++){
            stName = i;
            while(curGradeNotesStr.charAt(i) != '$') {
                i++;
                if(i == curGradeNotesStr.length()) {
                    i--;
                    endReached = true;
                    break;
                }
            }
            if(endReached)
                break;
            String curName = curGradeNotesStr.substring(stName, i);
            i++;
            if(!curName.equals(DataStorage.getCurTextName())){
                while(curGradeNotesStr.charAt(i) != '|')
                    i++;
            }
            else
            {
                hasAnyInThisBook = true;
                curGradeNotesStr = curGradeNotesStr.substring(0, i)
                        + newNote
                        + curGradeNotesStr.substring(i);
            }
        }

        if(!hasAnyInThisBook){
            curGradeNotesStr = DataStorage.getCurTextName()
                    + "$"
                    + newNote
                    + "|"
                    + curGradeNotesStr;
        }
    }

    public static void updateCurGradePartInFullNotesStr(){
        for(int i = 0; i < fullNotesStr.length(); i++) {
            if(fullNotesStr.charAt(i) == 'g') {
                i++;
                if ((fullNotesStr.substring(i, i + grade.length())).equals(grade)) {
                    int j = i + grade.length();
                    while(fullNotesStr.charAt(j) != '#'){
                        j++;
                    }
                    fullNotesStr = fullNotesStr.substring(0, i + grade.length() + 1)
                            + curGradeNotesStr
                            + fullNotesStr.substring(j);
                    break;
                }
            }
        }
    }

    public static void setNotes(String fullNotesStr) {
        setFullNotesStr(fullNotesStr);

        if (fullNotesStr == null)
            return;

        curGradeNotesStr = "";

        for (int i = 0; i < fullNotesStr.length(); i++) {
            if (fullNotesStr.charAt(i) == 'g') {
                i++;
                if ((fullNotesStr.substring(i, i + grade.length())).equals(grade)) {
                    for (int j = i + grade.length() + 1; j < fullNotesStr.length(); j++) {
                        if (fullNotesStr.charAt(j) == '#') {
                            curGradeNotesStr = fullNotesStr.substring(i + grade.length() + 1, j);
                            break;
                        }
                    }
                }
            }
        }

        setNotesHM();
    }

    private static void setNotesHM() {
        int stName;
        int stNote;
        for(int i = 0; i < curGradeNotesStr.length(); i++){
            stName = i;
            while(curGradeNotesStr.charAt(i) != '$'){
                i++;
                if(i == curGradeNotesStr.length())
                    return;
            }
            String curName = curGradeNotesStr.substring(stName, i);
            i++;
            ArrayList<String> curNotesArray = new ArrayList<>();
            while(curGradeNotesStr.charAt(i) != '|') {
                stNote = i;
                while (curGradeNotesStr.charAt(i) != ';') {
                    i++;
                    if(i == curGradeNotesStr.length())
                        return;
                }
                curNotesArray.add(curGradeNotesStr.substring(stNote, i));
                i++;
                if(i == curGradeNotesStr.length())
                    return;
            }
            notesHM.put(curName, curNotesArray);
        }
    }

    private static void removeNoteFromHM(int positionToRemove){
        ArrayList<String> updCurNotesArray = notesHM.get(curTextName);
        try{
            updCurNotesArray.remove(positionToRemove);
        }
        catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        notesHM.put(curTextName, updCurNotesArray);
    }

    private static void addNoteToHM(String newNote){
        ArrayList<String> updCurNotesArray = notesHM.get(curTextName);
        if(updCurNotesArray == null) {
            updCurNotesArray = new ArrayList<String>();
        }
        updCurNotesArray.add(newNote.substring(0, newNote.length() - 1)); //discarding ';'
        notesHM.put(curTextName, updCurNotesArray);
    }

    public static void setCurTextName(String curTextName) {
        DataStorage.curTextName = curTextName;
    }

    public static void setCurTextAuthor(String curTextAuthor) {
        DataStorage.curTextAuthor = curTextAuthor;
    }
}
