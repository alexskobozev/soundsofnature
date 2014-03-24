package ru.eternalkaif.soundsofnature.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

public class SoundsProvider extends ContentProvider {

    public static final int SOUNDS = 10;
    public static final int SOUND_ID = 20;

    public static final String AUTHORITY = "ru.eternalkaif.db.Sounds";
    public static final String BASE_PATH = "sounds";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/sounds";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/sound";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, SOUNDS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", SOUND_ID);
    }

    private SoundsOpenHelper database;


    public SoundsProvider() {
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case SOUNDS:
                rowsDeleted = sqLiteDatabase.delete(SoundsDataBaseContract.Sounds.TABLE_NAME,
                        selection, selectionArgs);
                break;
            case SOUND_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqLiteDatabase.delete(SoundsDataBaseContract.Sounds.TABLE_NAME,
                            BaseColumns._ID + " = " + id,
                            null);
                } else {
                    rowsDeleted = sqLiteDatabase.delete(SoundsDataBaseContract.Sounds.TABLE_NAME,
                            BaseColumns._ID + " = " + id + " and " + selection,
                            selectionArgs
                    );
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();
        int rowsDeleted = 0;
        long id = 0;
        switch (uriType) {
            case SOUNDS:
                id = sqLiteDatabase.insert(SoundsDataBaseContract.Sounds.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknowen URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public boolean onCreate() {
        database = new SoundsOpenHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        checkColoumns(projection);
        queryBuilder.setTables(SoundsDataBaseContract.Sounds.TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case SOUNDS:
                break;
            case SOUND_ID:
                queryBuilder.appendWhere(BaseColumns._ID + " = " + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("UnknownUri " + uri);

        }
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case SOUNDS:
                rowsUpdated = sqLiteDatabase.update(SoundsDataBaseContract.Sounds.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case SOUND_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqLiteDatabase.update(SoundsDataBaseContract.Sounds.TABLE_NAME,
                            values,
                            BaseColumns._ID + " = " + id,
                            null);
                } else {
                    rowsUpdated = sqLiteDatabase.update(SoundsDataBaseContract.Sounds.TABLE_NAME,
                            values,
                            BaseColumns._ID + " = " + id
                                    + " and " + selection,
                            selectionArgs
                    );
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColoumns(String[] projection) {
        String[] available = {
                SoundsDataBaseContract.Sounds.NamesColoumns.SOUNDTITLE,
                SoundsDataBaseContract.Sounds.NamesColoumns.SOUNDMP3LINK,
                SoundsDataBaseContract.Sounds.NamesColoumns.SOUNDJPGLINK,
                BaseColumns._ID
        };
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }


        }
    }
}
