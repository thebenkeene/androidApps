package keene.drugdatabase;

/**
 * Created by BigBen on 5/6/17.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



public  class databaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PlayerDB";
    private static final String SQLITE_TABLE = "PlayerTable";
    private static final int DATABASE_VERSION = 1;
    private final Context context;

    private SQLiteDatabase db;

    public static final String ROWID = "_id";
    public static final String FIRSTNAME = "FIRSTNAME";
    public static final String LASTNAME = "LASTNAME";
    public static final String SCORE = "SCORE";

    private static final String TAG = "PlayerDbHelper";

    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
                    ROWID + " integer PRIMARY KEY autoincrement," +
                    FIRSTNAME + " VARCHAR," +
                    LASTNAME + " VARCHAR," +
                    SCORE + " INTEGER," +
                    " UNIQUE (" + FIRSTNAME +"));";

    databaseHelper(Context context, SQLiteDatabase db) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.db = db;
        Log.v("TAG", DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w(TAG, DATABASE_CREATE);
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE);
        onCreate(db);
    }
}