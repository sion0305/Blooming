package swcontest.dwu.blooming;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

import swcontest.dwu.blooming.adapter.DailyMemoCursorAdapter;
import swcontest.dwu.blooming.db.DailyMemoDBHelper;

public class DailyMemoListActivity extends AppCompatActivity {

    ListView list = null;
    DailyMemoDBHelper helper;
    Cursor cursor;

    DailyMemoCursorAdapter adapter;
    final int UPDATE_CODE = 100;
    boolean result = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_memo_list);

        list = (ListView) findViewById(R.id.daily_memo_list);
        helper = new DailyMemoDBHelper(this);
        adapter = new DailyMemoCursorAdapter(this, R.layout.dailymemo_item, null);
        list.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (result == false) { }

        else{
            SQLiteDatabase db = helper.getReadableDatabase();
            cursor = db.rawQuery("select * from " + helper.TABLE_NAME, null);

            adapter.changeCursor(cursor);

            helper.close();
        }
        result = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
    }
}