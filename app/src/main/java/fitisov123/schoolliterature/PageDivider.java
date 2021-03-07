package fitisov123.schoolliterature;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fitisov123.fb2_decoder.Span;

public class PageDivider extends AsyncTask<Context, Integer, Void> {

    private static int charsInLine = 31;
    private static int linesOnPage = 22;

    private static Context context;

    public PageDivider(Layout layout){
        float dpHeight = DataStorage.getWindowHeightDP() - 60f - 10f - 10f - 10f - 10f - 30f;
        float dpWidth  = DataStorage.getWindowWidthDP() - 10f - 10f - 10f - 10f - 10f;
        linesOnPage = (int)Math.floor(dpHeight / 24.0f);

        if (layout != null) {
            charsInLine = layout.getLineEnd(0) - layout.getLineStart(0) - 1;
        } else {
            charsInLine = (int)Math.floor(dpWidth / 10.0f);
        }

        Logger logger = Logger.getLogger(PageDivider.class.getName());
        logger.log(Level.INFO, "width: " + String.valueOf(dpWidth));
        logger.log(Level.INFO, "height: " + String.valueOf(dpHeight));
        logger.log(Level.INFO, "lines on page: " + String.valueOf(linesOnPage));
        logger.log(Level.INFO, "chars in line: " + String.valueOf(charsInLine));
    }

    public void setNextPageStartIndex(Context context, int curPage) {
        int startIndex = CacheManager.getTextStartIndexOnPage(context, DataStorage.getCurTextName(), curPage);
        int curIndexInText = startIndex;

        //string of current word
        StringBuilder curWord = new StringBuilder();

        //whether we have reached the end of text
        boolean endReached = false;

        for (int line = 0; line < linesOnPage; line++) {
            if (endReached) {
                break;
            }

            for (int i = curWord.length(); i < charsInLine; i++) {
                if (curWord.length() == charsInLine - 1) {
                    curWord = new StringBuilder();
                    continue;
                }

                char curChar = DataStorage.getCurText().charAt(curIndexInText);
                if (Character.isWhitespace(curChar) || curChar == '\r' || curChar == '\n') {
                    if (curWord.length() != 0) {
                        curWord = new StringBuilder();
                        if (curChar == '\r'
                                && curIndexInText + 1 < DataStorage.getCurText().length()
                                && DataStorage.getCurText().charAt(curIndexInText) == '\n') {
                            curIndexInText++;
                        }
                    }
                } else {
                    curWord.append(curChar);
                }
                if (curChar == '\r' || curChar == '\n') {
                    //make this iteration of cycle the last one
                    i = charsInLine - 1;
                }

                curIndexInText++;

                if (curIndexInText >= DataStorage.getCurText().length()) {
                    endReached = true;
                    break;
                }
            }
        }
        if (!endReached) {
            int nextPageStart = curIndexInText - curWord.length();
            CacheManager.setTextStartIndexOnPage(context, DataStorage.getCurTextName(), curPage + 1, nextPageStart);
        } else {
            CacheManager.setTextLastPage(context, DataStorage.getCurTextName(), curPage);
        }
    }

    private static SpannableStringBuilder preparePageText(SpannableStringBuilder pageText,
                                                          Context context) {
        if (CacheManager.getSettingsAlignmentMode(context) == TextAlignment.leftAlignment) {
            return pageText;
        } else {
            SpannableStringBuilder resultText = new SpannableStringBuilder();
            int curCharIndex = 0;
            while (curCharIndex < pageText.length()) {
                ArrayList<SpannableStringBuilder> wordsInLine = new ArrayList<>();
                SpannableStringBuilder curWord = new SpannableStringBuilder();
                int nonspaceCharCounter = 0;
                for (int inLineIndex = 0; inLineIndex < charsInLine; inLineIndex++, curCharIndex++) {
                    if (curCharIndex >= pageText.length()) {
                        break;
                    }
                    char curChar = pageText.charAt(curCharIndex);
                    if (curChar == ' ' || curChar == '\n') {
                        wordsInLine.add(curWord);
                        curWord = new SpannableStringBuilder();
                        if (curChar == '\n') {
                            break;
                        }
                    } else {
                        curWord.append(curChar);
                        nonspaceCharCounter++;
                    }
                }
                if (curWord.length() == charsInLine) {
                    wordsInLine.add(curWord);
                } else if (curWord.length() != 0) {
                    curCharIndex -= curWord.length();
                    nonspaceCharCounter -= curWord.length();
                }
                if (wordsInLine.size() > 1) {
                    int smallSpacingLength = (charsInLine - nonspaceCharCounter) / (wordsInLine.size() - 1);
                    int largeSpacingLength = smallSpacingLength + 1;
                    int largeCnt = (charsInLine - nonspaceCharCounter) % (wordsInLine.size() - 1);
                    StringBuilder smallSpacing = getSpacingByLength(smallSpacingLength);
                    StringBuilder largeSpacing = getSpacingByLength(largeSpacingLength);
                    SpannableStringBuilder curLine = new SpannableStringBuilder(wordsInLine.get(0));
                    for (int i = 0; i < wordsInLine.size() - 1; i++) {
                        if (largeCnt > 0) {
                            curLine.append(largeSpacing);
                            largeCnt--;
                        } else {
                            curLine.append(smallSpacing);
                        }
                        curLine.append(wordsInLine.get(i + 1));
                    }
                    resultText.append(curLine);
                    resultText.append(" ");
                } else if (wordsInLine.size() == 1) {
                    resultText.append(wordsInLine.get(0));
                    resultText.append(" ");
                    curCharIndex++;
                } else {
                    resultText.append("\n");
                    curCharIndex++;
                }
            }
            return resultText;
        }
    }

