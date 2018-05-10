package develop.admin.it.formular;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;


public class GlobalClass extends AppCompatActivity {

    public GlobalClass() {
        super();
    }

    public DatabaseHelper sql;

    public String dateDay(String typeDate) {
        Date today = new Date();
        DateFormat df = new SimpleDateFormat(typeDate);//yyyy-MM-dd HH:mm:ss
        df.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String day = df.format(today);
        return day;
    }

    public int dateInt(String typeDate) {
        Date today = new Date();
        DateFormat df = new SimpleDateFormat(typeDate);//yyyy-MM-dd HH:mm:ss
        df.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String day = df.format(today);
        return Integer.parseInt(day);
    }

    public String dateDayChange(String typeDate, int time) {
        Date today = new Date(System.currentTimeMillis() - time);
        DateFormat df = new SimpleDateFormat(typeDate);//yyyy-MM-dd HH:mm:ss
        df.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String day = df.format(today);
        return day;
    }

    public String converTimeMill(String typeDate, long time) {
        Date today = new Date(time);
        DateFormat df = new SimpleDateFormat(typeDate);//yyyy-MM-dd HH:mm:ss
        df.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String day = df.format(today);
        return day;
    }

    public String MillNoTimeZone(String typeDate, long time) {
        Date today = new Date(time);
        DateFormat df = new SimpleDateFormat(typeDate);//yyyy-MM-dd HH:mm:ss
        String day = df.format(today);
        return day;
    }

    public String dateDayNoTimeZone(String typeDate) {
        Date today = new Date();
        DateFormat df = new SimpleDateFormat(typeDate);//yyyy-MM-dd HH:mm:ss
        String day = df.format(today);
        return day;
    }

    public long converDayToMill(String typeDate, String toParse) {
        SimpleDateFormat formatter = new SimpleDateFormat(typeDate); // I assume d-M, you may refer to M-d for month-day instead.
        Date date = null; // You will need try/catch around this
        try {
            date = formatter.parse(toParse);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public long converDateToMilliseconds(String type, String formattedDate) {
        long milliseconds = Long.parseLong(null);
        try {
            Date date = new SimpleDateFormat(type, Locale.ENGLISH).parse(formattedDate);
            milliseconds = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return milliseconds;
    }

    public long converMilliseconds(String type,String date) {
        SimpleDateFormat formatter = new SimpleDateFormat(type); // "dd.MM.yyyy, HH:mm"
        formatter.setLenient(false);
        Date oldDate = null;
        long oldMillis = 0;
        try {
            oldDate = formatter.parse(date);
            oldMillis = oldDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return oldMillis;
    }

    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setCancelable(true);
        builder1.setTitle(title);
        builder1.setMessage(message);
        builder1.show();
    }

    public void showAlertDialogLoading(Context context, String title, String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setCancelable(true);
        builder1.setTitle(title);
        builder1.setMessage(message);
        builder1.setView(R.menu.menu_contact);
        builder1.show();
    }

    public void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public String getXMLFromUrl(String url) {
        BufferedReader br = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) (new URL(url)).openConnection();
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;
            final StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<String> format() {
        ArrayList<String> SmsFormat = new ArrayList<>();
        SmsFormat.add("de");
        SmsFormat.add("lo");
        SmsFormat.add("bacang");
        SmsFormat.add("xien");
        return SmsFormat;
    }

    public String uniStrip(String kitu) {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("a", "á,à,ả,ã,ạ,ă,ắ,ặ,ằ,ẳ,ẵ,â,ấ,ầ,ẩ,ẫ,ậ");
        aMap.put("d", "đ");
        aMap.put("e", "é,è,ẻ,ẽ,ẹ,ê,ế,ề,ể,ễ,ệ");
        aMap.put("i", "í,ì,ỉ,ĩ,ị");
        aMap.put("o", "ó,ò,ỏ,õ,ọ,ô,ố,ồ,ổ,ỗ,ộ,ơ,ớ,ờ,ở,ỡ,ợ");
        aMap.put("u", "ú,ù,ủ,ũ,ụ,ư,ứ,ừ,ử,ữ,ự");
        aMap.put("y", "ý,ỳ,ỷ,ỹ,ỵ");
        for (Map.Entry<String, String> entry : aMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String[] stripUnicode = value.split(",");
            for (int j = 0; j < stripUnicode.length; j++) {
                kitu = kitu.replace(stripUnicode[j], key);
            }
        }

        return kitu;
    }

    public ArrayList<String> limitNumber() {
        ArrayList<String> limitNumber = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (i < 10) {
                limitNumber.add("0" + String.valueOf(i));
            } else {
                limitNumber.add(String.valueOf(i));
            }

        }
        return limitNumber;
    }

    public ArrayList<String> limitMiniNumber() {
        ArrayList<String> limitMiniNumber = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            limitMiniNumber.add(String.valueOf(i));
        }
        return limitMiniNumber;
    }

    public ArrayList<String> limitNumberBaCang() {
        ArrayList<String> limitNumber = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            if (i < 10) {
                limitNumber.add("00" + String.valueOf(i));
            } else if (i < 100) {
                limitNumber.add("0" + String.valueOf(i));
            } else {
                limitNumber.add(String.valueOf(i));
            }

        }
        return limitNumber;
    }

