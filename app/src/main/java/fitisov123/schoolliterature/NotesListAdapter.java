package fitisov123.schoolliterature;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NotesListAdapter extends BaseAdapter {

    private ArrayList<NoteObject> arrayList = new ArrayList<>();
    private Context context;

    private class NoteObject implements Comparable {
        Integer pageNum, index;
        String noteText;

        NoteObject(){}

        public NoteObject(Integer pageNum, String noteText, Integer index) {
            this.pageNum = pageNum;
            this.index = index;
            this.noteText = noteText;
        }

        @Override
        public int compareTo(@NonNull Object o) {
            return pageNum - ((NoteObject)o).pageNum;
        }
    }

    public NotesListAdapter(Context context, ArrayList<Pair<Integer, String>> notesList) {
        for(int i = 0; i < notesList.size(); i++){
            arrayList.add(new NoteObject(notesList.get(i).first, notesList.get(i).second, i));
        }
        arrayList = Algorithm.mergeSort(arrayList, 0, arrayList.size() - 1);
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public NoteObject getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return arrayList.get(position).index;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.notes_item_list, null);
        TextView note_text_tv = (TextView) view.findViewById(R.id.noteText);
        TextView note_page_tv = (TextView) view.findViewById(R.id.notePageNumber);
        note_page_tv.setText(String.valueOf(getItem(position).pageNum + 1));
        note_text_tv.setText(getItem(position).noteText);
        view.setTag(new String[]{String.valueOf(getItem(position).index),
                String.valueOf(getItem(position).pageNum),
                getItem(position).noteText});
        //first: index in database; second: page number; third: note text
        return view;
    }
}