    private static StringBuilder getSpacingByLength(int spacingLength) {
        StringBuilder spacing = new StringBuilder();
        for (int i = 0; i < spacingLength; i++) {
            spacing.append(' ');
        }
        return spacing;
    }

    public static Page getPage(final Context context, final Context activityContext, int curPage) {
        //resulting page
        Page result = new Page();
        CacheManager.setTextCurPage(context, DataStorage.getCurTextName(), curPage);
        int startIndex = CacheManager.getTextStartIndexOnPage(context, DataStorage.getCurTextName(), curPage);
        int nextPageStart = CacheManager.getTextStartIndexOnPage(context, DataStorage.getCurTextName(), curPage + 1);
        int endIndex = DataStorage.getCurText().length() - 1;
        if (nextPageStart != -1) {
            endIndex = nextPageStart - 1;
        }

        result.pageText = new SpannableStringBuilder(DataStorage.getCurText().substring(startIndex, endIndex + 1));

        //we get the spans which are on our page
        ArrayList<Span> spans = DataStorage.decoder.textSpans.getPageSpanList(startIndex, endIndex);

        if (spans == null) {
            result.pageText = preparePageText(result.pageText, context);
            return result;
        }

        for (int i = 0; i < spans.size(); i++) {
            final Span span = spans.get(i);
            StyleSpan styleSpan = new StyleSpan(Typeface.NORMAL);
            switch (span.type) {
                case EMPHASIS : styleSpan = new StyleSpan(Typeface.BOLD_ITALIC); break;
                case STRONG   : styleSpan = new StyleSpan(Typeface.BOLD);        break;
                case STYLE    : styleSpan = new StyleSpan(Typeface.ITALIC);      break;
                case REF      : styleSpan = new StyleSpan(Typeface.BOLD_ITALIC);
                    SpannableStringBuilder noteText = new SpannableStringBuilder();
                    try {
                        noteText = new SpannableStringBuilder(
                                DataStorage.decoder.noteList.getNote(span.noteId).getText()
                        );
                        processNote(noteText, span.noteId);
                    } catch (Exception e) {
                        Algorithm.logMessage("null note text " + e.getMessage() + "\n while reading " + span.noteId + " id note");
                    }
                    result.notesText.append(noteText);
            }
            //recalc in page indexes
            result.pageText.setSpan(
                    styleSpan,
                    span.startIndex - startIndex,
                    span.endIndex - startIndex + 1,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE
            );
        }

        result.pageText = preparePageText(result.pageText, context);

        return result;
    }

    private static void processNote(SpannableStringBuilder noteText, String noteId) {
        List<Span> innerSpans = DataStorage.decoder.noteList.getNote(noteId).getSpans();
        for (int j = 0; j < innerSpans.size(); j++) {
            final Span innerSpan = innerSpans.get(j);
            Object innerStyleSpan = new StyleSpan(Typeface.NORMAL);
            switch (innerSpan.type) {
                case EMPHASIS : innerStyleSpan = new StyleSpan(Typeface.BOLD_ITALIC); break;
                case STRONG   : innerStyleSpan = new StyleSpan(Typeface.BOLD);        break;
                case STYLE    : innerStyleSpan = new StyleSpan(Typeface.ITALIC);      break;
            }
            noteText.setSpan(innerStyleSpan, innerSpan.startIndex, innerSpan.endIndex + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate  (progress);
        TextFragment.preparingPB.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Algorithm.logMessage("ON POST EXECUTE");
        DataStorage.decoder.textSpans.divideSpans(context);
        DataStorage.decoder.textSpans.sortSpans();
        Algorithm.logMessage("HERE!!!!!!!!!!!!!!!!!!!");
        DataStorage.textFragment.afterPageCalculation();
    }

    @Override
    protected Void doInBackground(Context... contexts) {
        context = contexts[0];
        double textLength = DataStorage.getCurText().length();
        double progress = 0;
        int curPage = 0;
        CacheManager.setTextLastPage(context, DataStorage.getCurTextName(), -1);
        while (CacheManager.getTextLastPage(context, DataStorage.getCurTextName()) == -1) {
            setNextPageStartIndex(context, curPage);
            curPage++;
            if (CacheManager.getTextLastPage(context, DataStorage.getCurTextName()) != -1) {
                break;
            }
            double cntCurPage = CacheManager.getTextStartIndexOnPage(
                    context,
                    DataStorage.getCurTextName(),
                    curPage
            )
                    - CacheManager.getTextStartIndexOnPage(
                    context,
                    DataStorage.getCurTextName(),
                    curPage - 1
            );
            progress += cntCurPage;
            publishProgress((int)Math.floor((progress / textLength) * 100));
        }
        return null;
    }
}