    public String convertFormatDate(String dateMath) {
        // yyyy-MM-dd
        String[] daySlip = dateMath.split("-");
        String newDateMath;
        if (daySlip[2].replaceAll("(^\\s+|\\s+$)", "").trim().length() == 4) {
            newDateMath = daySlip[2] + "-" + daySlip[1] + "-" + daySlip[0];
        } else {
            newDateMath = dateMath.replaceAll("(^\\s+|\\s+$)", "").trim();
        }
        return newDateMath;
    }

    public String convertFormatDateDd(String dateMath) {
        // dd-MM-yyyy
        String[] daySlip = dateMath.split("-");
        String newDateMath;
        if (daySlip[0].length() == 4) {
            newDateMath = daySlip[2] + "-" + daySlip[1] + "-" + daySlip[0];
        } else {
            newDateMath = dateMath;
        }
        return newDateMath;
    }

    public static String removeAccent(String s) {
        s = s.replace("bỏ", "bor");
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }

    public String repkDau(String kitu) {
        String converKitu = kitu.replace("xien", "xi").replace("xie", "xi").
                replace("ghep", "gep").
                replace("gepbc", "gepbc ").
                replace("de", "JAVASTR de").replace("lo", "JAVASTR lo").
                replace("ld", "JAVASTR lD").replace("dd", "JAVASTR DD").
                replace("3so", "3c").replace("3 so", "3c").replace("bs", "3c").
                replace("baso", "3c").replace("ba so", "3c").replace("3s", "3c").
                replace("3cang", "3c").replace("3 cang", "3c").
                replace("db", "JAVASTR db").
                replace("3c", "JAVASTR 3c")
                .replace("lx ", "JAVASTR si ").
                        replace("lx2", "JAVASTR si2").replace("lx3", "JAVASTR si3").
                        replace("lx4", "JAVASTR si4").replace("xi ", "JAVASTR si ").
                        replace("xi2", "JAVASTR si2").replace("xi3", "JAVASTR si3").
                        replace("xi4", "JAVASTR si4").replace("xi ", "JAVASTR si ").
                        replace("xien quay", "JAVASTR sq").replace("xienquay", "JAVASTR sq").
                        replace("xq", "JAVASTR sq").
                        replace("mc", "x").
                        replace("ms", "x").
                        replace("moi con", "x").
                        replace("moicon", "x").
                        replace("moi cap", "x").
                        replace("moicap", "x").
                        replace("×", "x").
                        replace("ba cang", "3c").
                        replace("bacang", "3c").
                        replace("to to", "toto").replace("to nho", "tolho").
                        replace("nho to", "lhoto").replace("nho nho", "lholho").
                        replace("le chan", "lechal").replace("le le", "lele").
                        replace("chan le", "challe").replace("chan chan", "chalchal").
                        replace("tonho", "tolho").replace("nhoto", "lhoto").
                        replace("nhonho", "lholho").replace("lechan", "lechal").
                        replace("chanle", "challe").replace("chanchan", "chalchal").
                        replace("tong tren muoi", "tongtrelmuoi").replace("tong tren 10", "tongtrel10").
                        replace("tong trel10", "tongtrel10").replace("tong duoi10", "tongduoi10").
                        replace("tong duoi muoi", "tongduoimuoi").replace("tong duoi 10", "tongduoi10").
                        replace("chia 3 du 0", "chia3du0").
                        replace("chia 3 du 1", "chia3du1").
                        replace("chia 3 du 2", "chia3du2").
                        replace("on", "ON").replace("t0n", "TON").
                        replace("sat kep","satcep").replace("satkep","satcep").
                        replace("kepbang", "cepbalg").replace("kep bang", "cepbalg").replace("kep lech", "ceplech").
                        replace("dan", "dal").
                        replace("an", "al").
                        replace("nghin", "n").
                        replace("ngin", "n").
                        replace("ng", "n").
                        replace(" n", "n").
                        replace("diem", "d").
                        replace("/", " ").
                        replace("\\", " ").
                        replace(":", " ").
                        replace(";", " ").
                        replace("'", " ").
                        replace("\"", " ").
                        replace("-", " ").
                        replace("_", " ").
                        replace("+", " ").
                        replace(",", " ").
                        replace(".", " ").
                        replace("da", "DA").replace("di", "DI").replace("du", "DU").
                        replace("n/1c", "N1c").replace("n\\1c", "N1c").replace("n1c", "N1c").
                        replace("d/1c", "D1c").replace("d\\1c", "D1c").replace("d1c", "D1c");
        return converKitu;
    }

