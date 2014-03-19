package ru.eternalkaif.soundsofnature.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import ru.eternalkaif.soundsofnature.objects.Sound;

import static ru.eternalkaif.soundsofnature.db.SoundsDataBaseContract.*;

public class SoundsOpenHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "sounds.db";

    public SoundsOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SOUNDS_TABLE = "CREATE TABLE " + Sounds.TABLE_NAME + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Sounds.NamesColoumns.SOUNDTITLE + " TEXT NOT NULL, "
                + Sounds.NamesColoumns.SOUNDMP3LINK + " TEXT, "
                + Sounds.NamesColoumns.SOUNDJPGLINK + " TEXT, "
                + Sounds.NamesColoumns.DOWNLOADED + " INTEGER DEFAULT 0"
                + ") ";
        db.execSQL(CREATE_SOUNDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP DATABASE IF EXISTS " + Sounds.TABLE_NAME);
        onCreate(db);
    }


    public void addSong(Sound item) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(Sounds.NamesColoumns.SOUNDTITLE, item.getSoundtitle());
        contentValues.put(Sounds.NamesColoumns.SOUNDMP3LINK, item.getSoundmp3link());
        contentValues.put(Sounds.NamesColoumns.SOUNDJPGLINK, item.getSoundjpglink());
        contentValues.put(Sounds.NamesColoumns.DOWNLOADED, item.isDownloaded());

        if (db != null) {
            db.insert(Sounds.TABLE_NAME, null, contentValues);
            db.close();
        }
    }

    public boolean ifExistByUrl(String songUrl) {

        SQLiteDatabase db = getReadableDatabase();
        if (db != null) {
            Cursor cursor = db.query(Sounds.TABLE_NAME,
                    new String[]{BaseColumns._ID,
                            Sounds.NamesColoumns.SOUNDTITLE,
                            Sounds.NamesColoumns.SOUNDMP3LINK,
                            Sounds.NamesColoumns.SOUNDJPGLINK,
                            Sounds.NamesColoumns.DOWNLOADED},
                    Sounds.NamesColoumns.SOUNDMP3LINK + "=?",
                    new String[]{songUrl},
                    null, null, null, null
            );
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    return true;
                }


            }
        }
        return false;
    }

    public Sound getSong(int id) {
        SQLiteDatabase db = getReadableDatabase();

        if (db != null) {
            Cursor cursor = db.query(Sounds.TABLE_NAME,
                    new String[]{BaseColumns._ID,
                            Sounds.NamesColoumns.SOUNDTITLE,
                            Sounds.NamesColoumns.SOUNDMP3LINK,
                            Sounds.NamesColoumns.SOUNDJPGLINK,
                            Sounds.NamesColoumns.DOWNLOADED},
                    BaseColumns._ID + "=?",
                    new String[]{String.valueOf(id)},
                    null, null, null, null
            );
            if (cursor != null) {
                cursor.moveToFirst();

                return new Sound(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4));
            }
        }
        return null;

    }

    public List<Sound> getAllSongs() {

        List<Sound> soundList = new ArrayList<Sound>();
        String selectQuery = "SELECT * FROM " + Sounds.TABLE_NAME;

        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor;
        if (database != null) {
            cursor = database.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    Sound sound = new Sound(
                            cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4));
                    soundList.add(sound);
                } while (cursor.moveToNext());
            }
        }

        return soundList;
    }

    public int getSongsCount() {
        String songQuery = "SELECT * FROM " + Sounds.TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor;
        if (db != null) {
            cursor = db.rawQuery(songQuery, null);
            cursor.close();
            return cursor.getCount();

        }
        return 0;
    }

    public int updateSong(Sound sound) {
        return 0;
    }

    public void deleteSong(int id) {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            db.delete(Sounds.TABLE_NAME, BaseColumns._ID + " =?",
                    new String[]{String.valueOf(id)});
            db.close();
        }

    }

    public void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + Sounds.TABLE_NAME);
    }
}
