package ru.eternalkaif.soundsofnature.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SoundsOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "sounds.db";

    public static final String TABLE_SOUNDS_NAME = "sounds";

    private static final String KEY_ID = "_id";
    private static final String KEY_SONGTITLE = "songtitle";
    private static final String KEY_SONG_URL = "songurl";
    private static final String KEY_JPG_URL = "jpgurl";
    private static final String KEY_IS_DOWNLOADED = "is_downloaded";


    public SoundsOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SOUNDS_TABLE = "CREATE TABLE " + TABLE_SOUNDS_NAME + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_SONGTITLE + " TEXT NOT NULL, "
                + KEY_SONG_URL + " TEXT, "
                + KEY_JPG_URL + " TEXT, "
                + KEY_IS_DOWNLOADED + " INTEGER DEFAULT 0"
                + ") ";
        db.execSQL(CREATE_SOUNDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP DATABASE IF EXISTS " + TABLE_SOUNDS_NAME);
        onCreate(db);
    }


    public void addSong(SongItem item) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_SONGTITLE, item.getName());
        contentValues.put(KEY_SONG_URL, item.getSongUrl());
        contentValues.put(KEY_JPG_URL, item.getJpgUrl());
        contentValues.put(KEY_IS_DOWNLOADED, item.isDownloaded());

        if (db != null) {
            db.insert(TABLE_SOUNDS_NAME, null, contentValues);
            db.close();
        }
    }

    public SongItem getSong(int id) {
        SQLiteDatabase db = getReadableDatabase();

        if (db != null) {
            Cursor cursor = db.query(TABLE_SOUNDS_NAME,
                    new String[]{KEY_ID, KEY_SONGTITLE, KEY_SONG_URL, KEY_JPG_URL, KEY_IS_DOWNLOADED},
                    KEY_ID + "=?",
                    new String[]{String.valueOf(id)},
                    null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();

                return new SongItem(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4));
            }
        }
        return null;

    }

    public List<SongItem> getAllSongs() {

        List<SongItem> songItems = new ArrayList<SongItem>();
        String selectQuery = "SELECT * FROM " + TABLE_SOUNDS_NAME;

        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor;
        if (database != null) {
            cursor = database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    SongItem songItem = new SongItem(Integer.parseInt(cursor.getString(0)),
                            cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4));
                    songItems.add(songItem);
                } while (cursor.moveToNext());
            }
        }

        return songItems;
    }

    public int getSongsCount() {
        String songQuery = "SELECT * FROM " + TABLE_SOUNDS_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        if (db != null) {
            cursor = db.rawQuery(songQuery, null);
            cursor.close();
            return cursor.getCount();

        }
        return 0;
    }

    public int updateSong(SongItem item) {
        return 0;
    }

    public void deleteSong(int id) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.delete(TABLE_SOUNDS_NAME, KEY_ID + " =?",
                    new String[]{String.valueOf(id)});
            db.close();
        }

    }
}
