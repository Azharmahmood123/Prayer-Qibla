package noman.quran.dbmanager;/*
   Created by Noman and  Email = nomanarif.cdz@gmail.com on 6/7/2016.
*/


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import noman.quran.dbconnection.DataBaseHelper;
import noman.quran.model.JuzModel;

public class JuzDataManager {
    private DataBaseHelper databaseHelper;
    private Context mContext;

    public JuzDataManager(Context context) {
        this.databaseHelper = new DataBaseHelper(context);
        this.mContext = context;
    }

    public List<JuzModel> getJuzList(int surahNumber) {
        String sqlStatement = "SELECT * from tbl_QuranComplete where surat_ID=" + surahNumber;
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        if (db == null)
            return null;
        List<JuzModel> categoryModelList = new ArrayList<JuzModel>();
        Cursor c = db.rawQuery(sqlStatement, null);
        JuzModel categoryModel = null;
        if (c.moveToFirst()) {
            do {
                categoryModel = new JuzModel();
                categoryModel.setAyatId(c.getInt(c.getColumnIndex("Ayat_ID")));
                categoryModel.setSuratId(c.getInt(c.getColumnIndex("surat_ID")));
                categoryModel.setParaId(c.getInt(c.getColumnIndex("para_ID")));
                categoryModel.setAyatNo(c.getInt(c.getColumnIndex("only_aayat_no")));

                categoryModelList.add(categoryModel);

            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return categoryModelList;
    }

    public JuzModel getJuzNumber(int surahNumber,int ayaNumber) {
        String sqlStatement = "SELECT * from tbl_QuranComplete where surat_ID=" + surahNumber +" and only_aayat_no="+ayaNumber;
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        if (db == null)
            return null;

        Cursor c = db.rawQuery(sqlStatement, null);
        JuzModel categoryModel = null;
        if (c.moveToFirst()) {
            do {
                categoryModel = new JuzModel();
                categoryModel.setAyatId(c.getInt(c.getColumnIndex("Ayat_ID")));
                categoryModel.setSuratId(c.getInt(c.getColumnIndex("surat_ID")));
                categoryModel.setParaId(c.getInt(c.getColumnIndex("para_ID")));
                categoryModel.setAyatNo(c.getInt(c.getColumnIndex("only_aayat_no")));

            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return categoryModel;
    }
}