    public String repDauBang(String kitu) {
        String converKitu = kitu.replace("xien", "xi").replace("xie", "xi").
                replace("ghep", "gep").
                replace("gepbc", "gepbc ").
                replace("de", "JAVASTR de").replace("lo", "JAVASTR lo").
                replace("ld", "JAVASTR lD").replace("dd", "JAVASTR DD").
                replace("3so", "3c").replace("3 so", "3c").replace("bs", "3c").
                replace("baso", "3c").replace("ba so", "3c").replace("3s", "3c").
                replace("3cang", "3c").replace("3 cang", "3c").
                replace("3c", "JAVASTR 3c").replace("lx", "JAVASTR si").
                replace("lx2", "JAVASTR si2").replace("lx3", "JAVASTR si3").
                replace("lx4", "JAVASTR si4").replace("xi ", "JAVASTR si ").
                replace("xi2", "JAVASTR si2").replace("xi3", "JAVASTR si3").
                replace("xi4", "JAVASTR si4").
                replace("xien quay", "JAVASTR sq").replace("xienquay", "JAVASTR sq").
                replace("xq", "JAVASTR sq").
                replace("mc", "x").
                replace("=", "x").
                replace("ms", "x").
                replace("×", "x").
                replace("moi con", "x").
                replace("moicon", "x").
                replace("moi cap", "x").
                replace("moicap", "x").
                replace("ba cang", "3c").
                replace("bacang", "3c").
                replace("to to", "toto").replace("to nho", "tolho").
                replace("nho to", "lhoto").replace("nho nho", "lholho").
                replace("le chan", "lechal").replace("le le", "lele").
                replace("chan le", "challe").replace("chan chan", "chalchal").
                replace("tonho", "tolho").replace("nhoto", "lhoto").
                replace("nhonho", "lholho").replace("lechan", "lechal").
                replace("chanle", "challe").replace("chanchan", "chalchal").
                replace("tong tren 10", "TONGTREL10").
                replace("tong tren10", "TONGTREL10").
                replace("tong duoi10", "tongduoi10").
                replace("tong duoi 10", "tongduoi10").
                replace("chia 3 du 0", "chia3du0").
                replace("chia 3 du 1", "chia3du1").
                replace("chia 3 du 2", "chia3du2").
                replace("on", "ON").replace("t0n", "TON").
                replace("kepbang", "kepbalg").replace("kep bang", "kepbalg").replace("kep lech", "keplech").
                replace("dan", "dal").
                replace("an", "al").
                replace("nghin", "n").
                replace("ngin", "n").
                replace("ng", "n").
                replace(" n", "n").
                replace("diem", "d").
                replace("/", " ").
                replace("\\", " ").
                replace(":", " ").
                replace(";", " ").
                replace("'", " ").
                replace("\"", " ").
                replace("-", " ").
                replace("_", " ").
                replace("+", " ").
                replace(",", " ").
                replace(".", " ").
                replace("da", "DA").replace("di", "DI").replace("du", "DU").
                replace("n/1c", "N1c").replace("n\\1c", "N1c").replace("n1c", "N1c").
                replace("d/1c", "D1c").replace("d\\1c", "D1c").replace("d1c", "D1c");
        return converKitu;
    }

