package fitisov123.fb2_decoder;

import android.support.annotation.NonNull;

public class Span implements Comparable{
    public SpanType type;
    public Integer startIndex, endIndex; // all indexes included
    public String noteId;

    public Span() {
        type = SpanType.DEFAULT;
        startIndex = -1;
        endIndex = -1;
        noteId = null;
    }

    public Span(SpanType type, Integer startIndex, Integer endIndex) {
        this.type = type;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.noteId = null;
    }

    public Span(SpanType type, Integer startIndex, Integer endIndex, String noteId) {
        this.type = type;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.noteId = noteId;
    }

    @Override
    public int compareTo(@NonNull Object object) {
        if (startIndex == ((Span)object).startIndex) {
            return endIndex - ((Span)object).endIndex;
        }
        return startIndex - ((Span)object).startIndex;
    }
}
