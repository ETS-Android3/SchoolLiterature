package fitisov123.fb2_decoder;

import java.util.ArrayList;

public abstract class SpannableResource {
    public int processedLength;
    protected ArrayList<Span> spans;

    public abstract void addSpan(Span span);
}
