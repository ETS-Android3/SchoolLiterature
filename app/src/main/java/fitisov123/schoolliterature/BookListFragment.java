package fitisov123.schoolliterature;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookListFragment extends Fragment {

    private static EditText searchEditText;
    private static ProgressBar loadingProgressBar;
    private static ListView bookListView;
    private static Context context;
    private static List<Book> bookSignaturesList;
    private static List<Map<String, String>> bookSignaturesListForAdapter;
    private static SimpleAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        final View layout = inflater.inflate(R.layout.fragment_book_list, container, false);
        bookListView = (ListView) layout.findViewById(R.id.regular_books_list);
        searchEditText = (EditText) layout.findViewById(R.id.search_et);
        loadingProgressBar = (ProgressBar) layout.findViewById(R.id.progressBarBookList);
        //TODO: save position of book list in shared preferences
        changeLoadingStatus(LoadingStatus.start);
        loadBooks();
        return layout;
    }

    private static boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    public static void afterBooksLoaded() {
        makeBookSignaturesList(DataStorage.getTextNames(), DataStorage.getTextAuthors());
        bookSignaturesListForAdapter = new ArrayList<>();
        initCurrentTextNameAuthorPairList();
        initBookListView();
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                initCurrentTextNameAuthorPairList();
                if (s.toString().equals("")) {
                    initBookListView();
                } else {
                    searchItem(s.toString());
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View chosenItem, int position, long id) {
                boolean connected = isConnected();
                if(!connected) {
                    Toast.makeText(context, "Необходимо подключение к интернету", Toast.LENGTH_LONG).show();
                } else {
                    String textName = ((TextView) chosenItem.findViewById(R.id.textName)).getText().toString();
                    String textAuthor = ((TextView) chosenItem.findViewById(R.id.textAuthor)).getText().toString();
                    CacheManager.setBookListPosition(context, DataStorage.getGrade(), position);
                    MainActivity.inflateFragment(new TextFragmentManager(), 0);
                    new BookTextRequest(context).execute(DataStorage.getGrade(), textName, textAuthor);
                }
            }
        });
        bookListView.setSelection(CacheManager.getBookListPosition(context, DataStorage.getGrade()));
        changeLoadingStatus(LoadingStatus.stop);
    }

    public static void loadBooks() {
        if(!DataStorage.isBookListInfoLoaded()) {
            new UserDataRequest(context).execute("get_texts", DataStorage.getGrade());
        } else {
            afterBooksLoaded();
        }
    }

    enum LoadingStatus {
        start,
        stop,
    }

    private static void changeLoadingStatus(LoadingStatus status) {
        switch (status) {
            case start : changeLoadingViews(View.VISIBLE, View.GONE, View.GONE); break;
            case stop : changeLoadingViews(View.GONE, View.VISIBLE, View.VISIBLE); break;
        }
    }

    private static void changeLoadingViews(int loadingPBStatus, int bookListStatus, int searchEditTextStatus) {
        loadingProgressBar.setVisibility(loadingPBStatus);
        bookListView.setVisibility(bookListStatus);
        searchEditText.setVisibility(searchEditTextStatus);
    }

    private static void initCurrentTextNameAuthorPairList() {
        bookSignaturesListForAdapter.clear();
        Map<String, String> map;
        for(int i = 0; i < bookSignaturesList.size(); i++) {
            map = new HashMap<>();
            map.put("Name", bookSignaturesList.get(i).getBookName());
            map.put("Author", bookSignaturesList.get(i).getBookAuthor());
            bookSignaturesListForAdapter.add(map);
        }
    }

    private static void initBookListView() {
        adapter = new SimpleAdapter(
                        context, bookSignaturesListForAdapter, R.layout.books_item_list,
                        new String[]{"Name", "Author"},
                        new int[]{R.id.textName, R.id.textAuthor}
                    );

        bookListView.setAdapter(adapter);
    }

    private static void searchItem(String input) {
        List<Map<String, String>> toDelete = new ArrayList<>();
        input = input.toLowerCase();
        for (Map<String, String> item : bookSignaturesListForAdapter) {
            if(!item.get("Name").toLowerCase().contains(input) && !item.get("Author").toLowerCase().contains(input)) {
                toDelete.add(item);
            }
        }
        bookSignaturesListForAdapter.removeAll(toDelete);
    }

    private static void makeBookSignaturesList(String textNames, String textAuthors) {
        bookSignaturesList = new ArrayList<>();
        int indexTextNames = 0, indexTextAuthors = 0;
        while (indexTextNames < textNames.length() || indexTextAuthors < textAuthors.length()) {
            int startIndexTextNames = indexTextNames, startIndexTextAuthors = indexTextAuthors;
            while (indexTextNames < textNames.length() && textNames.charAt(indexTextNames) != ';') {
                indexTextNames++;
            }
            while (indexTextAuthors < textAuthors.length() && textAuthors.charAt(indexTextAuthors) != ';') {
                indexTextAuthors++;
            }
            String currentName = textNames.substring(startIndexTextNames, indexTextNames);
            String currentAuthor = textAuthors.substring(startIndexTextAuthors, indexTextAuthors);
            bookSignaturesList.add(new Book(currentName, currentAuthor));
            indexTextNames++;
            indexTextAuthors++;
        }
    }
}
