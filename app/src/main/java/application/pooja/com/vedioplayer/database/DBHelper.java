package application.pooja.com.vedioplayer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyDB.db";
    public static final String CONTACTS_TABLE_NAME = "videoStatusList";


    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_VIDEO_ID = "videoId";
    public static final String CONTACTS_COLUMN_VIDEO_TIME = "time";
    public static final String CONTACTS_COLUMN_VIDEO_SEEK_TIME = "seekTime";


    int i = 0;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + CONTACTS_TABLE_NAME +
                        "( " + CONTACTS_COLUMN_ID + " integer primary key autoincrement , " + CONTACTS_COLUMN_VIDEO_ID + " text ," + CONTACTS_COLUMN_VIDEO_TIME + " text," + CONTACTS_COLUMN_VIDEO_SEEK_TIME + " text)"
        );


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + CONTACTS_TABLE_NAME);
        onCreate(db);
    }

    public int getVedioStatus() {
        String countQuery = "SELECT  * FROM " + CONTACTS_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }


    public boolean insertVideoStatus(String videoId, long vedioTime, long vedioSeekTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        int available = getVedioStatusIfAdded(videoId);

        if(available == 1)
        {
            ContentValues cv = new ContentValues();
            cv.put(CONTACTS_COLUMN_VIDEO_SEEK_TIME,vedioSeekTime); //These Fields should be your String values of actual column names
            db.update(CONTACTS_TABLE_NAME, cv,CONTACTS_COLUMN_VIDEO_ID + " = '" + videoId + "'", null);
            return false;
        }
        ContentValues values = new ContentValues();

        values.put(CONTACTS_COLUMN_VIDEO_ID, String.valueOf(videoId.trim()));
        values.put(CONTACTS_COLUMN_VIDEO_TIME, String.valueOf(vedioTime));
        values.put(CONTACTS_COLUMN_VIDEO_SEEK_TIME, String.valueOf(vedioSeekTime));

        db.insert(CONTACTS_TABLE_NAME, null, values);

        db.close();
        return true;

    }
    public int getVedioStatusIfAdded(String id) {
        String countQuery = "SELECT  * FROM " + CONTACTS_TABLE_NAME+ " WHERE " + CONTACTS_COLUMN_VIDEO_ID + " = '" + id + "'";;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public String fetchSeekTime(String args1) {
        SQLiteDatabase db = this.getWritableDatabase();
        String time = null;
        String usernam = args1.trim();
        String query = "SELECT * FROM " + CONTACTS_TABLE_NAME + " WHERE " + CONTACTS_COLUMN_VIDEO_ID + " = '" + usernam + "'";
        Log.d("VT", "Query " + query);
        Cursor cursor = db.rawQuery(query, null);
        Log.d("VT", "value of cursor 1 " + cursor.getCount());
        if (cursor.equals(0)) {
            i = 0;
        } else {

            if (cursor.moveToFirst()) {
                do {

                    time = cursor.getString(cursor.getColumnIndex(CONTACTS_COLUMN_VIDEO_SEEK_TIME));
                    i++;
                } while (cursor.moveToNext());

                return time;
            }

        }

        db.close();
        return null;
    }

    public String fetchTotalDuration(String args1) {
        SQLiteDatabase db = this.getWritableDatabase();
        String time = null;
        String usernam = args1.trim();
        String query = "SELECT * FROM " + CONTACTS_TABLE_NAME + " WHERE " + CONTACTS_COLUMN_VIDEO_ID + " = '" + usernam + "'";
        Log.d("VT", "Query " + query);
        Cursor cursor = db.rawQuery(query, null);
        Log.d("VT", "value of cursor 1 " + cursor.getCount());
        if (cursor.equals(0)) {
            i = 0;
        } else {

            if (cursor.moveToFirst()) {
                do {

                    time = cursor.getString(cursor.getColumnIndex(CONTACTS_COLUMN_VIDEO_TIME));
                    i++;
                } while (cursor.moveToNext());

                return time;
            }

        }

        db.close();
        return null;
    }
}
