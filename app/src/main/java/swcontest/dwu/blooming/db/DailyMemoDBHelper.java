package swcontest.dwu.blooming.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DailyMemoDBHelper extends SQLiteOpenHelper {

    private final static String DB_NAME = "daily_memo_db";
    public final static String TABLE_NAME = "daily_memo_table";
    public final static String COL_ID = "_id";
    public final static String COL_DATE = "date";
    public final static String COL_TIME = "time";
    public final static String COL_CONTENT = "content";

    public DailyMemoDBHelper (Context context) { super(context, DB_NAME, null, 1);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( " + COL_ID + " integer primary key autoincrement,"
                + COL_DATE + " TEXT, " + COL_TIME + " TEXT, " + COL_CONTENT + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + TABLE_NAME);
        onCreate(db);
    }
}