    public String converStringSms(String messageSms) {
        String message = messageSms.replace("DA", "da").replace("DI", "di").
                replace("DU", "du").
                replace("gep", "JAVASTR gep").
                replace("dau", "JAVASTR dau ").replace("dit", "JAVASTR dit ")
                .replace("toto", "JAVASTR toto").replace("tolho", "JAVASTR tolho")
                .replace("lhoto", "JAVASTR lhoto").replace("lholho", "JAVASTR lholho")
                .replace("lechal", "JAVASTR lechal").replace("lele", "JAVASTR lele")
                .replace("challe", "JAVASTR challe").replace("chalchal", "JAVASTR chalchal")
                .replace("ON", "on").replace("TON", "ton")
                .replace("tongtren10", "JAVASTR tongtren10")
                .replace("tongduoi10", "JAVASTR tongduoi10")
                .replace("tong", "JAVASTR tong ")
                .replace("he", "JAVASTR he ").replace("bo", "JAVASTR bo ")
                .replace("dal", "JAVASTR dal ").replace("keplech", "JAVASTR keplech").replace("kepbalg", "JAVASTR kepbalg")
                .replace("ceplech", "JAVASTR ceplech").replace("cepbalg", "JAVASTR cepbalg")
                .replace("chia 3 du 0", "JAVASTR chia3du0")
                .replace("chia 3 du 1", "JAVASTR chia3du1")
                .replace("chia 3 du 2", "JAVASTR chia3du2");
        return message;
    }

    public ArrayList<String> kieuboso() {
        ArrayList<String> kieubobso = new ArrayList<>();
        kieubobso.add("dau");
        kieubobso.add("dit");
        kieubobso.add("tong");
        kieubobso.add("bo");
        kieubobso.add("he");
        kieubobso.add("dal");
        kieubobso.add("cham");
        kieubobso.add("day");
        return kieubobso;
    }

    public ArrayList<String> kieubosodan() {
        ArrayList<String> kieubobso = new ArrayList<>();
        kieubobso.add("toto");
        kieubobso.add("tolho");
        kieubobso.add("lhoto");
        kieubobso.add("lholho");
        kieubobso.add("lechal");
        kieubobso.add("lele");
        kieubobso.add("challe");
        kieubobso.add("chalchal");
        kieubobso.add("kepbalg");
        kieubobso.add("keplech");
        kieubobso.add("satkep");
        kieubobso.add("cepbalg");
        kieubobso.add("ceplech");
        kieubobso.add("satcep");
        kieubobso.add("tongtrel10");
        kieubobso.add("tongduoi10");
        kieubobso.add("chia3du0");
        kieubobso.add("chia3du1");
        kieubobso.add("chia3du2");
        return kieubobso;
    }

