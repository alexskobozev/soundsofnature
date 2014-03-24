package ru.eternalkaif.soundsofnature.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ru.eternalkaif.soundsofnature.R;
import ru.eternalkaif.soundsofnature.db.SoundsDataBaseContract;

public class SoundsListCursorAdapter extends CursorAdapter {

    private final int id;
    private final int songtitle;
    LayoutInflater mInflater;
    private Cursor mCursor;
    private Context mContext;

    public SoundsListCursorAdapter(@NotNull Context context, @NotNull Cursor c, int flags) {
        super(context, c, flags);
        mInflater = (LayoutInflater.from(context));
        mCursor = c;
        mContext = context;
        id = c.getColumnIndexOrThrow(BaseColumns._ID);
        songtitle = c.getColumnIndexOrThrow(SoundsDataBaseContract.Sounds.NamesColoumns.SOUNDTITLE);

    }

    @Nullable
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.row_songlist, parent, false);
    }

    @Override
    public void bindView(@NotNull View view, Context context, @NotNull Cursor cursor) {
        TextView rowText = (TextView) view.findViewById(R.id.songsText);
        rowText.setText(cursor.getString(cursor
                .getColumnIndex(SoundsDataBaseContract.Sounds.NamesColoumns.SOUNDTITLE)));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        mCursor.moveToPosition(position);
        if (v == null) {
            holder = new ViewHolder();
            v = mInflater.inflate(R.layout.row_songlist, null);
            if (v != null) {
                holder.tvsoundName = (TextView) v.findViewById(R.id.songsText);
                v.setTag(holder);
            }
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.tvsoundName.setText(mCursor.getString(songtitle));
        return v;
    }

    public static class ViewHolder {
        public TextView tvsoundName;

    }
}
