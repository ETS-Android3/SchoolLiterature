package fitisov123.fb2_decoder;

import java.util.HashMap;

public class Notes{
    private HashMap<String, Note> notes = new HashMap<>();

    public Notes() {}

    public Note getNote(String id) {
        return (Note) notes.get(id);
    }

    public void addNote(String id, Note note){
        notes.put(id, note);
    }
}
