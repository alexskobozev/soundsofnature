package ru.eternalkaif.soundsofnature.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import ru.eternalkaif.soundsofnature.R;
import ru.eternalkaif.soundsofnature.db.SongsTable;

public class SoundsListCursorAdapter extends CursorAdapter {

    LayoutInflater mInflater;
    Cursor mCursor;

    public SoundsListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater.from(context));

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.row_songlist, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView rowText = (TextView) view.findViewById(R.id.songsText);
        rowText.setText(cursor.getString(cursor.getColumnIndex(SongsTable.SONG_TITLE)));
    }
}
