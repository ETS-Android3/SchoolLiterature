package fitisov123.fb2_decoder;

import java.util.ArrayList;

public class Note extends SpannableResource {
    private String id;
    private StringBuilder text = new StringBuilder();

    public Note() {
        spans = new ArrayList<>();
        processedLength = 0;
        id = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StringBuilder getText() {
        return text;
    }

    public void setText(StringBuilder text) {
        this.text = text;
    }

    public Note(String id, StringBuilder text) {
        this.id = id;
        this.text = text;
    }

    public ArrayList<Span> getSpans() {
        return spans;
    }

    @Override
    public void addSpan(Span span){
        spans.add(span);
    }
}
