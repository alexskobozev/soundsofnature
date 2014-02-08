package ru.eternalkaif.soundsofnature.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

/**
 * Created by Alexander on 08.02.14.
 */
public class SoundsListCursorAdapter extends CursorAdapter {

    public SoundsListCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    public SoundsListCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SoundsListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
