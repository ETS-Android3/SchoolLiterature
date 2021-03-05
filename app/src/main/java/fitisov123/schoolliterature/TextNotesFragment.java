package fitisov123.schoolliterature;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class TextNotesFragment extends Fragment {

    private static View layout;
    private static Context context;
    private static HashMap<Integer, String> localNotesHM = new HashMap<>();
    private static TextView noNotesTv;
    private static Dialog dialogNewNote, dialogChangeOrDeleteNote;
    private static ImageButton addNoteButton;
    private static ListView notesList;
    private static ArrayList<String> notesArray;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        layout = inflater.inflate(R.layout.fragment_text_notes, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

        notesList = (ListView) layout.findViewById(R.id.notes_list);
        noNotesTv = (TextView) layout.findViewById(R.id.noNotes_tv);
        addNoteButton = (ImageButton) layout.findViewById(R.id.add_note_button);

        initialiseNotesList();

        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final View chosenItem = view;
                String[] itemBundle = (String[]) chosenItem.getTag(); //first: index in database; second: page number; third: note text
                Integer pageNumber = Integer.valueOf(itemBundle[1]);
                CacheManager.setTextCurPage(context, DataStorage.getCurTextName(), pageNumber);
                TextFragmentManager.useTextFragment();
            }
        });

        notesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final View chosenItem = view;
                String[] itemBundle = (String[]) chosenItem.getTag(); //first: index in database; second: page number; third: note text
                Integer itemPosition = Integer.valueOf(itemBundle[0]);
                Integer notePage = Integer.valueOf(itemBundle[1]);
                String noteText = itemBundle[2];
                createDialogChangeOrDeleteNote(itemPosition, notePage, noteText);
                return true;
            }
        });

        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialogNewNote();
            }
        });

        return layout;
    }

    public static void initialiseNotesList(){
        notesArray = DataStorage.getNotesHM().get(DataStorage.getCurTextName());

        if(notesArray == null || notesArray.isEmpty()){
            noNotesTv.setVisibility(View.VISIBLE);
            notesList.setVisibility(View.GONE);
        }
        else {
            noNotesTv.setVisibility(View.GONE);
            notesList.setVisibility(View.VISIBLE);
            ArrayList<Pair<Integer, String> > notes = new ArrayList<>();
            for(String note : notesArray){
                Pair<Integer, String> noteDecoded = DataStorage.decodeNote(context, note);
                Integer pageNumber = noteDecoded.first;
                String noteText = noteDecoded.second;
                localNotesHM.put(pageNumber, noteText);
                notes.add(new Pair<Integer, String>(pageNumber, noteText));
            }
            NotesListAdapter notesListAdapter = new NotesListAdapter(context, notes);
            notesList.setAdapter(notesListAdapter);
        }
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public void createDialogChangeOrDeleteNote(final int position, final Integer notePage, final String noteText){
        dialogChangeOrDeleteNote = new Dialog(getActivity());
        dialogChangeOrDeleteNote.setContentView(R.layout.dialog_change_or_delete_note);
        final Button changeNoteBn = (Button) dialogChangeOrDeleteNote.findViewById(R.id.change_note);
        final Button deleteNoteBn = (Button) dialogChangeOrDeleteNote.findViewById(R.id.delete_note);

        deleteNoteBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataStorage.eraseFromGradeNotesStr(position);
                DataStorage.updateCurGradePartInFullNotesStr();
                new UserDataRequest(context).execute("update_notes", DataStorage.getUserId(), DataStorage.getFullNotesStr());
                initialiseNotesList();
                dialogChangeOrDeleteNote.dismiss();
            }
        });

        changeNoteBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChangeOrDeleteNote.dismiss();
                final Dialog changeNoteDialog = new Dialog(getActivity());
                changeNoteDialog.setContentView(R.layout.dialog_change_note);
                final EditText editNoteEt = (EditText) changeNoteDialog.findViewById(R.id.change_note_et);
                final ImageButton submitChangeBn = (ImageButton) changeNoteDialog.findViewById(R.id.submit_change_note);
                final EditText pageEt = (EditText) changeNoteDialog.findViewById(R.id.page_num_et);
                final ProgressBar loadingPB = (ProgressBar) changeNoteDialog.findViewById(R.id.change_note_loadingPB);

                pageEt.setText(String.valueOf(notePage + 1));
                editNoteEt.setText(noteText);

                submitChangeBn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isEmpty(editNoteEt)) {
                            Integer curNotePage = notePage;
                            String curNoteText = editNoteEt.getText().toString();
                            submitChangeBn.setClickable(false);
                            submitChangeBn.setVisibility(View.GONE);
                            loadingPB.setVisibility(View.VISIBLE);
                            String newNoteText = editNoteEt.getText().toString();
                            if (!FieldChecker.correctField(newNoteText, false)) {
                                Toast.makeText(context, FieldChecker.noteTextWrongToastString, Toast.LENGTH_LONG).show();
                                loadingPB.setVisibility(View.GONE);
                                submitChangeBn.setVisibility(View.VISIBLE);
                                submitChangeBn.setClickable(true);
                                return;
                            }
                            if(!isEmpty(pageEt)){
                                Integer newPageNumber = Integer.valueOf(pageEt.getText().toString()) - 1;
                                if(DataStorage.isPageInBoundaries(context, newPageNumber))
                                    curNotePage = newPageNumber;
                                else {
                                    Toast.makeText(context, "Выбрана несуществующая страница", Toast.LENGTH_LONG).show();
                                    loadingPB.setVisibility(View.GONE);
                                    submitChangeBn.setVisibility(View.VISIBLE);
                                    submitChangeBn.setClickable(true);
                                    return;
                                }
                            }
                            else {
                                Toast.makeText(context, "Страница не выбрана", Toast.LENGTH_LONG).show();
                                loadingPB.setVisibility(View.GONE);
                                submitChangeBn.setVisibility(View.VISIBLE);
                                submitChangeBn.setClickable(true);
                                return;
                            }
                            int newNoteIndex = CacheManager.getTextStartIndexOnPage(context, DataStorage.getCurTextName(), curNotePage);
                            DataStorage.eraseFromGradeNotesStr(position);
                            DataStorage.addToCurGradeNotesStr(curNoteText, String.valueOf(newNoteIndex));
                            DataStorage.updateCurGradePartInFullNotesStr();
                            new UserDataRequest(getActivity()).execute("update_notes", DataStorage.getUserId(), DataStorage.getFullNotesStr());
                            initialiseNotesList();
                            changeNoteDialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(context, "Необходимо ввести название заметки!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                changeNoteDialog.show();
            }
        });

        dialogChangeOrDeleteNote.show();
    }

    public void createDialogNewNote(){
        dialogNewNote = new Dialog(getActivity());
        dialogNewNote.setContentView(R.layout.dialog_change_note);
        final EditText newNoteEt = (EditText) dialogNewNote.findViewById(R.id.change_note_et);
        final ImageButton submitNoteBn = (ImageButton) dialogNewNote.findViewById(R.id.submit_change_note);
        final EditText pageEt = (EditText) dialogNewNote.findViewById(R.id.page_num_et);
        final ProgressBar loadingPB = (ProgressBar) dialogNewNote.findViewById(R.id.change_note_loadingPB);

        final Integer prevNotePage = CacheManager.getTextCurPage(context, DataStorage.getCurTextName());

        pageEt.setText(String.valueOf(prevNotePage + 1));

        submitNoteBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmpty(newNoteEt)) {
                    Integer curNotePage = prevNotePage;
                    String curNoteText = newNoteEt.getText().toString();
                    submitNoteBn.setClickable(false);
                    submitNoteBn.setVisibility(View.GONE);
                    loadingPB.setVisibility(View.VISIBLE);
                    String newNoteText = newNoteEt.getText().toString();
                    if(!FieldChecker.correctField(newNoteText, false)){
                        Toast.makeText(context, FieldChecker.noteTextWrongToastString, Toast.LENGTH_LONG).show();
                        loadingPB.setVisibility(View.GONE);
                        submitNoteBn.setVisibility(View.VISIBLE);
                        submitNoteBn.setClickable(true);
                        return;
                    }
                    if(!isEmpty(pageEt)){
                        Integer newPageNumber = Integer.valueOf(pageEt.getText().toString()) - 1;
                        if(DataStorage.isPageInBoundaries(context, newPageNumber))
                            curNotePage = newPageNumber;
                        else {
                            Toast.makeText(context, "Выбрана несуществующая страница", Toast.LENGTH_LONG).show();
                            loadingPB.setVisibility(View.GONE);
                            submitNoteBn.setVisibility(View.VISIBLE);
                            submitNoteBn.setClickable(true);
                            return;
                        }
                    }
                    else {
                        Toast.makeText(context, "Страница не выбрана", Toast.LENGTH_LONG).show();
                        loadingPB.setVisibility(View.GONE);
                        submitNoteBn.setVisibility(View.VISIBLE);
                        submitNoteBn.setClickable(true);
                        return;
                    }
                    int newNoteIndex = CacheManager.getTextStartIndexOnPage(context, DataStorage.getCurTextName(), curNotePage);
                    DataStorage.addToCurGradeNotesStr(curNoteText, String.valueOf(newNoteIndex));
                    DataStorage.updateCurGradePartInFullNotesStr();
                    new UserDataRequest(getActivity()).execute("update_notes", DataStorage.getUserId(), DataStorage.getFullNotesStr());
                    initialiseNotesList();
                    dialogNewNote.dismiss();
                }
                else
                {
                    Toast.makeText(context, "Необходимо ввести название заметки!", Toast.LENGTH_LONG).show();
                }
            }
        });

        dialogNewNote.show();
    }

    public static void afterAddingNote(){

    }
}
