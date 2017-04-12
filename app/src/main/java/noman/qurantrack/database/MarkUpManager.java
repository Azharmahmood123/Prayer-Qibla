package noman.qurantrack.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.quranreading.helper.DataBaseHelper;

import noman.quran.model.JuzModel;

public class MarkUpManager {
    private DataBaseHelper databaseHelper;
    private Context mContext;

    public MarkUpManager(Context context) {
        this.databaseHelper = new DataBaseHelper(context);
        this.mContext = context;
    }

    public int getSurahCount(int surahNumber) {
        int size = 0;

        String sqlStatement = "SELECT count(Ayat_ID) as total from tbl_QuranComplete where surat_ID= " + surahNumber + " and only_aayat_no != 0";
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        if (db == null)
            return 0;

        Cursor c = db.rawQuery(sqlStatement, null);

        if (c.moveToFirst()) {
            do {
                size = c.getInt((c.getColumnIndex("total")));
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return size;
    }

    public JuzModel getJuzNumber(int surahNumber, int ayaNumber) {
        String sqlStatement = "SELECT * from tbl_QuranComplete where surat_ID=" + surahNumber + " and only_aayat_no=" + ayaNumber;
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

    //For amarker position and aya per day read
    public int getQuery1(int surahNumber, int ayaNumber) {
        int countr = 0;

        String sqlStatement = "SELECT * from tbl_QuranComplete where surat_ID=" + surahNumber + " and only_aayat_no=" + ayaNumber;
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        if (db == null)
            return 0;

        Cursor c = db.rawQuery(sqlStatement, null);
        JuzModel categoryModel = null;
        if (c.moveToFirst()) {
            do {
                categoryModel = new JuzModel();
                categoryModel.setAyatId(c.getInt(c.getColumnIndex("Ayat_ID")));
             /*   categoryModel.setSuratId(c.getInt(c.getColumnIndex("surat_ID")));
                categoryModel.setParaId(c.getInt(c.getColumnIndex("para_ID")));
                categoryModel.setAyatNo(c.getInt(c.getColumnIndex("only_aayat_no")));

*/
                countr = categoryModel.getAyatId();


            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return countr;
    } //e.g 34 Ayat id

    public int getQuery2() {
        int countr = 0;

        String sqlStatement = "select Ayat_ID from tbl_QuranComplete where mark = 1";
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        if (db == null)
            return 0;

        Cursor c = db.rawQuery(sqlStatement, null);
        JuzModel categoryModel = null;
        if (c.moveToFirst()) {
            do {
                categoryModel = new JuzModel();
                categoryModel.setAyatId(c.getInt(c.getColumnIndex("Ayat_ID")));
                countr = categoryModel.getAyatId();


            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return countr;
    }//e.g 14 Ayat id

    public int getQuery3() {
        int countr = 0;
        String sqlStatement = "UPDATE tbl_QuranComplete set mark = 0";
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        if (db == null)
            return 0;

        Cursor c = db.rawQuery(sqlStatement, null);
        JuzModel categoryModel = null;
        if (c.moveToFirst()) {
            do {
             //   categoryModel = new JuzModel();
             //   categoryModel.setAyatId(c.getInt(c.getColumnIndex("Ayat_ID")));
              //  countr = categoryModel.getAyatId();
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return countr;
    }//update marker coulum to 0

    public int getQuery4(int surahNumber, int ayaNumber) {
        int countr = 0;

        String sqlStatement = "UPDATE tbl_QuranComplete set mark = 1 where surat_ID=" + surahNumber + " and only_aayat_no=" + ayaNumber;
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        if (db == null)
            return 0;

        Cursor c = db.rawQuery(sqlStatement, null);
        JuzModel categoryModel = null;
        if (c.moveToFirst()) {
            do {
              //  categoryModel = new JuzModel();
             //   categoryModel.setAyatId(c.getInt(c.getColumnIndex("Ayat_ID")));
              //  countr = categoryModel.getAyatId();
            } while (c.moveToNext());
            c.close();
        }
        db.close();
        return countr;
    }//update marker coulum to 1 to the selected value
}

