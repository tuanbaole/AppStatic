package develop.admin.it.formular;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IT on 4/23/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Formular.db";
    public static final String COL_NGAYTAO = "NGAYTAO";
    public static final String COL_NGAYSUA = "NGAYSUA";
    public static final String COL_NGAY = "NGAY";

    public static final String TABLE_NAME_1 = "kq_table";
    public static final String COL_11 = "ID";
    public static final String COL_12 = "LOTO";
    public static final String COL_13 = "GIAI";
    public static final String COL_14 = "VITRI";
    public static final String COL_15 = "NGAY";
    public static final String COL_16 = "VALUE";
    public static final String COL_17 = "VALUEDAU";

    public static final String TABLE_NAME_2 = "dongia_table";
    public static final String COL_21 = "ID";
    public static final String COL_22 = "SDT";
    public static final String COL_23 = "TEN";
    public static final String COL_24 = "HSDE";
    public static final String COL_25 = "HSLO";
    public static final String COL_26 = "HSBACANG";
    public static final String COL_27 = "HSXIENHAI";
    public static final String COL_28 = "HSXIENBA";
    public static final String COL_29 = "HSXIENBON";
    public static final String COL_210 = "COPHAN";
    public static final String COL_211 = "THDE";
    public static final String COL_212 = "THLO";
    public static final String COL_213 = "THBACANG";
    public static final String COL_214 = "THGIAINHAT";
    public static final String COL_215 = "THXIENHAI";
    public static final String COL_216 = "THXIENBA";
    public static final String COL_217 = "THXIENBON";
    public static final String COL_218 = "THTRUOTMUOI";
    public static final String COL_219 = "THTRUOTCHIN";
    public static final String COL_220 = "THTRUOTTAM";
    public static final String COL_221 = "THTRUOTBAY";
    public static final String COL_222 = "KIEUCOPHAN";
    public static final String COL_223 = "NGOIMOT";
    public static final String COL_224 = "NGOIHAI";

    public static final String TABLE_NAME_3 = "kihieu_table";
    public static final String COL_31 = "ID";
    public static final String COL_32 = "KIHIEU";
    public static final String COL_33 = "DAYSO";
    public static final String COL_34 = "KIEU";
    public static final String COL_35 = "VITRI";

    public static final String TABLE_NAME_4 = "time_table";
    public static final String COL_41 = "ID";
    public static final String COL_42 = "KHOALO";
    public static final String COL_43 = "KHOADE";
    public static final String COL_44 = "KHOAAPP";

    public static final String TABLE_NAME_5 = "solieu_table";
    public static final String COL_51 = "ID";
    public static final String COL_52 = "SMSID";
    public static final String COL_53 = "DONGIAID";
    public static final String COL_54 = "LOTO";
    public static final String COL_55 = "TRUNG";
    public static final String COL_56 = "TIENDANH";
    public static final String COL_57 = "TIENTHUONG";
    public static final String COL_58 = "TONG";
    public static final String COL_59 = "KIEU"; // INBOX,SEND
    public static final String COL_510 = "SDT";
    public static final String COL_511 = "KIHIEU"; // LO - DE - XIEN
    public static final String COL_512 = "TEN";
    public static final String COL_513 = "NGAY";
    public static final String COL_514 = "HESO";
    public static final String COL_515 = "THUONG";
    public static final String COL_516 = "MOICON";
    public static final String COL_517 = "TIENDANHSMS";
    public static final String COL_518 = "TRUNGSMS";

    public static final String TABLE_NAME_6 = "sms_ready_table";
    public static final String COL_61 = "ID";
    public static final String COL_62 = "SMSID";
    public static final String COL_63 = "DONGIAID";
    public static final String COL_64 = "CONTENT";
    public static final String COL_65 = "STATUS";
    public static final String COL_66 = "NGAY";

    public static final String TABLE_NAME_7 = "manager_money_table";
    public static final String COL_71 = "ID";
    public static final String COL_72 = "SDT";
    public static final String COL_73 = "TEN";
    public static final String COL_74 = "DANHINBOX";
    public static final String COL_75 = "HESOINBOX";
    public static final String COL_76 = "DANHSEND";
    public static final String COL_77 = "HESOSEND";
    public static final String COL_78 = "KIEU";
    public static final String COL_79 = "DONGIAID";

    public static final String TABLE_NAME_8 = "autosend_table";
    public static final String COL_81 = "ID";
    public static final String COL_82 = "SMSID";
    public static final String COL_83 = "DONGIAID";
    public static final String COL_84 = "LOTO";
    public static final String COL_86 = "TIENDANH";
    public static final String COL_89 = "KIEU"; // INBOX,SEND
    public static final String COL_810 = "SDT";
    public static final String COL_811 = "KIHIEU"; // LO - DE - XIEN
    public static final String COL_812 = "TEN";
    public static final String COL_813 = "NGAY";
    public static final String COL_816 = "MOICON";

    public static final String TABLE_NAME_9 = "setingsend_table";
    public static final String COL_91 = "ID";
    public static final String COL_92 = "MAXDE";
    public static final String COL_93 = "MAXLO";
    public static final String COL_94 = "MAXBACANG";
    public static final String COL_95 = "SDT";
    public static final String COL_96 = "MAXXIEN";

    public static final String TABLE_NAME_10 = "user_table";
    public static final String COL_101 = "ID";
    public static final String COL_102 = "PASSWORD";

    // hien thi thoi gian cai cac packed
    GlobalClass controller = new GlobalClass();
    String day = controller.dateDay("yyyy-MM-dd HH:mm:ss");

    public DatabaseHelper(Context context) {
//        super(context,DATABASE_NAME,factory,version);
        super(context, DATABASE_NAME, null, 12);
        SQLiteDatabase db = this.getWritableDatabase(); // su dung khi tao bang
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(
//                "create table " + TABLE_NAME_1 + " (" +
//                        "ID INTEGER PRIMARY KEY AUTOINCREMENT,LOTO VARCHAR,GIAI INTEGER," +
//                        "VITRI INTEGER,NGAY VARCHAR,NGAYTAO DATETIME,VALUE VARCHAR,VALUEDAU VARCHAR,NGAYSUA DATETIME) "
//        );
//
//        db.execSQL(
//                "create table " + TABLE_NAME_2 + " (" +
//                        "ID INTEGER PRIMARY KEY AUTOINCREMENT,SDT VARCHAR,TEN VARCHAR,HSDE DOUBLE,HSLO DOUBLE,HSBACANG DOUBLE," +
//                        "HSXIENHAI DOUBLE,HSXIENBA DOUBLE,HSXIENBON DOUBLE,COPHAN DOUBLE,THDE DOUBLE,THLO DOUBLE,THBACANG DOUBLE," +
//                        "THGIAINHAT DOUBLE,THXIENHAI DOUBLE,THXIENBA DOUBLE,THXIENBON DOUBLE,THTRUOTMUOI DOUBLE," +
//                        "THTRUOTCHIN DOUBLE,THTRUOTTAM DOUBLE,THTRUOTBAY DOUBLE,KIEUCOPHAN INTEGER,NGOIMOT VARCHAR,NGOIHAI VARCHAR," +
//                        "NGAYTAO DATETIME,NGAYSUA DATETIME) "
//        );
//
//        db.execSQL(
//                "create table " + TABLE_NAME_3 + " (" +
//                        "ID INTEGER PRIMARY KEY AUTOINCREMENT,KIHIEU VARCHAR,DAYSO TEXT,KIEU INTEGER,VITRI INTEGER,NGAYTAO DATETIME,NGAYSUA DATETIME) "
//        );
//
//        db.execSQL(
//                "create table " + TABLE_NAME_4 + " (" +
//                        "ID INTEGER PRIMARY KEY AUTOINCREMENT,KHOADE VARCHAR,KHOALO VARCHAR,KHOAAPP VARCHAR,NGAYTAO DATETIME,NGAYSUA DATETIME) "
//        );
//
//        db.execSQL(
//                "create table " + TABLE_NAME_5 + " (" +
//                        "ID INTEGER PRIMARY KEY AUTOINCREMENT,SMSID INTEGER,DONGIAID INTEGER,LOTO VARCHAR," +
//                        "TRUNG DOUBLE,TIENDANH DOUBLE,TIENTHUONG DOUBLE,TONG DOUBLE,KIEU VARCHAR,SDT VARCHAR,KIHIEU VARCHAR," +
//                        "TEN VARCHAR,NGAY DATETIME,HESO DOUBLE,THUONG DOUBLE,MOICON DOUBLE,TIENDANHSMS DOUBLE,TRUNGSMS DOUBLE, " +
//                        "NGAYTAO DATETIME,NGAYSUA DATETIME) "
//        );
//
//        db.execSQL(
//                "create table " + TABLE_NAME_6 + " (" +
//                        "ID INTEGER PRIMARY KEY AUTOINCREMENT,SMSID INTEGER,DONGIAID INTEGER,CONTENT VARCHAR," +
//                        "STATUS INTEGER,NGAY DATETIME,NGAYTAO DATETIME,NGAYSUA DATETIME) "
//        );
//
//        db.execSQL(
//                "create table " + TABLE_NAME_7 + " (" +
//                        "ID INTEGER PRIMARY KEY AUTOINCREMENT,SDT VARCHAR,TEN VARCHAR,DANHINBOX VARCHAR," +
//                        "HESOINBOX VARCHAR,DANHSEND VARCHAR,HESOSEND VARCHAR,KIEU VARCHAR,DONGIAID INTEGER,NGAY DATETIME" +
//                        ",NGAYTAO DATETIME,NGAYSUA DATETIME) "
//        );

        db.execSQL(
                "create table " + TABLE_NAME_9 + " (" +
                        "ID INTEGER PRIMARY KEY AUTOINCREMENT,SDT VARCHAR,MAXDE VARCHAR,MAXLO VARCHAR,MAXBACANG VARCHAR,MAXXIEN VARCHAR) "
        );

        db.execSQL(
                "create table " + TABLE_NAME_10 + " (" +
                        "ID INTEGER PRIMARY KEY AUTOINCREMENT,PASSWORD VARCHAR) "
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_1);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_2);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_3);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_4);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_5);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_6);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_7);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_9);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_10);
        onCreate(db);
    }

    public boolean insertManagerMoney(String sdt, String ten, double danhinbox, double hesoinbox, double danhsend
            , double hesosend, String kieu, int dongiaId, String ngay) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_72, sdt);
        contentValues.put(COL_73, ten);
        contentValues.put(COL_74, danhinbox);
        contentValues.put(COL_75, hesoinbox);
        contentValues.put(COL_76, danhsend);
        contentValues.put(COL_77, hesosend);
        contentValues.put(COL_78, kieu);
        contentValues.put(COL_79, dongiaId);
        contentValues.put(COL_NGAY, ngay);
        contentValues.put(COL_NGAYTAO, day);
        contentValues.put(COL_NGAYSUA, day);
        long result = db.insert(TABLE_NAME_7, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertCaiDatCanBang(String maxDe, String maxLo, String maxBaCang, String Sdt,String maxXien) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_92, maxDe);
        contentValues.put(COL_93, maxLo);
        contentValues.put(COL_94, maxBaCang);
        contentValues.put(COL_95, Sdt);
        contentValues.put(COL_96, maxXien);
        long result = db.insert(TABLE_NAME_9, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    public boolean insertSmsReady(int smsId, int dongiaId, String content, int status, String ngay) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_62, smsId);
        contentValues.put(COL_63, dongiaId);
        contentValues.put(COL_64, content);
        contentValues.put(COL_65, status);
        contentValues.put(COL_66, ngay);
        contentValues.put(COL_NGAYTAO, day);
        contentValues.put(COL_NGAYSUA, day);
        long result = db.insert(TABLE_NAME_6, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertDataDonGia(String sdt, String ten, double hsde, double hslo, double hsbacang, double hsxienhai, double hsxienba,
                                    double hsxienbon, double cophan, double thde, double thlo, double thbacang,
                                    double thgiainhat, double thxienhai, double thxienba, double thxienbon,
                                    double thtruot10, double thtruot9, double thtruot8, double thtruot7,
                                    int kieucophan, String ngoimot, String ngoihai) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_22, sdt);
        contentValues.put(COL_23, ten);
        contentValues.put(COL_24, hsde);
        contentValues.put(COL_25, hslo);
        contentValues.put(COL_26, hsbacang);
        contentValues.put(COL_27, hsxienhai);
        contentValues.put(COL_28, hsxienba);
        contentValues.put(COL_29, hsxienbon);
        contentValues.put(COL_210, cophan);
        contentValues.put(COL_211, thde);
        contentValues.put(COL_212, thlo);
        contentValues.put(COL_213, thbacang);
        contentValues.put(COL_214, thgiainhat);
        contentValues.put(COL_215, thxienhai);
        contentValues.put(COL_216, thxienba);
        contentValues.put(COL_217, thxienbon);
        contentValues.put(COL_218, thtruot10);
        contentValues.put(COL_219, thtruot9);
        contentValues.put(COL_220, thtruot8);
        contentValues.put(COL_221, thtruot7);
        contentValues.put(COL_222, kieucophan);
        contentValues.put(COL_223, ngoimot);
        contentValues.put(COL_224, ngoihai);
        contentValues.put(COL_NGAYTAO, day);
        contentValues.put(COL_NGAYSUA, day);
        long result = db.insert(TABLE_NAME_2, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }

    }

    public boolean insertDataKq(String loto, int giai, int vitri, String ngay, String value, String valueDau) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_12, loto);
        contentValues.put(COL_13, giai);
        contentValues.put(COL_14, vitri);
        contentValues.put(COL_15, ngay);
        contentValues.put(COL_16, value);
        contentValues.put(COL_17, valueDau);
        contentValues.put(COL_NGAYTAO, day);
        contentValues.put(COL_NGAYSUA, day);
        long result = db.insert(TABLE_NAME_1, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertDataKiHieu(String kihieu, String dayso, int kieu, int vitri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_32, kihieu);
        contentValues.put(COL_33, dayso);
        contentValues.put(COL_34, kieu);// kieu 3 danh cho ca lo lan de
        contentValues.put(COL_35, vitri);// kieu 3 danh cho ca lo lan de
        contentValues.put(COL_NGAYTAO, day);
        contentValues.put(COL_NGAYSUA, day);
        long result = db.insert(TABLE_NAME_3, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean insertDataTime(String timeLo, String timeDe, String timeApp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_42, timeLo);
        contentValues.put(COL_43, timeDe);
        contentValues.put(COL_44, timeApp);
        contentValues.put(COL_NGAYTAO, day);
        contentValues.put(COL_NGAYSUA, day);
        long result = db.insert(TABLE_NAME_4, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    public boolean updateTimeTable(String id, String timeDe, String timeLo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(COL_41, id);
        contentValue.put(COL_42, timeLo);
        contentValue.put(COL_43, timeDe);
        db.update(TABLE_NAME_4, contentValue, "ID = ? ", new String[]{id});
        return true;
    }

    public boolean updateTimeTableIMEI(String id, String imei) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(COL_41, id);
        contentValue.put(COL_44, imei);
        db.update(TABLE_NAME_4, contentValue, "ID = ? ", new String[]{id});
        return true;
    }

    public boolean insertDataSourceSoLieu(int smsId, int dongiaId, String loto, int trung, double tiendanh
            , double tienthuong, double tong, String kieu, String sdt, String kihieu, String ten, String ngay
            , double heso, double thuong, double tienMoiCon, double tiendanhsms, double trungsms) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_52, smsId);
        contentValues.put(COL_53, dongiaId);
        contentValues.put(COL_54, loto);
        contentValues.put(COL_55, trung);
        contentValues.put(COL_56, tiendanh);
        contentValues.put(COL_57, tienthuong);
        contentValues.put(COL_58, tong);
        contentValues.put(COL_59, kieu);
        contentValues.put(COL_510, sdt);
        contentValues.put(COL_511, kihieu);
        contentValues.put(COL_512, ten);
        contentValues.put(COL_513, ngay);
        contentValues.put(COL_514, heso);
        contentValues.put(COL_515, thuong);
        contentValues.put(COL_516, tienMoiCon); // tien danh 1 con
        contentValues.put(COL_517, tiendanhsms);
        contentValues.put(COL_518, trungsms);
        contentValues.put(COL_NGAYTAO, day);
        contentValues.put(COL_NGAYSUA, day);
        long result = db.insert(TABLE_NAME_5, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getAllDb(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(query, null);
        return res;
    }

    public boolean update(String id, String name, String surname, String marks) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(COL_11, id);
        contentValue.put(COL_12, name);
        contentValue.put(COL_13, surname);
        contentValue.put(COL_14, marks);
        db.update(TABLE_NAME_1, contentValue, "ID = ? ", new String[]{id});
        return true;
    }

    public boolean updateDonGia(String id, String sdt, String ten, double hsde, double hslo, double hsbacang
            , double hsxienhai, double hsxienba, double hsxienbon, double cophan, double thde, double thlo, double thbacang
            , double thgiainhat, double thxienhai, double thxienba, double thxienbon, double thuongtruotmuoi, double thuongtruotchin
            , double thuongtruottam, double thuongtruotbay, int kieucophan, String ngoimot, String ngoihai) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(COL_22, sdt);
        contentValue.put(COL_23, ten);
        contentValue.put(COL_24, hsde);
        contentValue.put(COL_25, hslo);
        contentValue.put(COL_26, hsbacang);
        contentValue.put(COL_27, hsxienhai);
        contentValue.put(COL_28, hsxienba);
        contentValue.put(COL_29, hsxienbon);
        contentValue.put(COL_210, cophan);
        contentValue.put(COL_211, thde);
        contentValue.put(COL_212, thlo);
        contentValue.put(COL_213, thbacang);
        contentValue.put(COL_214, thgiainhat);
        contentValue.put(COL_215, thxienhai);
        contentValue.put(COL_216, thxienba);
        contentValue.put(COL_217, thxienbon);
        contentValue.put(COL_218, thuongtruotmuoi);
        contentValue.put(COL_219, thuongtruotchin);
        contentValue.put(COL_220, thuongtruottam);
        contentValue.put(COL_221, thuongtruotbay);
        contentValue.put(COL_222, kieucophan);
        contentValue.put(COL_223, ngoimot);
        contentValue.put(COL_224, ngoihai);
        db.update(TABLE_NAME_2, contentValue, "ID = ? ", new String[]{id});
        return true;
    }

    public boolean updateUser(String id, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(COL_101, id);
        contentValue.put(COL_102, password);
        db.update(TABLE_NAME_10, contentValue, "ID = ? ", new String[]{id});
        return true;
    }

    public boolean insertUser(String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_102, password);
        long result = db.insert(TABLE_NAME_10, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Integer deleteData(String table_name, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(table_name, "ID = ? ", new String[]{id});
    }

    public Integer deleteSolieuSmsID(String table_name, String smsID) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(table_name, "SMSID = ? ", new String[]{smsID});
    }

    public Integer deleteSolieuSmsIDToDay(String table_name, String smsID, String getDays, String kihieu) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(table_name, "SMSID = ? AND NGAY = ? AND KIHIEU = ? ", new String[]{smsID, getDays, kihieu});
    }

    public Integer deleteSolieuIDToDay(String table_name, String ID,String getDays,String kihieu) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(table_name, "ID > ? AND NGAY = ? AND KIHIEU = ? ", new String[]{ID,getDays,kihieu});
    }

    public Integer deleteAll(String table_name, String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(table_name, "ID >= ? ", new String[]{id});
    }

    public Integer deleteCongNo(String table_name, String smsidArr) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(table_name, "SMSID IN (" + smsidArr + ") ", null);
    }

    public Integer deleteCongNoReady() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME_6, "STATUS=\"2\"", null);
    }

    public Integer deleteCongNoContent() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME_6, "CONTENT =\"\"", null);
    }

    public Integer deleteAllNgay(String table_name, String ngay) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(table_name, "NGAY = ? ", new String[]{ngay});
    }

    public Integer deleteAllNgayLast(String table_name, String ngay) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(table_name, "NGAY < ? ", new String[]{ngay});
    }

    public HashMap<String, ArrayList<String>> readkitu() {
        HashMap<String, ArrayList<String>> hashmap =
                new HashMap<String, ArrayList<String>>();
        Cursor kihieu = getAllDb("SELECT DAYSO,KIHIEU FROM kihieu_table");
        if (kihieu.getCount() > 0) {
            while (kihieu.moveToNext()) {
                ArrayList<String> arraylist = new ArrayList<String>();
                arraylist.add(kihieu.getString(kihieu.getColumnIndex("DAYSO")));
                hashmap.put(kihieu.getString(kihieu.getColumnIndex("KIHIEU")), arraylist);
            }
        }
        return hashmap;
    }

    public ArrayList<String> getArrayKeyRes(String day) {
        String newDay = controller.convertFormatDate(day);
        ArrayList<String> animals = new ArrayList<String>();
        Cursor kqsx = getAllDb("SELECT VALUE FROM kq_table WHERE NGAY=\"" + newDay + "\";");
        if (kqsx.getCount() > 0) {
            while (kqsx.moveToNext()) {
                animals.add(kqsx.getString(kqsx.getColumnIndex("VALUE")));
            }
        }
        return animals;
    }

    public ArrayList<String> getNotOkLo() {
        ArrayList<String> animals = new ArrayList<String>();
        for (int i = 0; i < 27; i++) {
            animals.add("100");
        }
        return animals;
    }

    public ArrayList<String> getLoDauGiai(String day) {
        String newDay = controller.convertFormatDate(day);
        ArrayList<String> animals = new ArrayList<String>();
        Cursor kqsx = getAllDb("SELECT VALUEDAU FROM kq_table WHERE NGAY=\"" + newDay + "\";");
        if (kqsx.getCount() > 0) {
            while (kqsx.moveToNext()) {
                animals.add(kqsx.getString(kqsx.getColumnIndex("VALUEDAU")));
            }
        }
        return animals;
    }

    public String compareDe(String day) {
        String newDay = controller.convertFormatDate(day);
        String query = "SELECT VALUE FROM kq_table WHERE NGAY=\"" + newDay + "\" AND GIAI=0";
        Cursor kqsx = getAllDb(query);
        String de = "100";
        if (kqsx.getCount() > 0) {
            kqsx.moveToFirst();
            de = kqsx.getString(kqsx.getColumnIndex("VALUE"));

        }
        return de;
    }

    public String compareDeDau(String day) {
        String newDay = controller.convertFormatDate(day);
        String query = "SELECT VALUEDAU FROM kq_table WHERE NGAY=\"" + newDay + "\" AND GIAI=0";
        Cursor kqsx = getAllDb(query);
        String dedau = "100";
        if (kqsx.getCount() > 0) {
            kqsx.moveToFirst();
            dedau = kqsx.getString(kqsx.getColumnIndex("VALUEDAU"));

        }
        return dedau;
    }

    public String compareBaCang(String day) {
        String newDay = controller.convertFormatDate(day);
        Cursor kqsx = getAllDb("SELECT LOTO FROM kq_table WHERE NGAY=\"" + newDay + "\" AND GIAI=0;");
        String bacang = "1000";
        if (kqsx.getCount() > 0) {
            kqsx.moveToFirst();
            String loto = kqsx.getString(kqsx.getColumnIndex("LOTO"));
            if (loto.length() == 3) {
                bacang = loto;
            } else if (loto.length() > 3) {
                bacang = loto.substring(loto.length() - 3);
            } else {
                // whatever is appropriate in this case
                bacang = "1000";
            }
        }
        return bacang;
    }

}