    public ArrayList<String> tachchuoi(String[] mangTach) {
        ArrayList<String> tachChuoi = new ArrayList<>();
        for (int jDe = 0; jDe < mangTach.length; jDe++) {
            if (!mangTach[jDe].equals("")) {
                if (mangTach[jDe].indexOf("x") > -1) {
                    String[] fixError2 = mangTach[jDe].replaceAll("(^\\s+|\\s+$)", "").split("x");
                    for (int r = 0; r < fixError2.length; r++) {
                        if (r + 1 < fixError2.length) {
                            int valCd = r + 1;
                            String[] ChuoiDacBiet = fixError2[valCd].replaceAll("(^\\s+|\\s+$)", "").split(" ");
                            String[] ChuoiSauX = fixError2[r].replaceAll("(^\\s+|\\s+$)", "").split(" ");
                            if (r > 0) {
                                String chuoiGop = "";
                                for (int cdsx = 1; cdsx < ChuoiSauX.length; cdsx++) {
                                    chuoiGop += ChuoiSauX[cdsx] + " ";
                                }
                                tachChuoi.add(chuoiGop.replaceAll("(^\\s+|\\s+$)", "") + "x" + ChuoiDacBiet[0]);
                            } else {
                                tachChuoi.add(fixError2[r].replaceAll("(^\\s+|\\s+$)", "") + "x" + ChuoiDacBiet[0]);
                            }
                        } else {
                            String[] ChuoiDacBiet = fixError2[r].replaceAll("(^\\s+|\\s+$)", "").split(" ");
                            String chuoiDeCuoi = "";
                            if (r > 0) {
                                for (int dbz = 1; dbz < ChuoiDacBiet.length; dbz++) {
                                    chuoiDeCuoi += ChuoiDacBiet[dbz] + " ";
                                }
                            } else {
                                for (int dbz = 0; dbz < ChuoiDacBiet.length; dbz++) {
                                    chuoiDeCuoi += ChuoiDacBiet[dbz] + " ";
                                }
                            }
                            tachChuoi.add(chuoiDeCuoi);
                        }
                    }
                } else {
                    tachChuoi.add(mangTach[jDe]);
                }
            }
        }
        ArrayList<String> tachChuoiChuan = new ArrayList<>();
        for (int iy = 0; iy < tachChuoi.size(); iy++) {
            if (!tachChuoi.get(iy).equals("")) {
                if (tachChuoi.get(iy).indexOf("=") > -1) {
                    String[] tachChuoiDauB = tachChuoi.get(iy).replaceAll("(^\\s+|\\s+$)", "").split("=");
                    for (int r2 = 0; r2 < tachChuoiDauB.length; r2++) {
                        if (r2 + 1 < tachChuoiDauB.length) {
                            int tc2 = r2 + 1;
                            String[] ChuoiDacBiet = tachChuoiDauB[tc2].replaceAll("(^\\s+|\\s+$)", "").split(" ");
                            String[] ChuoiSauX = tachChuoiDauB[r2].replaceAll("(^\\s+|\\s+$)", "").split(" ");
                            if (r2 > 0) {
                                String chuoiGop = "";
                                for (int cdsx = 1; cdsx < ChuoiSauX.length; cdsx++) {
                                    chuoiGop += ChuoiSauX[cdsx] + " ";
                                }
                                tachChuoiChuan.add(chuoiGop.replaceAll("(^\\s+|\\s+$)", "") + "=" + ChuoiDacBiet[0]);
                            } else {
                                tachChuoiChuan.add(tachChuoiDauB[r2].replaceAll("(^\\s+|\\s+$)", "") + "=" + ChuoiDacBiet[0]);
                            }
                        } else {
                            String[] ChuoiDacBiet = tachChuoiDauB[r2].replaceAll("(^\\s+|\\s+$)", "").split(" ");
                            String chuoiCuoi = "";
                            if (r2 > 0) {
                                for (int dbz = 1; dbz < ChuoiDacBiet.length; dbz++) {
                                    chuoiCuoi += ChuoiDacBiet[dbz] + " ";
                                }
                            } else {
                                for (int dbz = 0; dbz < ChuoiDacBiet.length; dbz++) {
                                    chuoiCuoi += ChuoiDacBiet[dbz] + " ";
                                }
                            }
                            tachChuoiChuan.add(chuoiCuoi);
                        }
                    }
                } else {
                    tachChuoiChuan.add(tachChuoi.get(iy));
                }
            }
        }

        return tachChuoiChuan;
    }

    public String tachChuoiXien(String chuoiXien, ArrayList<String> limitNumberBaCang) {
        String[] mangValXien2 = chuoiXien.replaceAll("(^\\s+|\\s+$)", "").split(" ");
        String chuoiTachSoXien = "";
        for (int valx = 0; valx < mangValXien2.length; valx++) {
            if (limitNumberBaCang.contains(mangValXien2[valx])) {
                String valx1 = mangValXien2[valx].substring(0, 1);
                String valx2 = mangValXien2[valx].substring(2, 3);
                if (valx1.equals(valx2)) {
                    chuoiTachSoXien += mangValXien2[valx].substring(0, 2) + " " + mangValXien2[valx].substring(1, 3) + " ";
                } else {
                    chuoiTachSoXien += mangValXien2[valx] + " ";
                }
            } else {
                chuoiTachSoXien += mangValXien2[valx] + " ";
            }
        }
        return chuoiTachSoXien;
    }

    public Integer soLanXuatHienInArr(String chuoi) {
        String[] chuoiArr = chuoi.replace(".", ",").split(",");
        Integer result = chuoiArr.length;
        return result;
    }

