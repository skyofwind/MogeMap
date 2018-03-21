package com.example.dzj.theweather.main_menu;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.dzj.theweather.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.example.dzj.theweather.main.DataDeal.DB_PATH;

/**
 * Created by dzj on 2016/12/1.
 */

public class DB_code {
    private final int BUFFER_SIZE = 400000;
    public static final String DB_NAME2 = "city_code.db"; //保存的数据库文件名
    private SQLiteDatabase database;
    private Context context;
    public DB_code(Context context) {
        this.context = context;
    }
    public void openDatabase() {
        this.database = this.openDatabase(DB_PATH + "/" + DB_NAME2);
    }
    private SQLiteDatabase openDatabase(String dbfile) {
        try{
            if(!(new File(dbfile).exists())){
                InputStream is = this.context.getResources().openRawResource(R.raw.city_code); //欲导入的数据库
                FileOutputStream fos = new FileOutputStream(dbfile);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            }
            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile,null);
            Log.i("Database", "创建成功 ");
            return db;
        }catch (FileNotFoundException e){
            Log.i("Database", "File not found ");
            e.printStackTrace();
        }catch (IOException e1){
            Log.i("Database", "IO exception");
            e1.printStackTrace();
        }
        return null;
    }
    public Cursor Query(){
        Cursor cursor=this.database.query("city_id_list",null,null,null,null,null,null);
        return cursor;
    }

    public void closeDatabase() {
        this.database.close();
    }
}
