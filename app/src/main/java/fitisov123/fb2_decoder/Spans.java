package fitisov123.fb2_decoder;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import fitisov123.schoolliterature.Algorithm;
import fitisov123.schoolliterature.CacheManager;
import fitisov123.schoolliterature.DataStorage;

public class Spans extends SpannableResource {
    public Spans() {
        spans = new ArrayList<>();
        processedLength = 0;
    }

    @Override
    public void addSpan(Span span){
        spans.add(span);
    }

    public void sortSpans() {
        Algorithm.logMessage("STARTED SORTING");

        spans = Algorithm.mergeSort(spans, 0, spans.size() - 1);
    }

    public void divideSpans(Context context) {
        String curTextName = DataStorage.getCurTextName();
        ArrayList<Span> newSpans = new ArrayList<>();
        Algorithm.logMessage("Spans size: " + String.valueOf(spans.size()));
        for (int i = 0; i < spans.size(); i++) {
            Span span = spans.get(i);
            int l = -1, r = CacheManager.getTextLastPage(context, curTextName) + 1, m;
            while (r - l > 1) {
                m = l + (r - l) / 2;
                if (span.startIndex >= CacheManager.getTextStartIndexOnPage(context, curTextName, m)) {
                    l = m;
                } else {
                    r = m;
                }
            }
            int curPageStartIndex = CacheManager.getTextStartIndexOnPage(context, curTextName, l);
            while (curPageStartIndex <= span.endIndex && CacheManager.doesPageExist(context, curTextName, l)) {
                if (curPageStartIndex < span.startIndex) {
                    int nextPageStartIndex = CacheManager.getTextStartIndexOnPage(context, curTextName, l + 1);
                    newSpans.add(new Span(span.type, span.startIndex, Math.min(nextPageStartIndex - 1, span.endIndex)));
                    curPageStartIndex = CacheManager.getTextStartIndexOnPage(context, curTextName, ++l);
                    continue;
                }
                if (curPageStartIndex > span.endIndex) {
                    int prevPageStartIndex = CacheManager.getTextStartIndexOnPage(context, curTextName, l - 1);
                    newSpans.add(new Span(span.type, prevPageStartIndex, span.endIndex));
                    curPageStartIndex = CacheManager.getTextStartIndexOnPage(context, curTextName, ++l);
                    continue;
                }
                int prevPageStartIndex = CacheManager.getTextStartIndexOnPage(context, curTextName, l - 1);
                newSpans.add(new Span(span.type, prevPageStartIndex, curPageStartIndex - 1));
                curPageStartIndex = CacheManager.getTextStartIndexOnPage(context, curTextName, ++l);
            }
            Algorithm.logMessage("cur span is " + i);
        }
    }

    private void carefullyAddSpan(List<Span> result, int spanIndex, int startPageIndex, int endPageIndex) {
        if (spanIndex >= spans.size() || spanIndex < 0) {
            return;
        }

        Span span = spans.get(spanIndex);

        if (span.endIndex < startPageIndex || endPageIndex < span.startIndex) {
            return;
        }

        if (span.startIndex <= startPageIndex && endPageIndex <= span.endIndex) {
            result.add(new Span(span.type, startPageIndex, endPageIndex));
            return;
        }

        /*
         * if the first half of span is on the previous page
         * we return only second part which is on current page
         */
        if (span.startIndex < startPageIndex) {
            result.add(new Span(span.type, startPageIndex, span.endIndex));
            return;
        }

        /*
         * if the second half of span is on the next page
         * we return only first part which is on current page
         */
        if (endPageIndex < span.endIndex) {
            result.add(new Span(span.type, span.startIndex, endPageIndex));
            return;
        }

        //by default we return our span
        result.add(span);
    }

    public ArrayList<Span> getPageSpanList(int startPageIndex, int endPageIndex) {
        ArrayList<Span> result = new ArrayList<>();

        int l = -1, r = spans.size(), m;
        while (r - l > 1) {
            m = l + (r - l) / 2;
            if (spans.get(m).startIndex >= startPageIndex) {
                r = m;
            } else {
                l = m;
            }
        }

        int startSpanIndex = r;

        l = -1;
        r = spans.size();
        while (r - l > 1) {
            m = l + (r - l) / 2;
            if (spans.get(m).startIndex <= endPageIndex) {
                l = m;
            } else {
                r = m;
            }
        }

        int endSpanIndex = l;

        for (int i = startSpanIndex; i <= endSpanIndex; i++) {
            carefullyAddSpan(result, i, startPageIndex, endPageIndex);
        }
        return result;
    }
}