    public ArrayList<String> ghepab(String chuoighep) {
        ArrayList<String> ghepab = new ArrayList<>();
        String danhsachSo = "";
        if (chuoighep.indexOf("voi") > -1) {
            String[] mangchuoighep = chuoighep.split("voi");
            if (mangchuoighep.length == 2) {
                mangchuoighep[0] = mangchuoighep[0].trim().replace("gepab", "");
                mangchuoighep[1] = mangchuoighep[1].trim();
                String fitterString = mangchuoighep[0].replaceAll("[0-9]", "").replace(" ", "");
                String fitterNumber = mangchuoighep[0].replaceAll("(^\\s+|\\s+$)", "").replace(" ", "");
                String[] checkmangchuoighep = mangchuoighep[0].trim().split(" ");
                chuoighep = "";
                if (fitterString.length() > 0 || checkmangchuoighep.length > 1 || fitterNumber.length() == 0) {
                    chuoighep += " <font color=\"RED\">gepab" + mangchuoighep[0] + "</font>";
                } else {
                    chuoighep += "gepab" + mangchuoighep[0];
                }
                String fitterString2 = mangchuoighep[1].replaceAll("[0-9]", "").replace(" ", "");
                String fitterNumber2 = mangchuoighep[1].replaceAll("(^\\s+|\\s+$)", "").replace(" ", "");
                String[] checkmangchuoighep2 = mangchuoighep[1].trim().split(" ");
                if (fitterString2.length() > 0 || checkmangchuoighep2.length > 1 || fitterNumber2.length() == 0) {
                    chuoighep += " <font color=\"RED\">voi " + mangchuoighep[1] + "</font>";
                } else {
                    chuoighep += " voi " + mangchuoighep[1];
                }
                if (fitterString.length() > 0 || checkmangchuoighep.length > 1
                        || fitterString2.length() > 0 || checkmangchuoighep2.length > 1) {
                    ghepab.add(chuoighep);
                    ghepab.add(danhsachSo);
                    return ghepab;
                } else {
                    String mangchuoighepRep1 = mangchuoighep[0].replace(" ", "");
                    String mangchuoighepRep2 = mangchuoighep[1].replace(" ", "");
                    for (int a = 0; a < mangchuoighepRep1.trim().length(); a++) {
                        String ghepa = mangchuoighepRep1.substring(a, a + 1);
                        for (int b = 0; b < mangchuoighepRep2.trim().length(); b++) {
                            String ghepb = mangchuoighepRep2.substring(b, b + 1);
                            String ghepAllAB = ghepa + ghepb;
                            String ghepAllBA = ghepb + ghepa;
                            if (ghepAllAB.length() != 2 || ghepAllBA.length() != 2) {
                                chuoighep = "<font color=\"RED\">" + chuoighep + "</font>";
                                ghepab.add(chuoighep);
                                ghepab.add(danhsachSo);
                                return ghepab;
                            } else {
                                if (danhsachSo.indexOf(ghepAllAB) == -1) {
                                    danhsachSo += ghepAllAB + ",";
                                }
                                if (danhsachSo.indexOf(ghepAllBA) == -1) {
                                    danhsachSo += ghepAllBA + ",";
                                }
                            }
                        }
                    }
                }
            } else {
                chuoighep = "<font color=\"RED\">" + chuoighep + " </font>";
            }
        } else {
            chuoighep = "<font color=\"RED\">" + chuoighep + " </font>";
        }
        ghepab.add(chuoighep);
        ghepab.add(danhsachSo);
        return ghepab;
    }

