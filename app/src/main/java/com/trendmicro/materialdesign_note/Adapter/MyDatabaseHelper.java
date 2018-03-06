package com.trendmicro.materialdesign_note.Adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.widget.Toast;

import com.trendmicro.materialdesign_note.Utils.PhotoBitmapUtils;

import java.io.File;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "passwodSafe.db";
    public static final int VERSION = 1;

    public static final String CREATE_ADDRESS = "create table Messagebook ("
            + "id integer primary key autoincrement, "
            + "title text, "
            + "password text, "
            + "memoInfo text, "
            + "time text) " ;


    private Context mContext;

    public MyDatabaseHelper(Context context, String name,
							SQLiteDatabase.CursorFactory factory, int version) {
        super(context, getMyDatabaseName(context), factory, version);
        mContext = context;
    }
    private static String getMyDatabaseName(Context context){
        String dbPath= PhotoBitmapUtils.getPhoneRootPath(context);
        File dbp = new File(dbPath);
        if(!dbp.exists()){
            dbp.mkdirs();
        }
        String databasename = dbPath + "/database/"+DB_NAME;
        return databasename;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ADDRESS);
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
