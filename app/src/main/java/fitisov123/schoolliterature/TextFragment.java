package fitisov123.schoolliterature;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TextFragment extends Fragment {

    private static TextView text_tv, text_notes_tv, cur_page_tv, max_page_tv, slash_tv, downloadingWarningTV, preparingWarningTV;
    private static Context context;
    private static View layout;
    private static RelativeLayout relativeLayout;
    public static ProgressBar downloadingPB, preparingPB;
    private static PageDivider pageDivider;
    private static Button nextPage, prevPage;
    private static Dialog dialogGoToPage;
    private static Button goToPage;
    private static Button peekButton;
    private static boolean isPeekButtonOpened = false;

    private static Layout buttonsLayout;
    private static RelativeLayout textLayout;

    private static ImageView pageBackground;
    private static ArrayList<TextView> pageTVs;


    public void afterTextDownloading(){
        downloadingPB.setVisibility(View.GONE);
        downloadingWarningTV.setVisibility(View.GONE);
        preparingPB.setVisibility(View.VISIBLE);
        preparingWarningTV.setVisibility(View.VISIBLE);

        StringBuilder testString = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            testString.append('А');
        }
        text_tv.setText(testString);
        text_tv.setVisibility(View.GONE);

        pageDivider = new PageDivider(text_tv.getLayout());

        text_tv.setText("");
        text_tv.setVisibility(View.VISIBLE);

        if (CacheManager.getTextLastPage(context, DataStorage.getCurTextName()) == -1) {
            calculatePages();
        } else {
            preparingPB.setProgress(100);
            afterPageCalculation();
        }
    }

    public void afterPageCalculation(){
        int lastPage = CacheManager.getTextCurPage(context, DataStorage.getCurTextName());

        max_page_tv.setText(String.valueOf(CacheManager.getTextLastPage(context, DataStorage.getCurTextName()) + 1));

        for(TextView tv : pageTVs)
            tv.setVisibility(View.VISIBLE);

        preparingPB.setVisibility(View.GONE);
        preparingWarningTV.setVisibility(View.GONE);

        goToPage.setClickable(true);
        nextPage.setClickable(true);
        prevPage.setClickable(true);
        peekButton.setClickable(true);

        TextFragmentManager.openNotes.setClickable(true);
        if(MainActivity.interstitialAd.isLoaded()){
            MainActivity.interstitialAd.show();
        }
        showPage(lastPage);
    }

    private void calculatePages(){
        pageDivider.execute(context);
    }

    private void showPage(int curPageNumber){
        if(!DataStorage.isPageInBoundaries(context, curPageNumber) )
            return;
        cur_page_tv.setText(String.valueOf(curPageNumber + 1));

        Page curPage = PageDivider.getPage(context, getActivity(), curPageNumber);
        if(curPage == null)
            return;
        text_tv.setText(curPage.pageText);
        text_notes_tv.setText(curPage.notesText);
        text_notes_tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void showNextPage(){
        int curPage = CacheManager.getTextCurPage(context, DataStorage.getCurTextName());
        showPage(curPage + 1);
    }

    private void showPrevPage(){
        int curPage = CacheManager.getTextCurPage(context, DataStorage.getCurTextName());
        showPage(curPage - 1);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_text, container, false);

        relativeLayout = (RelativeLayout) layout.findViewById(R.id.textLayout);
        text_tv = (TextView) layout.findViewById(R.id.bookText);
        text_notes_tv = (TextView) layout.findViewById(R.id.text_notes_tv);
        slash_tv = (TextView) layout.findViewById(R.id.slash);
        cur_page_tv = (TextView) layout.findViewById(R.id.cur_page);
        max_page_tv = (TextView) layout.findViewById(R.id.max_page);
        goToPage = (Button) layout.findViewById(R.id.go_to_bage_bn);
        downloadingPB = (ProgressBar) layout.findViewById(R.id.determinate_pb_download);
        preparingPB = (ProgressBar) layout.findViewById(R.id.determinate_pb_prepare);
        downloadingWarningTV = (TextView) layout.findViewById(R.id.downloadingWarningTV);
        preparingWarningTV = (TextView) layout.findViewById(R.id.preparingWarningTV);
        nextPage = (Button) layout.findViewById(R.id.next_page_button);
        prevPage = (Button) layout.findViewById(R.id.prev_page_button);
        peekButton = (Button) layout.findViewById(R.id.peek_button);
        textLayout = (RelativeLayout) layout.findViewById(R.id.text_and_notes_layout);

        pageBackground = (ImageView) layout.findViewById(R.id.page_background);

        pageTVs = new ArrayList<>();
        pageTVs.add(cur_page_tv);
        pageTVs.add(max_page_tv);
        pageTVs.add(slash_tv);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "TextFont2.ttf");
        text_tv.setTypeface(typeface);
        text_tv.setBreakStrategy(Layout.BREAK_STRATEGY_SIMPLE);

        TextStyle style = CacheManager.getSettingsStyleMode(context);
        switchToNightMode(style);

        goToPage.setClickable(false);
        nextPage.setClickable(false);
        prevPage.setClickable(false);
        peekButton.setClickable(false);

        goToPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialogMoveToPage();
            }
        });

        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextPage();
            }
        });

        prevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrevPage();
            }
        });

        peekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPeekButtonOpened) {
                    nextPage.setClickable(false);
                    prevPage.setClickable(false);
                    openTextNotes(v, text_tv);
                    isPeekButtonOpened = true;
                } else {
                    nextPage.setClickable(true);
                    prevPage.setClickable(true);
                    closeTextNotes(v, text_tv);
                    isPeekButtonOpened = false;
                }
            }
        });

        return layout;
    }

    private void openTextNotes(final View button, final View textTv) {
        float distInDP = 300f * DataStorage.getDensity();

        ObjectAnimator animationBN = ObjectAnimator.ofFloat(button, "translationY", -distInDP);
        animationBN.setDuration(300);
        animationBN.start();

        ObjectAnimator animationTV = ObjectAnimator.ofFloat(textTv, "translationY", -distInDP);
        animationTV.setDuration(300);
        animationTV.start();
    }

    private void closeTextNotes(final View button, final View textTv) {
        float distInDP = 1f * DataStorage.getDensity();

        ObjectAnimator animationBN = ObjectAnimator.ofFloat(button, "translationY", distInDP);
        animationBN.setDuration(300);
        animationBN.start();

        ObjectAnimator animationTV = ObjectAnimator.ofFloat(textTv, "translationY", distInDP);
        animationTV.setDuration(300);
        animationTV.start();
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private void createDialogMoveToPage(){
        dialogGoToPage = new Dialog(getActivity());
        dialogGoToPage.setContentView(R.layout.dialog_set_page);
        final EditText page_et = (EditText) dialogGoToPage.findViewById(R.id.page_num_et);
        final ImageButton submittingNewPage = (ImageButton) dialogGoToPage.findViewById(R.id.submit_page_bn);

        submittingNewPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Algorithm.logMessage("goto page clicked");

                if(!isEmpty(page_et)) {
                    submittingNewPage.setClickable(false);
                    Integer newPageNumber = Integer.valueOf(page_et.getText().toString());
                    showPage(newPageNumber - 1);
                    dialogGoToPage.dismiss();
                }
                else
                {
                    Toast.makeText(context, "Необходимо ввести номер страницы!", Toast.LENGTH_LONG).show();
                }
            }
        });

        dialogGoToPage.show();
    }

    @Override
    public void onStart() {
        if(BookTextRequest.isRequestReady())
            afterTextDownloading();
        super.onStart();
    }

    public static void switchToNightMode(TextStyle style){
        if(style.equals(TextStyle.darkMode)){
            for(TextView tv : pageTVs){
                tv.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
            }
            text_tv.setBackgroundColor(ContextCompat.getColor(context, R.color.Darker));
            text_tv.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
            text_notes_tv.setBackgroundColor(ContextCompat.getColor(context, R.color.Darker));
            text_notes_tv.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
            pageBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.Darker));
            relativeLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.mainDark));
            preparingWarningTV.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
            downloadingWarningTV.setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
        } else {
            for(TextView tv : pageTVs){
                tv.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
            }
            text_tv.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
            text_tv.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
            text_notes_tv.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
            text_notes_tv.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
            pageBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
            relativeLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorWhite));
            preparingWarningTV.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
            downloadingWarningTV.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
        }
    }
}