    public ArrayList<String> ghepxien(String xienChuoi) {
        ArrayList<String> ghepxien = new ArrayList<>();
        String res = "";
        String resXien2 = "";
        String resXien3 = "";
        String resXien4 = "";
        String[] chuoiXienMang = xienChuoi.split(" ");
        if (chuoiXienMang[0].indexOf("gep") > -1 || chuoiXienMang[0].indexOf("ghep") > -1) {
            String getTypeXien = chuoiXienMang[0].replace("gep", "").replace("ghep", "");
            String checkXienStr = getTypeXien.replaceAll("[0-9]", "").replace(" ", "");
            String checkXienNum = getTypeXien.replaceAll("(^\\s+|\\s+$)", "").replace(" ", "");
            if (checkXienNum.indexOf("0") == -1 && checkXienNum.indexOf("1") == -1 && checkXienNum.indexOf("5") == -1 &&
                    checkXienNum.indexOf("6") == -1 && checkXienNum.indexOf("7") == -1 && checkXienNum.indexOf("8") == -1 &&
                    checkXienNum.indexOf("9") == -1) {
                if (checkXienStr.length() == 0 && checkXienNum.length() >= 1 && checkXienNum.length() <= 3) {
                    if (chuoiXienMang.length >  2) {
                        if (checkXienNum.indexOf("4") > -1 && chuoiXienMang.length < 6) {
                            res += "<font color=\"RED\">" + xienChuoi + "</font>";
                        } else if(checkXienNum.indexOf("3") > -1 && chuoiXienMang.length < 5) {
                            res += "<font color=\"RED\">" + xienChuoi + "</font>";
                        } else if(checkXienNum.indexOf("2") > -1 && chuoiXienMang.length < 4) {
                            res += "<font color=\"RED\">" + xienChuoi + "</font>";
                        } else {
                            res += "ghep" + checkXienNum + " ";
                            for (int cx = 1; cx < chuoiXienMang.length; cx++) {
                                if (chuoiXienMang[cx].length() == 2 &&
                                        chuoiXienMang[cx].replaceAll("[0-9]", "").replace(" ", "").length() == 0) {
                                    if (res.indexOf(chuoiXienMang[cx]) > -1) {
                                        res += "<font color=\"RED\">" + chuoiXienMang[cx] + "</font> ";
                                    } else {
                                        res += chuoiXienMang[cx] + " ";
                                    }
                                } else {
                                    res += "<font color=\"RED\">" + chuoiXienMang[cx] + "</font> ";
                                }
                            }
                            if (res.indexOf("</font>") == -1) {
                                if (checkXienNum.indexOf("2") > -1) {
                                    resXien2 = resXien2(chuoiXienMang);
                                }
                                if (checkXienNum.indexOf("3") > -1) {
                                    resXien3 = resXien3(chuoiXienMang);
                                }
                                if (checkXienNum.indexOf("4") > -1) {
                                    resXien4 = resXien4(chuoiXienMang);
                                }
                            }
                        }

                    } else {
                        res += "<font color=\"RED\">" + xienChuoi + "</font>";
                    }
                } else {
                    res += "<font color=\"RED\">" + xienChuoi + "</font>";
                }
            } else {
                res += "<font color=\"RED\">" + xienChuoi + "</font>";
            }
        } else {
            res += "<font color=\"RED\">" + xienChuoi + "</font>";
        }
        ghepxien.add(res);
        ghepxien.add(resXien2);
        ghepxien.add(resXien3);
        ghepxien.add(resXien4);
        return ghepxien;
    }

    private String resXien2(String[] chuoiXienMang) {
        String resChuoiXien2 = "";
        int stt = 0;
        for (int xa = 1; xa < chuoiXienMang.length; xa++) {
            for (int xb = xa + 1; xb < chuoiXienMang.length; xb++) {
                stt++;
                resChuoiXien2 += chuoiXienMang[xa] + " " + chuoiXienMang[xb] + ",";
            }
        }
        return resChuoiXien2;
    }

    private String resXien3(String[] chuoiXienMang) {
        String resChuoiXien3 = "";
        int stt = 0;
        for (int xa = 1; xa < chuoiXienMang.length; xa++) {
            for (int xb = xa + 1; xb < chuoiXienMang.length; xb++) {
                for (int xc = xb + 1; xc < chuoiXienMang.length; xc++) {
                    stt++;
                    resChuoiXien3 += chuoiXienMang[xa] + " " + chuoiXienMang[xb] + " " + chuoiXienMang[xc] + ",";
                }
            }
        }
        return resChuoiXien3;
    }

    private String resXien4(String[] chuoiXienMang) {
        String resChuoiXien4 = "";
        int stt = 0;
        for (int xa = 1; xa < chuoiXienMang.length; xa++) {
            for (int xb = xa + 1; xb < chuoiXienMang.length; xb++) {
                for (int xc = xb + 1; xc < chuoiXienMang.length; xc++) {
                    for (int xd = xc + 1; xd < chuoiXienMang.length; xd++) {
                        stt++;
                        resChuoiXien4 += chuoiXienMang[xa] + " " + chuoiXienMang[xb] + " " + chuoiXienMang[xc] + " " + chuoiXienMang[xd] + ",";
                    }
                }
            }
        }
        return resChuoiXien4;
    }

}
