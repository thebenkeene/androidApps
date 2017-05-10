package keene.drugdatabase;

/**
 * Created by BigBen on 5/6/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;


public class playerAdapter {
    public static final String ROWID = "_id";
    public static final String FIRSTNAME = "FIRSTNAME";
    public static final String LASTNAME = "LASTNAME";
    public static final String SCORE = "SCORE";

    private static final String TAG = "PlayerDbAdaptor";
    private databaseHelper mDbHelper;
    private SQLiteDatabase myDatabase;
    //private static final String DATABASE_NAME = "StudentDB";
    private static final String SQLITE_TABLE = "PlayerTable";

    private static final int DATABASE_VERSION = 1;
    private final Context context;


    public playerAdapter(Context context, SQLiteDatabase myDatabase,databaseHelper mDbHelper) {
        this.context = context;
        this.myDatabase = myDatabase;
        this.mDbHelper =  mDbHelper;
    }

    public void close() {
        if ( mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public void createPlayer(String firstname, String lastname,
                              int score) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(FIRSTNAME, firstname);
        initialValues.put(LASTNAME, lastname);
        initialValues.put(SCORE, score);
        myDatabase.insert(SQLITE_TABLE, null, initialValues);
    }


    //this will delete (remove) one record
    public boolean deleteOnePlayer(String name) {

        int doneDelete = 0;
        doneDelete = myDatabase.delete(SQLITE_TABLE, "FIRSTNAME=?", new String[] {name});
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;

    }

    //this will delete (remove) entire data table
    public boolean deleteAllPlayers() {

        int doneDelete = 0;
        doneDelete = myDatabase.delete(SQLITE_TABLE, null , null);
        Log.w(TAG, Integer.toString(doneDelete));
        return doneDelete > 0;

    }

    public Cursor fetchStudentsByName(String inputText) throws SQLException {
        Log.w(TAG, inputText);
        Cursor mCursor = null;
        if (inputText == null  ||  inputText.length () == 0)  {
            mCursor = myDatabase.query(SQLITE_TABLE, new String[] {ROWID,
                            FIRSTNAME, LASTNAME, SCORE},
                    null, null, null, null, null);

        }
        else {
            mCursor = myDatabase.query(true, SQLITE_TABLE, new String[] {ROWID,
                            FIRSTNAME, LASTNAME, SCORE},
                    FIRSTNAME + " like '%" + inputText + "%'", null,
                    null, null, null, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

//
//    public Cursor fetchStudentsByMajor(String inputText) throws SQLException {
//        Log.w(TAG, inputText);
//        Cursor mCursor = null;
//        if (inputText == null  ||  inputText.length () == 0)  {
//            mCursor = myDatabase.query(SQLITE_TABLE, new String[] {ROWID,
//                            FIRSTNAME, LASTNAME, SCORE},
//                    null, null, null, null, null);
//
//        }
//        else {
//            mCursor = myDatabase.query(true, SQLITE_TABLE, new String[] {ROWID,
//                            FIRSTNAME, LASTNAME, SCORE},
//                    SCORE + " like '%" + inputText + "%'", null,
//                    null, null, null, null);
//        }
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//        return mCursor;
//
//    }


//    public Cursor fetchStudentsGTGPA(double inputText) throws SQLException {
//        //Log.w(TAG, inputText);
//        Cursor mCursor = null;
//        if (inputText  < 0.0)  {
//            mCursor = myDatabase.query(SQLITE_TABLE, new String[] {ROWID,
//                            NAME, MAJOR, GPA},
//                    null, null, null, null, null);
//
//        }
//        else {
//            mCursor = myDatabase.query(true, SQLITE_TABLE, new String[] {ROWID,
//                            NAME, MAJOR, GPA},
//                    GPA + " >= " + inputText, null,
//                    null, null, null, null);
//        }
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//        return mCursor;
//
//    }

//    public Cursor fetchStudentsLTGPA(double inputText) throws SQLException {
//        //Log.w(TAG, inputText);
//        Cursor mCursor = null;
//        if (inputText  < 0.0)  {
//            mCursor = myDatabase.query(SQLITE_TABLE, new String[] {ROWID,
//                            NAME, MAJOR, GPA},
//                    null, null, null, null, null);
//
//        }
//        else {
//            mCursor = myDatabase.query(true, SQLITE_TABLE, new String[] {ROWID,
//                            NAME, MAJOR, GPA},
//                    GPA + " <= " + inputText, null,
//                    null, null, null, null);
//        }
//        if (mCursor != null) {
//            mCursor.moveToFirst();
//        }
//        return mCursor;
//
//    }

    public Cursor fetchAllStudents() {

        Cursor mCursor = myDatabase.query(SQLITE_TABLE, new String[] {ROWID,
                        FIRSTNAME, LASTNAME, SCORE},
                null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

//    public void insertSomeStudents() {
//
//        createStudent("Harry","CS",3.0);
//        createStudent("Albus","CS",3.0);
//        createStudent("Ron","MATH",3.0);
//        createStudent("Hemoine","CS",3.0);
//        createStudent("Voldermort","Psychology",3.0);
//        createStudent("Severus","Psychology",1.0);
//    }


}
