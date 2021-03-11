package develop.admin.it.formular;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


public class GlobalClass extends AppCompatActivity {

    public GlobalClass() {
        super();
    }

    public DatabaseHelper sql;

    public String dateDay(String typeDate) {
        Date today = new Date();
        DateFormat df = new SimpleDateFormat( typeDate );//yyyy-MM-dd HH:mm:ss
        df.setTimeZone( TimeZone.getTimeZone( "Asia/Ho_Chi_Minh" ) );
        String day = df.format( today );
        return day;
    }

    public int dateInt(String typeDate) {
        Date today = new Date();
        DateFormat df = new SimpleDateFormat( typeDate );//yyyy-MM-dd HH:mm:ss
        df.setTimeZone( TimeZone.getTimeZone( "Asia/Ho_Chi_Minh" ) );
        String day = df.format( today );
        return Integer.parseInt( day );
    }

    public String dateDayChange(String typeDate, int time) {
        Date today = new Date( System.currentTimeMillis() - time ); // time 7 * 24 * 60 * 60 * 1000
        DateFormat df = new SimpleDateFormat( typeDate );//yyyy-MM-dd HH:mm:ss
        df.setTimeZone( TimeZone.getTimeZone( "Asia/Ho_Chi_Minh" ) );
        String day = df.format( today );
        return day;
    }

    public String converTimeMill(String typeDate, long time) {
        Date today = new Date( time );
        DateFormat df = new SimpleDateFormat( typeDate );//yyyy-MM-dd HH:mm:ss
        df.setTimeZone( TimeZone.getTimeZone( "Asia/Ho_Chi_Minh" ) );
        String day = df.format( today );
        return day;
    }

    public String MillNoTimeZone(String typeDate, long time) {
        Date today = new Date( time );
        DateFormat df = new SimpleDateFormat( typeDate );//yyyy-MM-dd HH:mm:ss
        String day = df.format( today );
        return day;
    }

    public String dateDayNoTimeZone(String typeDate) {
        Date today = new Date();
        DateFormat df = new SimpleDateFormat( typeDate );//yyyy-MM-dd HH:mm:ss
        String day = df.format( today );
        return day;
    }

    public long converDayToMill(String typeDate, String toParse) {
        SimpleDateFormat formatter = new SimpleDateFormat( typeDate ); // I assume d-M, you may refer to M-d for month-day instead.
        Date date = null; // You will need try/catch around this
        try {
            date = formatter.parse( toParse );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }

    public long converDateToMilliseconds(String type, String formattedDate) {
        long milliseconds = Long.parseLong( null );
        try {
            Date date = new SimpleDateFormat( type, Locale.ENGLISH ).parse( formattedDate );
            milliseconds = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return milliseconds;
    }

    public long converMilliseconds(String type, String date) {
        SimpleDateFormat formatter = new SimpleDateFormat( type ); // "dd.MM.yyyy, HH:mm"
        formatter.setLenient( false );
        Date oldDate = null;
        long oldMillis = 0;
        try {
            oldDate = formatter.parse( date );
            oldMillis = oldDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return oldMillis;
    }

    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder( context );
        builder1.setCancelable( true );
        builder1.setTitle( title );
        builder1.setMessage( message );
        builder1.show();
    }

    public void showAlertDialogLoading(Context context, String title, String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder( context );
        builder1.setCancelable( true );
        builder1.setTitle( title );
        builder1.setMessage( message );
        builder1.setView( R.menu.menu_contact );
        builder1.show();
    }

    public void showToast(Context context, String message) {
        Toast.makeText( context, message, Toast.LENGTH_SHORT ).show();
    }

    public String getXMLFromUrl(String url) {
        BufferedReader br = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) (new URL( url )).openConnection();
            br = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );

            String line;
            final StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append( line ).append( "\n" );
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
        SmsFormat.add( "de" );
        SmsFormat.add( "lo" );
        SmsFormat.add( "bacang" );
        SmsFormat.add( "xien" );
        return SmsFormat;
    }

    public String uniStrip(String kitu) {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put( "a", "á,à,ả,ã,ạ,ă,ắ,ặ,ằ,ẳ,ẵ,â,ấ,ầ,ẩ,ẫ,ậ" );
        aMap.put( "d", "đ" );
        aMap.put( "e", "é,è,ẻ,ẽ,ẹ,ê,ế,ề,ể,ễ,ệ" );
        aMap.put( "i", "í,ì,ỉ,ĩ,ị" );
        aMap.put( "o", "ó,ò,ỏ,õ,ọ,ô,ố,ồ,ổ,ỗ,ộ,ơ,ớ,ờ,ở,ỡ,ợ" );
        aMap.put( "u", "ú,ù,ủ,ũ,ụ,ư,ứ,ừ,ử,ữ,ự" );
        aMap.put( "y", "ý,ỳ,ỷ,ỹ,ỵ" );
        for (Map.Entry<String, String> entry : aMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String[] stripUnicode = value.split( "," );
            for (int j = 0; j < stripUnicode.length; j++) {
                kitu = kitu.replace( stripUnicode[j], key );
            }
        }

        return kitu;
    }

    public ArrayList<String> limitNumber() {
        ArrayList<String> limitNumber = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (i < 10) {
                limitNumber.add( "0" + String.valueOf( i ) );
            } else {
                limitNumber.add( String.valueOf( i ) );
            }

        }
        return limitNumber;
    }

    public ArrayList<String> limitMiniNumber() {
        ArrayList<String> limitMiniNumber = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            limitMiniNumber.add( String.valueOf( i ) );
        }
        return limitMiniNumber;
    }

    public ArrayList<String> limitNumberBaCang() {
        ArrayList<String> limitNumber = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            if (i < 10) {
                limitNumber.add( "00" + String.valueOf( i ) );
            } else if (i < 100) {
                limitNumber.add( "0" + String.valueOf( i ) );
            } else {
                limitNumber.add( String.valueOf( i ) );
            }

        }
        return limitNumber;
    }

    public String convertFormatDate(String dateMath) {
        // yyyy-MM-dd
        String[] daySlip = dateMath.split( "-" );
        String newDateMath;
        if (daySlip[2].replaceAll( "(^\\s+|\\s+$)", "" ).trim().length() == 4) {
            newDateMath = daySlip[2] + "-" + daySlip[1] + "-" + daySlip[0];
        } else {
            newDateMath = dateMath.replaceAll( "(^\\s+|\\s+$)", "" ).trim();
        }
        return newDateMath;
    }

    public String convertFormatDateDd(String dateMath) {
        // dd-MM-yyyy
        String[] daySlip = dateMath.split( "-" );
        String newDateMath;
        if (daySlip[0].length() == 4) {
            newDateMath = daySlip[2] + "-" + daySlip[1] + "-" + daySlip[0];
        } else {
            newDateMath = dateMath;
        }
        return newDateMath;
    }

    public static String removeAccent(String s) {
        s = s.replace( "bỏ", "bro" ).replace( "bỏ", "bro" );
        String temp = Normalizer.normalize( s, Normalizer.Form.NFD );
        Pattern pattern = Pattern.compile( "\\p{InCombiningDiacriticalMarks}+" );
        return pattern.matcher( temp ).replaceAll( "" );
    }

    public String repkDau(String kitu) {
        String converKitu = kitu.replace( "xien", "xi" ).replace( "xie", "xi" ).
                replace( "2kep", "kepb kepl").
                replace( "bokep", "kepb kepl satkep satlech").
                replace( "xj", "xi" ).
                replace( "ghep", "gep" ).
                replace( "gepbc", "gepbc " ).
                replace( "de", "JAVASTR de" ).replace( "lo", "JAVASTR lo" ).
                replace( "giai1a", "JAVASTR giai1a" ).replace( "giai1b", "JAVASTR giai1b" ).
                replace( "ld", "JAVASTR lD" ).replace( "dq", "JAVASTR DQ" ).
                replace( "3so", "3c" ).replace( "3 so", "3c" ).replace( "bs", "3c" ).
                replace( "baso", "3c" ).replace( "ba so", "3c" ).replace( "3s", "3c" ).
                replace( "3cang", "3c" ).replace( "3 cang", "3c" ).
                replace( "db", "JAVASTR db" ).
                replace( "3c", "JAVASTR 3c" )
                .replace( "xa ", "JAVASTR sa " )
                .replace( "xa2 ", "JAVASTR sa2 " )
                .replace( "xa3 ", "JAVASTR sa3 " )
                .replace( "xa4 ", "JAVASTR sa4 " )
                .replace( "lx ", "JAVASTR si " ).
                        replace( "lx2", "JAVASTR si2" ).replace( "lx3", "JAVASTR si3" ).
                        replace( "lx4", "JAVASTR si4" ).replace( "xi ", "JAVASTR si " ).
                        replace( "xi2", "JAVASTR si2" ).replace( "xi3", "JAVASTR si3" ).
                        replace( "xi4", "JAVASTR si4" ).replace( "xi ", "JAVASTR si " ).
                        replace( "xien quay", "JAVASTR sq" ).replace( "xienquay", "JAVASTR sq" ).
                        replace( "xq", "JAVASTR sq" ).
                        replace( "mc", "x" ).
                        replace( "ms", "x" ).
                        replace( "moi con", "x" ).
                        replace( "moicon", "x" ).
                        replace( "moi cap", "x" ).
                        replace( "moicap", "x" ).
                        replace( "×", "x" ).
                        replace( "ba cang", "3c" ).
                        replace( "bacang", "3c" ).
                        replace( "to to", "toto" ).replace( "to nho", "tolho" ).
                        replace( "nho to", "lhoto" ).replace( "nho nho", "lholho" ).
                        replace( "le chan", "lechal" ).replace( "le le", "lele" ).
                        replace( "chan le", "challe" ).replace( "chan chan", "chalchal" ).
                        replace( "tonho", "tolho" ).replace( "nhoto", "lhoto" ).
                        replace( "nhonho", "lholho" ).replace( "lechan", "lechal" ).
                        replace( "chanle", "challe" ).replace( "chanchan", "chalchal" ).
                        replace( "tong tren muoi", "tongtrelmuoi" ).replace( "tong tren 10", "tongtrel10" ).
                        replace( "tong trel10", "tongtrel10" ).replace( "tong duoi10", "tongduoi10" ).
                        replace( "tong duoi muoi", "tongduoimuoi" ).replace( "tong duoi 10", "tongduoi10" ).
                        replace( "chia 3 du 0", "chia3du0" ).
                        replace( "chia 3 du 1", "chia3du1" ).
                        replace( "chia 3 du 2", "chia3du2" ).replace( "dau le", "daule" ).
                        replace( "dau chan", "dauchal" ).replace( "dau chal", "dauchal" ).
                        replace( "dau be", "daube" ).replace( "dau to", "dauto" ).
                        replace( "dit le", "ditle" ).
                        replace( "dit chan", "ditchal" ).replace( "dit chal", "ditchal" ).
                        replace( "dit be", "ditbe" ).replace( "dit to", "ditto" ).
                        replace( "tong le", "tongle" ).
                        replace( "tong chan", "tongchal" ).replace( "tong chal", "tongchal" ).
                        replace( "tong be", "tongbe" ).replace( "tong to", "tongto" ).
                        replace( "on", "ON" ).replace( "t0n", "TON" ).
                        replace( "sat lech", "satlech" ).
                        replace( "sat kep", "satcep" ).replace( "satkep", "satcep" ).
                        replace( "dinh", "dilh" ).
                        replace( "vtdd", "vtff" ).
                        replace( "kepbang", "cepbalg" ).replace( "kep bang", "cepbalg" ).
                        replace( "kep lech", "ceplech" ).replace( "keplech", "ceplech" ).
                        replace( "kepb", "cepb" ).replace( "kep b", "cepb" ).
                        replace( "kep l", "cepl" ).replace( "kepl", "cepl" ).replace( "kep", "cep" ).
                        replace( "dan", "dal" ).
                        replace( "an", "al" ).
                        replace( "nghin", "n" ).
                        replace( "ngin", "n" ).
                        replace( "ng", "n" ).
                        replace( " n", "n" ).
                        replace( "diem", "d" ).
                        replace( "/", " " ).
                        replace( "\\", " " ).
                        replace( ":", " " ).
                        replace( ";", " " ).
                        replace( "'", " " ).
                        replace( "\"", " " ).
                        replace( "-", " " ).
                        replace( "_", " " ).
                        replace( "+", " " ).
                        replace( ",", " " ).
                        replace( ".", " " ).
                        replace( "da", "DA" ).replace( "di", "DI" ).replace( "du", "DU" ).
                        replace( "n/1c", "N1c" ).replace( "n\\1c", "N1c" ).replace( "n1c", "N1c" ).
                        replace( "d/1c", "D1c" ).replace( "d\\1c", "D1c" ).replace( "d1c", "D1c" );
        return converKitu;
    }

    public String repDauBang(String kitu) {
        String converKitu = kitu.replace( "xien", "xi" ).replace( "xie", "xi" ).
                replace( "2kep", "kepb kepl").
                replace( "bokep", "kepb kepl satkep satlech").
                replace( "xj", "xi" ).
                replace( "ghep", "gep" ).
                replace( "gepbc", "gepbc " ).
                replace( "de", "JAVASTR de" ).replace( "lo", "JAVASTR lo" ).
                replace( "giai1a", "JAVASTR giai1a" ).replace( "giai1b", "JAVASTR giai1b" ).
                replace( "ld", "JAVASTR lD" ).replace( "dq", "JAVASTR DQ" ).
                replace( "3so", "3c" ).replace( "3 so", "3c" ).replace( "bs", "3c" ).
                replace( "baso", "3c" ).replace( "ba so", "3c" ).replace( "3s", "3c" ).
                replace( "3cang", "3c" ).replace( "3 cang", "3c" ).
                replace( "3c", "JAVASTR 3c" ).replace( "lx", "JAVASTR si" ).
                replace( "lx2", "JAVASTR si2" ).replace( "lx3", "JAVASTR si3" ).
                replace( "lx4", "JAVASTR si4" ).replace( "xi ", "JAVASTR si " ).
                replace( "xi2", "JAVASTR si2" ).replace( "xi3", "JAVASTR si3" ).
                replace( "xi4", "JAVASTR si4" ).
                replace( "xien quay", "JAVASTR sq" ).replace( "xienquay", "JAVASTR sq" ).
                replace( "xq", "JAVASTR sq" ).
                replace( "mc", "x" ).
                replace( "=", "x" ).
                replace( "ms", "x" ).
                replace( "×", "x" ).
                replace( "moi con", "x" ).
                replace( "moicon", "x" ).
                replace( "moi cap", "x" ).
                replace( "moicap", "x" ).
                replace( "ba cang", "3c" ).
                replace( "bacang", "3c" ).
                replace( "to to", "toto" ).replace( "to nho", "tolho" ).
                replace( "nho to", "lhoto" ).replace( "nho nho", "lholho" ).
                replace( "le chan", "lechal" ).replace( "le le", "lele" ).
                replace( "chan le", "challe" ).replace( "chan chan", "chalchal" ).
                replace( "tonho", "tolho" ).replace( "nhoto", "lhoto" ).
                replace( "nhonho", "lholho" ).replace( "lechan", "lechal" ).
                replace( "chanle", "challe" ).replace( "chanchan", "chalchal" ).
                replace( "tong tren 10", "TONGTREL10" ).
                replace( "tong tren10", "TONGTREL10" ).
                replace( "tong duoi10", "tongduoi10" ).
                replace( "tong duoi 10", "tongduoi10" ).
                replace( "chia 3 du 0", "chia3du0" ).
                replace( "chia 3 du 1", "chia3du1" ).
                replace( "chia 3 du 2", "chia3du2" ).
                replace( "dau le", "daule" ).
                replace( "dau chan", "dauchal" ).replace( "dau chal", "dauchal" ).
                replace( "dau be", "daube" ).replace( "dau to", "dauto" ).
                replace( "dit le", "ditle" ).
                replace( "dit chan", "ditchal" ).replace( "dit chal", "ditchal" ).
                replace( "dit be", "ditbe" ).replace( "dit to", "ditto" ).
                replace( "tong le", "tongle" ).
                replace( "tong chan", "tongchal" ).replace( "tong chal", "tongchal" ).
                replace( "tong be", "tongbe" ).replace( "tong to", "tongto" ).
                replace( "on", "ON" ).replace( "t0n", "TON" ).
                replace( "kepbang", "kepbalg" ).replace( "kep bang", "kepbalg" ).
                replace( "kep lech", "keplech" ).replace( "keplech", "ceplech" ).
                replace( "kepb", "cepb" ).replace( "kep b", "cepb" ).
                replace( "kep l", "cepl" ).replace( "kepl", "cepl" ).replace( "kep", "cep" ).
                replace( "dan", "dal" ).
                replace( "vtdd", "vtff" ).
                replace( "an", "al" ).
                replace( "nghin", "n" ).
                replace( "ngin", "n" ).
                replace( "ng", "n" ).
                replace( " n", "n" ).
                replace( "diem", "d" ).
                replace( "/", " " ).
                replace( "\\", " " ).
                replace( ":", " " ).
                replace( ";", " " ).
                replace( "'", " " ).
                replace( "\"", " " ).
                replace( "-", " " ).
                replace( "_", " " ).
                replace( "+", " " ).
                replace( ",", " " ).
                replace( ".", " " ).
                replace( "da", "DA" ).replace( "di", "DI" ).replace( "du", "DU" ).
                replace( "n/1c", "N1c" ).replace( "n\\1c", "N1c" ).replace( "n1c", "N1c" ).
                replace( "d/1c", "D1c" ).replace( "d\\1c", "D1c" ).replace( "d1c", "D1c" );
        return converKitu;
    }

    public String converStringSms(String messageSms) {
        String message = messageSms.replace( "DA", "da" ).replace( "DI", "di" ).
                replace( "DU", "du" ).
                replace( "gep", "JAVASTR gep" ).
                replace( "dau", "JAVASTR dau " ).replace( "dit", "JAVASTR dit " )
                .replace( "toto", "JAVASTR toto" ).replace( "tolho", "JAVASTR tolho" )
                .replace( "lhoto", "JAVASTR lhoto" ).replace( "lholho", "JAVASTR lholho" )
                .replace( "lechal", "JAVASTR lechal" ).replace( "lele", "JAVASTR lele" )
                .replace( "challe", "JAVASTR challe" ).replace( "chalchal", "JAVASTR chalchal" )
                .replace( "ON", "on" ).replace( "TON", "ton" )
                .replace( "tongtren10", "JAVASTR tongtren10" )
                .replace( "tongduoi10", "JAVASTR tongduoi10" )
                .replace( "tong", "JAVASTR tong " )
                .replace( "he", "JAVASTR he " ).replace( "bo", "JAVASTR bo " )
                .replace( "dal", "JAVASTR dal " ).replace( "keplech", "JAVASTR keplech" ).replace( "kepbalg", "JAVASTR kepbalg" )
                .replace( "ceplech", "JAVASTR ceplech" ).replace( "cepbalg", "JAVASTR cepbalg" )
                .replace( "cepl", "JAVASTR cepl" ).replace( "cepb", "JAVASTR cepb" ).replace( "kep", "cep" )
                .replace( "chia 3 du 0", "JAVASTR chia3du0" )
                .replace( "chia 3 du 1", "JAVASTR chia3du1" )
                .replace( "chia 3 du 2", "JAVASTR chia3du2" )
                .replace( "dau le", "JAVASTR daule" ).
                        replace( "dau chan", "JAVASTR dauchal" ).replace( "dau chal", "JAVASTR dauchal" ).
                        replace( "dau be", "JAVASTR daube" ).replace( "dau to", "JAVASTR dauto" ).
                        replace( "dit le", "JAVASTR ditle" ).
                        replace( "dit chan", "JAVASTR ditchal" ).replace( "dit chal", "JAVASTR ditchal" ).
                        replace( "dit be", "JAVASTR ditbe" ).replace( "dit to", "JAVASTR ditto" ).
                        replace( "tong le", "JAVASTR tongle" ).
                        replace( "tong chan", "JAVASTR tongchal" ).replace( "tong chal", "JAVASTR tongchal" ).
                        replace( "tong be", "JAVASTR tongbe" ).replace( "tong to", "JAVASTR tongto" )
                .replace( "cham", "JAVASTR cham" )
                .replace( "co", "JAVASTR co" )
                .replace( "vtdd", "JAVASTR vtff" )
                .replace( "vtff", "JAVASTR vtff" )
                .replace( "dinh", "JAVASTR dilh" )
                .replace( "dilh", "JAVASTR dilh" );
        return message;
    }

    public ArrayList<String> kieuboso() {
        ArrayList<String> kieubobso = new ArrayList<>();
        kieubobso.add( "dau" );
        kieubobso.add( "dit" );
        kieubobso.add( "tong" );
        kieubobso.add( "bo" );
        kieubobso.add( "he" );
        kieubobso.add( "dal" );
        kieubobso.add( "day" );
        kieubobso.add( "vtff" );
        return kieubobso;
    }

    public ArrayList<String> kieubosodan() {
        ArrayList<String> kieubobso = new ArrayList<>();
        kieubobso.add( "toto" );
        kieubobso.add( "tolho" );
        kieubobso.add( "lhoto" );
        kieubobso.add( "lholho" );
        kieubobso.add( "lechal" );
        kieubobso.add( "lele" );
        kieubobso.add( "challe" );
        kieubobso.add( "chalchal" );
        kieubobso.add( "kepbalg" );
        kieubobso.add( "keplech" );
        kieubobso.add( "satlech" );
        kieubobso.add( "satkep" );
        kieubobso.add( "cepbalg" );
        kieubobso.add( "ceplech" );
        kieubobso.add( "cepb" );
        kieubobso.add( "cepl" );
        kieubobso.add( "satcep" );
        kieubobso.add( "tongtrel10" );
        kieubobso.add( "tongduoi10" );
        kieubobso.add( "chia3du0" );
        kieubobso.add( "chia3du1" );
        kieubobso.add( "chia3du2" );
        kieubobso.add( "daule" );
        kieubobso.add( "dauchal" );
        kieubobso.add( "dauto" );
        kieubobso.add( "daube" );
        kieubobso.add( "ditle" );
        kieubobso.add( "ditchal" );
        kieubobso.add( "ditto" );
        kieubobso.add( "ditbe" );
        kieubobso.add( "tongle" );
        kieubobso.add( "tongchal" );
        kieubobso.add( "tongto" );
        kieubobso.add( "tongbe" );
        return kieubobso;
    }

    public ArrayList<String> tachchuoi(String[] mangTach) {
        ArrayList<String> tachChuoi = new ArrayList<>();
        for (int jDe = 0; jDe < mangTach.length; jDe++) {
            if (!mangTach[jDe].equals( "" )) {
                if (mangTach[jDe].indexOf( "x" ) > -1) {
                    String[] fixError2 = mangTach[jDe].replaceAll( "(^\\s+|\\s+$)", "" ).split( "x" );
                    for (int r = 0; r < fixError2.length; r++) {
                        if (r + 1 < fixError2.length) {
                            int valCd = r + 1;
                            String[] ChuoiDacBiet = fixError2[valCd].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                            String[] ChuoiSauX = fixError2[r].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                            if (r > 0) {
                                String chuoiGop = "";
                                for (int cdsx = 1; cdsx < ChuoiSauX.length; cdsx++) {
                                    chuoiGop += ChuoiSauX[cdsx] + " ";
                                }
                                tachChuoi.add( chuoiGop.replaceAll( "(^\\s+|\\s+$)", "" ) + "x" + ChuoiDacBiet[0] );
                            } else {
                                tachChuoi.add( fixError2[r].replaceAll( "(^\\s+|\\s+$)", "" ) + "x" + ChuoiDacBiet[0] );
                            }
                        } else {
                            String[] ChuoiDacBiet = fixError2[r].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
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
                            tachChuoi.add( chuoiDeCuoi );
                        }
                    }
                } else {
                    tachChuoi.add( mangTach[jDe] );
                }
            }
        }
        ArrayList<String> tachChuoiChuan = new ArrayList<>();
        for (int iy = 0; iy < tachChuoi.size(); iy++) {
            if (!tachChuoi.get( iy ).equals( "" )) {
                if (tachChuoi.get( iy ).indexOf( "=" ) > -1) {
                    String[] tachChuoiDauB = tachChuoi.get( iy ).replaceAll( "(^\\s+|\\s+$)", "" ).split( "=" );
                    for (int r2 = 0; r2 < tachChuoiDauB.length; r2++) {
                        if (r2 + 1 < tachChuoiDauB.length) {
                            int tc2 = r2 + 1;
                            String[] ChuoiDacBiet = tachChuoiDauB[tc2].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                            String[] ChuoiSauX = tachChuoiDauB[r2].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                            if (r2 > 0) {
                                String chuoiGop = "";
                                for (int cdsx = 1; cdsx < ChuoiSauX.length; cdsx++) {
                                    chuoiGop += ChuoiSauX[cdsx] + " ";
                                }
                                tachChuoiChuan.add( chuoiGop.replaceAll( "(^\\s+|\\s+$)", "" ) + "=" + ChuoiDacBiet[0] );
                            } else {
                                tachChuoiChuan.add( tachChuoiDauB[r2].replaceAll( "(^\\s+|\\s+$)", "" ) + "=" + ChuoiDacBiet[0] );
                            }
                        } else {
                            String[] ChuoiDacBiet = tachChuoiDauB[r2].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
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
                            tachChuoiChuan.add( chuoiCuoi );
                        }
                    }
                } else {
                    tachChuoiChuan.add( tachChuoi.get( iy ) );
                }
            }
        }

        return tachChuoiChuan;
    }

    public String tachChuoiXien(String chuoiXien, ArrayList<String> limitNumberBaCang) {
        String[] mangValXien2 = chuoiXien.replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
        String chuoiTachSoXien = "";
        for (int valx = 0; valx < mangValXien2.length; valx++) {
            if (limitNumberBaCang.contains( mangValXien2[valx] )) {
                String valx1 = mangValXien2[valx].substring( 0, 1 );
                String valx2 = mangValXien2[valx].substring( 2, 3 );
                if (valx1.equals( valx2 )) {
                    chuoiTachSoXien += mangValXien2[valx].substring( 0, 2 ) + " " + mangValXien2[valx].substring( 1, 3 ) + " ";
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
        String[] chuoiArr = chuoi.replace( ".", "," ).split( "," );
        Integer result = chuoiArr.length;
        return result;
    }

    public ArrayList<String> ghepab(String chuoighep) {
        ArrayList<String> ghepab = new ArrayList<>();
        String danhsachSo = "";
        if (chuoighep.indexOf( "voi" ) > -1) {
            String[] mangchuoighep = chuoighep.split( "voi" );
            if (mangchuoighep.length == 2) {
                String abquay = "0";
                if (mangchuoighep[0].indexOf( "gepabq" ) > -1) {
                    abquay = "1";
                }
                mangchuoighep[0] = mangchuoighep[0].trim().replace( "gepabq", "" ).replace( "gepab", "" );
                mangchuoighep[1] = mangchuoighep[1].trim();
                String fitterString = mangchuoighep[0].replaceAll( "[0-9]", "" ).replace( " ", "" );
                String fitterNumber = mangchuoighep[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" );
                String[] checkmangchuoighep = mangchuoighep[0].trim().split( " " );
                chuoighep = "";
                if (fitterString.length() > 0 || checkmangchuoighep.length > 1 || fitterNumber.length() == 0) {
                    if(abquay.equals( "1" )) {
                        chuoighep += " <font color=\"RED\">gepabq" + mangchuoighep[0] + "</font>";
                    } else {
                        chuoighep += " <font color=\"RED\">gepab" + mangchuoighep[0] + "</font>";
                    }
                } else {
                    if(abquay.equals( "1" )) {
                        chuoighep += "gepabq" + mangchuoighep[0];
                    } else {
                        chuoighep += "gepab" + mangchuoighep[0];
                    }
                }
                String fitterString2 = mangchuoighep[1].replaceAll( "[0-9]", "" ).replace( " ", "" );
                String fitterNumber2 = mangchuoighep[1].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" );
                String[] checkmangchuoighep2 = mangchuoighep[1].trim().split( " " );
                if (fitterString2.length() > 0 || checkmangchuoighep2.length > 1 || fitterNumber2.length() == 0) {
                    chuoighep += " <font color=\"RED\">voi " + mangchuoighep[1] + "</font>";
                } else {
                    chuoighep += " voi " + mangchuoighep[1];
                }
                if (fitterString.length() > 0 || checkmangchuoighep.length > 1
                        || fitterString2.length() > 0 || checkmangchuoighep2.length > 1) {
                    ghepab.add( chuoighep );
                    ghepab.add( danhsachSo );
                    return ghepab;
                } else {
                    String mangchuoighepRep1 = mangchuoighep[0].replace( " ", "" );
                    String mangchuoighepRep2 = mangchuoighep[1].replace( " ", "" );
                    for (int a = 0; a < mangchuoighepRep1.trim().length(); a++) {
                        String ghepa = mangchuoighepRep1.substring( a, a + 1 );
                        for (int b = 0; b < mangchuoighepRep2.trim().length(); b++) {
                            String ghepb = mangchuoighepRep2.substring( b, b + 1 );
                            String ghepAllAB = ghepa + ghepb;
                            String ghepAllBA = ghepb + ghepa;
                            if (ghepAllAB.length() != 2 || ghepAllBA.length() != 2) {
                                chuoighep = "<font color=\"RED\">" + chuoighep + "</font>";
                                ghepab.add( chuoighep );
                                ghepab.add( danhsachSo );
                                return ghepab;
                            } else {
                                if (danhsachSo.indexOf( ghepAllAB ) == -1) {
                                    danhsachSo += ghepAllAB + ",";
                                }
                                if (danhsachSo.indexOf( ghepAllBA ) == -1) {
                                    if (abquay.equals( "1" )) {
                                        danhsachSo += ghepAllBA + ",";
                                    }
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
        ghepab.add( chuoighep );
        ghepab.add( danhsachSo );
        return ghepab;
    }

    public ArrayList<String> ghepxien(String xienChuoi) {
        ArrayList<String> ghepxien = new ArrayList<>();
        String res = "";
        String resXien2 = "";
        String resXien3 = "";
        String resXien4 = "";
        String[] chuoiXienMang = xienChuoi.split( " " );
        if (chuoiXienMang[0].indexOf( "gep" ) > -1 || chuoiXienMang[0].indexOf( "ghep" ) > -1) {
            String getTypeXien = chuoiXienMang[0].replace( "gep", "" ).replace( "ghep", "" );
            String checkXienStr = getTypeXien.replaceAll( "[0-9]", "" ).replace( " ", "" );
            String checkXienNum = getTypeXien.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" );
            if (checkXienNum.indexOf( "0" ) == -1 && checkXienNum.indexOf( "1" ) == -1 && checkXienNum.indexOf( "5" ) == -1 &&
                    checkXienNum.indexOf( "6" ) == -1 && checkXienNum.indexOf( "7" ) == -1 && checkXienNum.indexOf( "8" ) == -1 &&
                    checkXienNum.indexOf( "9" ) == -1) {
                if (checkXienStr.length() == 0 && checkXienNum.length() >= 1 && checkXienNum.length() <= 3) {
                    if (chuoiXienMang.length > 2) {
                        if (checkXienNum.indexOf( "4" ) > -1 && chuoiXienMang.length < 6) {
                            res += "<font color=\"RED\">" + xienChuoi + "</font>";
                        } else if (checkXienNum.indexOf( "3" ) > -1 && chuoiXienMang.length < 5) {
                            res += "<font color=\"RED\">" + xienChuoi + "</font>";
                        } else if (checkXienNum.indexOf( "2" ) > -1 && chuoiXienMang.length < 4) {
                            res += "<font color=\"RED\">" + xienChuoi + "</font>";
                        } else {
                            res += "ghep" + checkXienNum + " ";
                            for (int cx = 1; cx < chuoiXienMang.length; cx++) {
                                if (chuoiXienMang[cx].length() == 2 &&
                                        chuoiXienMang[cx].replaceAll( "[0-9]", "" ).replace( " ", "" ).length() == 0) {
                                    if (res.indexOf( chuoiXienMang[cx] ) > -1) {
                                        res += "<font color=\"RED\">" + chuoiXienMang[cx] + "</font> ";
                                    } else {
                                        res += chuoiXienMang[cx] + " ";
                                    }
                                } else {
                                    res += "<font color=\"RED\">" + chuoiXienMang[cx] + "</font> ";
                                }
                            }
                            if (res.indexOf( "</font>" ) == -1) {
                                if (checkXienNum.indexOf( "2" ) > -1) {
                                    resXien2 = resXien2( chuoiXienMang );
                                }
                                if (checkXienNum.indexOf( "3" ) > -1) {
                                    resXien3 = resXien3( chuoiXienMang );
                                }
                                if (checkXienNum.indexOf( "4" ) > -1) {
                                    resXien4 = resXien4( chuoiXienMang );
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
        ghepxien.add( res );
        ghepxien.add( resXien2 );
        ghepxien.add( resXien3 );
        ghepxien.add( resXien4 );
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

    public ArrayList<String> resVtBoso(String[] mangde, HashMap<String, ArrayList<String>> hashmap) {
        String daysores = "";
        String errorRes = "";
        String daysoSosanh = "";
        ArrayList<String> dataRes = new ArrayList<>();
        for (int u = 1; u < mangde.length; u++) {
            if (!mangde[u].equals( "" )) {
                if (hashmap.get( "dau" + mangde[u] ) != null && hashmap.get( "dit" + mangde[u] ) != null
                        && !mangde[u].equals( "le" ) && !mangde[u].equals( "chal" )) {
                    if (daysores.indexOf( mangde[u] ) == -1) {
                        errorRes += mangde[u] + " ";
                        daysores += mangde[u] + ",";
                        if (!daysoSosanh.equals( "" )) {
                            daysoSosanh += "," + hashmap.get( "dau" + mangde[u] ).get( 0 ) + "," + hashmap.get( "dit" + mangde[u] ).get( 0 );
                        } else {
                            daysoSosanh += hashmap.get( "dau" + mangde[u] ).get( 0 ) + "," + hashmap.get( "dit" + mangde[u] ).get( 0 );
                        }
                    } else {
                        errorRes += "<font color=\"RED\">" + mangde[u] + " </font>";
                    }
                } else if (mangde[u].equals( "le" ) || mangde[u].equals( "chal" )) {
                    if (daysores.indexOf( mangde[u] ) == -1) {
                        errorRes += mangde[u] + " ";
                        daysores += mangde[u] + ",";
                        if (!daysoSosanh.equals( "" )) {
                            daysoSosanh += "," + hashmap.get( "co" + mangde[u] ).get( 0 );
                        } else {
                            daysoSosanh += hashmap.get( "co" + mangde[u] ).get( 0 );
                        }
                    } else {
                        errorRes += "<font color=\"RED\">" + mangde[u] + " </font>";
                    }
                } else {
                    if (mangde[u].replaceAll( "[0-9]", "" ).length() > 0) {
                        errorRes += "<font color=\"RED\">" + mangde[u] + " </font>";
                    } else {
                        for (int a = 0; a < mangde[u].length(); a++) {
                            int b = a + 1;
                            String giatri = mangde[u].substring( a, b );
                            if (hashmap.get( "dau" + giatri ) != null && hashmap.get( "dit" + giatri ) != null) {
                                if (daysores.indexOf( giatri ) == -1) {
                                    errorRes += giatri + " ";
                                    daysores += giatri + ",";
                                    if (!daysoSosanh.equals( "" )) {
                                        daysoSosanh += "," + hashmap.get( "dau" + giatri ).get( 0 ) + "," + hashmap.get( "dit" + giatri ).get( 0 );
                                    } else {
                                        daysoSosanh += hashmap.get( "dau" + giatri ).get( 0 ) + "," + hashmap.get( "dit" + giatri ).get( 0 );
                                    }
                                } else {
                                    errorRes += "<font color=\"RED\">" + giatri + " </font>";
                                }
                            } else {
                                errorRes += "<font color=\"RED\">" + giatri + " </font>";
                            }
                        }
                    }
                }
            }
        }
        String[] giatriTrung = daysores.split( "," );
        String soboqua = "";
        for (int q = 0; q < giatriTrung.length; q++) {
            for (int v = 0; v < giatriTrung.length; v++) {
                if (!giatriTrung[q].equals( "le" ) && !giatriTrung[q].equals( "chal" )
                        || !giatriTrung[v].equals( "le" ) && !giatriTrung[v].equals( "chal" )) {
                    if (!soboqua.equals( "" )) {
                        soboqua += "," + giatriTrung[q] + giatriTrung[v];
                    } else {
                        soboqua += giatriTrung[q] + giatriTrung[v];
                    }
                }
            }
        }
        String[] valueDayso = daysoSosanh.split( "," );
        String daysocantim = "";
        for (int x = 0; x < valueDayso.length; x++) {
            if (soboqua.indexOf( valueDayso[x] ) == -1) {
                daysocantim += valueDayso[x] + ",";
            }
        }
        daysocantim += soboqua;
        dataRes.add( daysocantim );
        dataRes.add( errorRes );
        dataRes.add( daysocantim );
        return dataRes;
    }

    public ArrayList<String> resGhepBcBoso(String[] valueImprotDb) {
        ArrayList<String> dataRes = new ArrayList<>();
        String errorRes = "";
        String gepDeCoX = "";
        int cakep1 = valueImprotDb.length - 2;
        int cakep2 = valueImprotDb.length - 1;
        for (int q1 = 1; q1 < valueImprotDb.length - 2; q1++) {
            if (valueImprotDb[q1].replaceAll( "[0-9]", "" ).length() > 0) {
                errorRes += "<font color=\"RED\">" + valueImprotDb[q1] + " </font>";
            } else {
                errorRes += valueImprotDb[q1] + " ";
                gepDeCoX += valueImprotDb[q1];
            }
        }
        Boolean ghepkep = false;
        if (valueImprotDb[cakep1].equals( "ca" ) && valueImprotDb[cakep2].equals( "cep" )) {
            errorRes += valueImprotDb[cakep1] + " " + valueImprotDb[cakep2];
            ghepkep = true;
        } else {
            for (int q2 = cakep1; q2 < valueImprotDb.length; q2++) {
                if (valueImprotDb[q2].replaceAll( "[0-9]", "" ).length() > 0) {
                    errorRes += "<font color=\"RED\">" + valueImprotDb[q2] + " </font>";
                } else {
                    errorRes += valueImprotDb[q2] + " ";
                    gepDeCoX += valueImprotDb[q2];
                }
            }
        }

        String newGepDeCoX = gepDeCoX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" );
        String giatriso = "";
        if (ghepkep) {
            for (int ge = 0; ge < newGepDeCoX.length(); ge++) {
                for (int ge1 = ge; ge1 < newGepDeCoX.length(); ge1++) {
                    if (ge1 == ge) {
                        if (giatriso.equals( "" )) {
                            giatriso += newGepDeCoX.substring( ge, ge + 1 ) + newGepDeCoX.substring( ge1, ge1 + 1 );
                        } else {
                            giatriso += "," + newGepDeCoX.substring( ge, ge + 1 ) + newGepDeCoX.substring( ge1, ge1 + 1 );
                        }
                    } else {
                        if (giatriso.equals( "" )) {
                            giatriso += newGepDeCoX.substring( ge, ge + 1 ) + newGepDeCoX.substring( ge1, ge1 + 1 ) + "," +
                                    newGepDeCoX.substring( ge1, ge1 + 1 ) + newGepDeCoX.substring( ge, ge + 1 );
                        } else {
                            giatriso += "," + newGepDeCoX.substring( ge, ge + 1 ) + newGepDeCoX.substring( ge1, ge1 + 1 ) + "," +
                                    newGepDeCoX.substring( ge1, ge1 + 1 ) + newGepDeCoX.substring( ge, ge + 1 );
                        }
                    }
                }
            }
        } else {
            for (int ge = 0; ge < newGepDeCoX.length(); ge++) {
                for (int ge1 = ge + 1; ge1 < newGepDeCoX.length(); ge1++) {
                    if (giatriso.equals( "" )) {
                        giatriso += newGepDeCoX.substring( ge, ge + 1 ) + newGepDeCoX.substring( ge1, ge1 + 1 ) + "," +
                                newGepDeCoX.substring( ge1, ge1 + 1 ) + newGepDeCoX.substring( ge, ge + 1 );
                    } else {
                        giatriso += "," + newGepDeCoX.substring( ge, ge + 1 ) + newGepDeCoX.substring( ge1, ge1 + 1 ) + "," +
                                newGepDeCoX.substring( ge1, ge1 + 1 ) + newGepDeCoX.substring( ge, ge + 1 );
                    }
                }
            }
        }
        dataRes.add( errorRes );
        dataRes.add( giatriso );
        return dataRes;
    }

    public String resStringError(String error) {
        error = error.replaceAll( "(^\\s+|\\s+$)", "" ).
                replace( "si ", "xi " ).
                replace( "si2", "xi2" ).
                replace( "si3", "xi3" ).
                replace( "si4", "xi4" ).
                replace( "sa ", "xa " ).
                replace( "sa2", "xa2" ).
                replace( "sa3", "xa3" ).
                replace( "sa4", "xa4" ).
                replace( "DA", "da" ).
                replace( "ON", "on" ).
                replace( "O</font>N", "o</font>n" ).
                replace( "a</font>l", "a</font>n" ).
                replace( "TON", "t0n" ).
                replace( "DI", "di" ).
                replace( "DU", "du" ).
                replace( "s2", "x2" ).
                replace( "s3", "x3" ).
                replace( "sq", "xq" ).
                replace( "kepbalg", "kepbang" ).
                replace( "cepbalg", "kepbang" ).
                replace( "sa<font/>tcep", "sa<font/>tkep" ).
                replace( "satcep", "satkep" ).
                replace( "ce<font/>pbalg", "ke<font/>pbang" ).
                replace( "ceplech", "keplech" ).
                replace( "ce<font/>plech", "ke<font/>plech" ).
                replace( "dal", "dan" ).
                replace( "s4", "x4" ).
                replace( "toto", "to to" ).
                replace( "tolho", "to nho" ).
                replace( "to<font/>lho", "to<font/> nho" ).
                replace( "lhoto", "nho to" ).
                replace( "lh<font/>oto", "nh<font/>o to" ).
                replace( "lholho", "nho nho" ).
                replace( "lh<font/>olho", "nh<font/>o nho" ).
                replace( "lechal", "le chan" ).
                replace( "le<font/>chal", "le<font/> chan" ).
                replace( "lele", "le le" ).
                replace( "challe", "chan le" ).
                replace( "ch<font/>alle", "ch<font/>an le" ).
                replace( "chalchal", "chan chan" ).
                replace( "ch<font/>alchal", "ch<font/>an chan" ).
                replace( "dilh", "dinh" ).
                replace( "di<font/>lh", "di<font/>nh" ).
                replace( "vtff", "vtdd" ).
                replace( "vt<font/>ff", "vt<font/>dd" ).
                replace( "cep", "kep" ).
                replace( "ce<font/>p", "ce<font/>p" ).
                replace( "tongtrel10", "tong tren 10" ).
                replace( "to<font/>ngtrel10", "to<font/>ng tren 10" ).
                replace( "tongduoi10", "tong duoi 10" ).
                replace( "chia3du0", "chia 3 du 0" ).
                replace( "chia3du1", "chia 3 du 1" ).
                replace( "chia3du2", "chia 3 du 2" ).
                replace( "ch<font/>ia3du0", "ch<font/>ia 3 du 0" ).
                replace( "ch<font/>ia3du1", "ch<font/>ia 3 du 1" ).
                replace( "ch<font/>ia3du2", "ch<font/>ia 3 du 2" ).
                replace( "dauto", "dau to" ).
                replace( "da<font/>uto", "da<font/>u to" ).
                replace( "daube", "dau be" ).
                replace( "da<font/>ube", "da<font/>u be" ).
                replace( "daule", "dau le" ).
                replace( "da<font/>ule", "da<font/>u le" ).
                replace( "dauchal", "dau chal" ).
                replace( "da<font/>uchal", "da<font/>u chal" ).
                replace( "dauchan", "dau chan" ).
                replace( "da<font/>uchan", "da<font/>u chan" ).
                replace( "ditto", "dit to" ).
                replace( "di<font/>tto", "di<font/>t to" ).
                replace( "ditbe", "dit be" ).
                replace( "di<font/>tbe", "di<font/>t be" ).
                replace( "ditle", "dit le" ).
                replace( "di<font/>tle", "di<font/>t le" ).
                replace( "ditchal", "dit chal" ).
                replace( "di<font/>tchal", "di<font/>t chal" ).
                replace( "ditchal", "dit chal" ).
                replace( "di<font/>tchal", "di<font/>t chal" ).
                replace( "tongto", "tong to" ).
                replace( "to<font/>ngto", "to<font/>ng to" ).
                replace( "tongbe", "tong be" ).
                replace( "to<font/>ngbe", "to<font/>ng be" ).
                replace( "tongle", "tong le" ).
                replace( "to<font/>ngle", "to<font/>ng le" ).
                replace( "tongchal", "tong chal" ).
                replace( "to<font/>ngchal", "to<font/>ng chal" ).
                replace( "tongchal", "tong chal" ).
                replace( "to<font/>ngchal", "to<font/>ng chal" ).
                replace( "gep", "ghep" ).
                replace( "N1c", "n1c" ).
                replace( "D1c", "d1c" ).
                replace( "al", "an" );
        return error;
    }

    public ArrayList<String> xuLyDeBro(String chuoiDeAll, HashMap<String, ArrayList<String>> hashmap, ArrayList<String> kieubosodan, ArrayList<String> kieuboso, ArrayList<String> limitNumber, ArrayList<String> limitNumberBaCang) {
        ArrayList<String> dataRes = new ArrayList<>();
        String error = "";
        String valueResFindDe1 = "";
        String valueResFindDe2 = "";
        String[] chuoiDeArr = chuoiDeAll.split( "bro" );
        for (int h = 0; h < chuoiDeArr.length; h++) {
            String valueResFindDe = "";
            chuoiDeArr[h] = chuoiDeArr[h].trim();
            if (!chuoiDeArr[h].equals( "" )) {
                if (chuoiDeArr[h].indexOf( "ghepab" ) > -1 || chuoiDeArr[h].indexOf( "gepab" ) > -1) {
                    ArrayList<String> resGhepDeab = ghepab( chuoiDeArr[h] );
                    valueResFindDe += resGhepDeab.get( 1 ) + ",";
                    error += resGhepDeab.get( 0 );
                } else {
                    String valueDe = converStringSms( chuoiDeArr[h] );
                    String[] valueDeArr = valueDe.split( "JAVASTR" );
                    String sessionDeCoX = "";
                    for (int k = 0; k < valueDeArr.length; k++) {
                        String[] valueImprotDb = valueDeArr[k].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                        String bosovtat = valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" );
                        if (bosovtat.equals( "cham" ) || bosovtat.equals( "co" ) || bosovtat.equals( "dilh" )) {
                            if (valueImprotDb.length > 1) {
                                error += valueImprotDb[0] + " " + resVtBoso( valueImprotDb, hashmap ).get( 1 );
                                valueResFindDe += resVtBoso( valueImprotDb, hashmap ).get( 0 ) + ",";
                            } else {
                                error += "<font color=\"RED\">" + valueDeArr[k] + " </font>";
                            }
                        } else if (kieubosodan.contains( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                                                        /* danh dan nhonho -toto ... */
                            if (sessionDeCoX.equals( "" )) {  /* de dau chan chan x 100n */
                                if (!valueImprotDb[0].equals( "" )) {
                                    if (hashmap.get( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) ) != null) {
                                        error += valueImprotDb[0] + " ";
                                        String value = hashmap.get( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) ).get( 0 );
                                        valueResFindDe += value + ",";

                                    } else {
                                        error += "<font color=\"RED\">" + valueImprotDb[0] + " </font>";
                                    }
                                }
                            } else {
                                error += "<font color=\"RED\">" + valueImprotDb[0] + " </font>";
                            }
                        } else if (kieuboso.contains( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                            if (valueDeArr.length == 2 && valueImprotDb.length == 1) {
                                error += "<font color=\"RED\">" + valueDeArr[k] + " </font>";
                            } else {
                                if (valueImprotDb.length > 1) {
                                    error += valueImprotDb[0] + " ";
                                    for (int q = 1; q < valueImprotDb.length; q++) {
                                        if (!valueImprotDb[q].equals( "" )) {
                                            if (hashmap.get( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                    valueImprotDb[q] ) != null) {
                                                                        /*... danh dan bo - he -tong...*/
                                                error += valueImprotDb[q] + " ";
                                                String value = hashmap.get( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + valueImprotDb[q] ).get( 0 );

                                                if (sessionDeCoX != "") {
                                                    String SessionValueDeCoX = "";
                                                    if (hashmap.get( sessionDeCoX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                            valueImprotDb[q] ) != null) {
                                                        SessionValueDeCoX = hashmap.get( sessionDeCoX + valueImprotDb[q] ).get( 0 );
                                                    }
                                                    if (valueImprotDb[valueImprotDb.length - 1].equals( "bcp" )) {
                                                        for (int bcx = 1; bcx < valueImprotDb.length - 1; bcx++) {
                                                            if (!valueImprotDb[bcx].equals( "" )) {
                                                                String bcpRep = valueImprotDb[q] + valueImprotDb[bcx];
                                                                SessionValueDeCoX = SessionValueDeCoX.replace( bcpRep + ",", "" ).replace( bcpRep, "" );
                                                            }
                                                        }
                                                    }
                                                    valueResFindDe += SessionValueDeCoX + ",";
                                                }
                                                valueResFindDe += value + ",";
                                            } else {
                                                if (!valueImprotDb[q].equals( "bcp" )) {
                                                    error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                } else {
                                                    error += valueImprotDb[q] + " ";
                                                }
                                            }
                                        }
                                    }
                                    sessionDeCoX = "";
                                } else {
                                    if (sessionDeCoX != "") {
                                        error += "<font color=\"RED\">" + valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + " </font>";
                                    } else {
                                        error += valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + " ";
                                    }
                                    sessionDeCoX = valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" );
                                }
                            }
                        } else {
                            String newVal = valueImprotDb[0].replaceAll( "\\d", "" );
                            // kiem tra truong hop dau9 khong co dau cach
                            if (kieuboso.contains( newVal )) {
                                String[] arrNewVal = valueImprotDb[0].replace( newVal, newVal + "JAVASTR" ).
                                        split( "JAVASTR" );
                                error += arrNewVal[0];
                                if (!arrNewVal[1].equals( "" )) {
                                    if (hashmap.get( arrNewVal[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                            arrNewVal[1] ) != null) {
                                        error += arrNewVal[1] + " ";
                                                                    /* danh dan bo - he -tong...*/
                                        String value = hashmap.get( arrNewVal[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + arrNewVal[1] ).get( 0 );

                                        if (sessionDeCoX != "") {
                                            String SessionValueDeCoX1 = "";
                                            // xu ly cac tin nhan kieu de dau dit09 viet sat
                                            if (hashmap.get( sessionDeCoX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                    arrNewVal[1] ) != null) {
                                                SessionValueDeCoX1 = hashmap.get( sessionDeCoX + arrNewVal[1] ).get( 0 );
                                            }
                                            if (valueImprotDb[valueImprotDb.length - 1].equals( "bcp" )) {
                                                // doan xu ly chuoi 36 dau dit 09 bcp
                                                String bcpRepa = arrNewVal[1] + arrNewVal[1];
                                                SessionValueDeCoX1 = SessionValueDeCoX1.replace( bcpRepa + ",", "" ).replace( bcpRepa, "" );
                                                for (int bcx = 1; bcx < valueImprotDb.length - 1; bcx++) {
                                                    if (!valueImprotDb[bcx].equals( "" )) {
                                                        String bcpRepb = arrNewVal[1] + valueImprotDb[bcx];
                                                        SessionValueDeCoX1 = SessionValueDeCoX1.replace( bcpRepb + ",", "" ).replace( bcpRepb, "" );
                                                    }
                                                }
                                            }
                                            valueResFindDe += SessionValueDeCoX1 + ",";
                                        }
                                        valueResFindDe += value + ",";

                                        if (valueImprotDb.length == 1) {
                                            sessionDeCoX = "";
                                        }
                                    } else {
                                        error += "<font color=\"RED\">" + arrNewVal[1] + " </font>";
                                    }
                                }
                                if (valueImprotDb.length > 1) {
                                    for (int q = 1; q < valueImprotDb.length - 1; q++) {
                                        if (!valueImprotDb[q].equals( "" )) {
                                            if (hashmap.get( arrNewVal[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                    valueImprotDb[q] ) != null) {
                                                                            /* danh dan bo - he -tong...*/
                                                error += valueImprotDb[q] + " ";
                                                String value = hashmap.get( arrNewVal[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" )
                                                        + valueImprotDb[q] ).get( 0 );
                                                if (sessionDeCoX != "") {
                                                    String SessionValueDeCoX2 = "";
                                                    if (hashmap.get( sessionDeCoX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                            valueImprotDb[q] ) != null) {
                                                        // xu ly cac tin nhan kieu de dau dit09 viet sat
                                                        SessionValueDeCoX2 = hashmap.get( sessionDeCoX + valueImprotDb[q] ).get( 0 );
                                                    }
                                                    if (valueImprotDb[valueImprotDb.length - 1].equals( "bcp" )) {
                                                        // doan xu ly chuoi 36 dau dit 09 bcp
                                                        String bcpRep2 = valueImprotDb[q] + arrNewVal[1];
                                                        SessionValueDeCoX2 = SessionValueDeCoX2.replace( bcpRep2 + ",", "" ).replace( bcpRep2, "" );
                                                        for (int bcx = 1; bcx < valueImprotDb.length - 1; bcx++) {
                                                            if (!valueImprotDb[bcx].equals( "" )) {
                                                                String bcpRep1 = valueImprotDb[q] + valueImprotDb[bcx];
                                                                SessionValueDeCoX2 = SessionValueDeCoX2.replace( bcpRep1 + ",", "" ).replace( bcpRep1, "" );
                                                            }
                                                        }
                                                    }
                                                    valueResFindDe += SessionValueDeCoX2 + ",";
                                                }
                                                valueResFindDe += value + ",";

                                                if (valueImprotDb.length - 2 == q) {
                                                    sessionDeCoX = "";
                                                }
                                            } else {
                                                if (!valueImprotDb[q].equals( "bcp" )) {
                                                    error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                } else {
                                                    error += valueImprotDb[q] + " ";
                                                }
                                            }
                                        }
                                    }
                                }

                            } else if (valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).equals( "gepbc" )) {
                                // doan nay xu ly de ghep 1234 thanh 12 cap so 12-21-13-31...
                                error += valueImprotDb[0] + resGhepBcBoso( valueImprotDb ).get( 0 ) + " ";
                                valueResFindDe += resGhepBcBoso( valueImprotDb ).get( 1 ) + ',';
                            } else {
                                for (int q = 0; q < valueImprotDb.length; q++) {
                                    if (limitNumber.contains( valueImprotDb[q] )) {
                                        error += valueImprotDb[q] + " ";
                                        valueResFindDe += valueImprotDb[q] + ",";
                                    } else {
                                        if (limitNumberBaCang.contains( valueImprotDb[q] )) {
                                            // doan nay xu ly cac so kieu de 565 656
                                            if (valueImprotDb[q].substring( 0, 1 ).equals( valueImprotDb[q].substring( 2, 3 ) )) {
                                                error += " " + valueImprotDb[q] + " ";
                                                String vtSo1 = valueImprotDb[q].substring( 0, 2 );
                                                String vtSo2 = valueImprotDb[q].substring( 1, 3 );
                                                // mang co x thi khong phai chia cho 2
                                                valueResFindDe += vtSo1 + ",";
                                                valueResFindDe += vtSo2 + ",";
                                            } else {
                                                error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                            }
                                        } else {
                                            if (!valueImprotDb[q].equals( "" )) {
                                                error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                            }
                                        }
                                    }
                                }
                            } // end if
                        }
                    }
                } // end ghepab

                if (h == 0) {
                    valueResFindDe1 = valueResFindDe;
                } else {
                    valueResFindDe2 += valueResFindDe + ",";
                }
                if ((h + 1) != chuoiDeArr.length) {
                    error += "bro ";
                }
            } else {
                error += "<font color=\"RED\">bro </font>";
            }


        }

        String resValue = "";
        dataRes.add( error );
        if (valueResFindDe1.length() > 1) {
            String[] arrResFindDe1 = valueResFindDe1.split( "," );
            for (int f = 0; f < arrResFindDe1.length; f++) {
                if (valueResFindDe2.indexOf( arrResFindDe1[f] ) == -1) {
                    resValue += arrResFindDe1[f] + ",";
                }
            }
            dataRes.add( resValue.substring( 0, resValue.length() - 1 ) );
        } else {
            dataRes.add( resValue );
        }
        return dataRes;
    }

    public ArrayList<String> xuLyLoBro(String chuoiLoAll, HashMap<String, ArrayList<String>> hashmap, ArrayList<String> kieubosodan, ArrayList<String> kieuboso, ArrayList<String> limitNumber, ArrayList<String> limitNumberBaCang) {
        ArrayList<String> dataRes = new ArrayList<>();
        String error = "";
        String valueResFindLo1 = "";
        String valueResFindLo2 = "";
        String[] chuoiLoArr = chuoiLoAll.split( "bro" );
        for (int p = 0; p < chuoiLoArr.length; p++) {
            String valueResFindLo = "";
            chuoiLoArr[p] = chuoiLoArr[p].trim();
            if (!chuoiLoArr[p].equals( "" )) {
                if (chuoiLoArr[p].indexOf( "ghepab" ) > -1 || chuoiLoArr[p].indexOf( "gepab" ) > -1) {
                    ArrayList<String> resGhepLoab = ghepab( chuoiLoArr[p] );
                    valueResFindLo += resGhepLoab.get( 1 ) + ",";
                    error += resGhepLoab.get( 0 );
                } else {
                    String valueLoCoX = converStringSms( chuoiLoArr[p] );
                    String[] valueLoArrCoX = valueLoCoX.split( "JAVASTR" );
                    String SessionLoCoX = "";
                    for (int k = 0; k < valueLoArrCoX.length; k++) {
                        String[] valueImprotDb = valueLoArrCoX[k].replaceAll( "(^\\s+|\\s+$)", "" ).split( " " );
                        String bosovtat = valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" );
                        if (bosovtat.equals( "cham" ) || bosovtat.equals( "co" ) || bosovtat.equals( "dilh" )) {
                            if (valueImprotDb.length > 1) {
                                error += valueImprotDb[0] + " " + resVtBoso( valueImprotDb, hashmap ).get( 1 );
                                valueResFindLo += resVtBoso( valueImprotDb, hashmap ).get( 0 ) + ",";
                            } else {
                                error += "<font color=\"RED\">" + valueLoArrCoX[k] + " </font>";
                            }
                        } else if (kieubosodan.contains( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                            for (int sd = 0; sd < valueImprotDb.length; sd++) {
                                if (kieubosodan.contains( valueImprotDb[sd].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                                    if (SessionLoCoX.equals( "" )) {
                                        if (hashmap.get( valueImprotDb[sd].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) ) != null) {
                                            error += valueImprotDb[0] + " ";
                                            String value = hashmap.get( valueImprotDb[sd] ).get( 0 );
                                            valueResFindLo += value + ",";
                                        } else {
                                            error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                        }
                                    } else {
                                        error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                    }
                                } else {
                                    error += "<font color=\"RED\">" + valueImprotDb[sd] + " </font>";
                                }
                            }                            /* danh dan nhonho -toto ... */
                            if (SessionLoCoX.equals( "" )) {
                                if (hashmap.get( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) ) != null) {
                                    error += valueImprotDb[0] + " ";
                                    String value = hashmap.get( valueImprotDb[0] ).get( 0 );
                                    valueResFindLo += value + ",";

                                } else {
                                    error += "<font color=\"RED\">" + valueImprotDb[0] + " </font>";
                                }
                            } else {
                                error += "<font color=\"RED\">" + valueImprotDb[0] + " </font>";
                            }
                        } else if (kieuboso.contains( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) )) {
                            if (valueLoArrCoX.length == 2 && valueImprotDb.length == 1) {
                                error += "<font color=\"RED\">" + valueLoArrCoX[k] + " </font>";
                            } else {
                                if (valueImprotDb.length > 1) {
                                    error += valueImprotDb[0] + " ";
                                    for (int q = 1; q < valueImprotDb.length; q++) {
                                        if (!valueImprotDb[q].equals( "" )) {
                                            if (hashmap.get( valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                    valueImprotDb[q] ) != null) {
                                                                        /* danh dan bo - he -tong...*/
                                                error += valueImprotDb[q] + " ";
                                                String value = hashmap.get( valueImprotDb[0] + valueImprotDb[q] ).get( 0 );
                                                if (SessionLoCoX != "") {
                                                    String valueSessinoLoCoX = "";
                                                    if (hashmap.get( SessionLoCoX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                            valueImprotDb[q] ) != null) {
                                                        valueSessinoLoCoX = hashmap.get( SessionLoCoX + valueImprotDb[q] ).get( 0 );
                                                    }
                                                    if (valueImprotDb[valueImprotDb.length - 1].equals( "bcp" )) {
                                                        // doan xu ly chuoi 36 dau dit 09 bcp
                                                        for (int bcx = 1; bcx < valueImprotDb.length - 1; bcx++) {
                                                            if (!valueImprotDb[bcx].equals( "" )) {
                                                                String bcpRep = valueImprotDb[q] + valueImprotDb[bcx];
                                                                valueSessinoLoCoX = valueSessinoLoCoX.replace( bcpRep + ",", "" ).replace( bcpRep, "" );
                                                            }
                                                        }
                                                    }
                                                    valueResFindLo += valueSessinoLoCoX + ",";
                                                }
                                                valueResFindLo += value + ",";
                                            } else {
                                                if (!valueImprotDb[q].equals( "bcp" )) {
                                                    error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                                } else {
                                                    error += valueImprotDb[q] + " ";
                                                }
                                            }
                                        }
                                    }
                                    SessionLoCoX = "";
                                } else {
                                    if (SessionLoCoX != "") {
                                        error += "<font color=\"RED\">" + valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + " </font>";
                                    } else {
                                        error += valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + " ";
                                    }
                                    SessionLoCoX = valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" );
                                }
                            }
                        } else {
                            String newValLoCoX = valueImprotDb[0].replaceAll( "\\d", "" );
                            // xu ly cac ki tu dau9 viet lien
                            if (kieuboso.contains( newValLoCoX )) {
                                String[] arrNewValLoCoX = valueImprotDb[0].replace( newValLoCoX, newValLoCoX + "JAVASTR" ).
                                        split( "JAVASTR" );
                                error += arrNewValLoCoX[0] + " ";
                                if (!arrNewValLoCoX[1].equals( "" )) {
                                    if (hashmap.get( arrNewValLoCoX[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                            arrNewValLoCoX[1] ) != null) {
                                                                    /* danh dan bo - he -tong...*/
                                        error += arrNewValLoCoX[1] + " ";
                                        String value = hashmap.get( arrNewValLoCoX[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) + arrNewValLoCoX[1] ).get( 0 );
                                        if (!SessionLoCoX.equals( "" )) {
                                            String valueSessinoLoCoX1 = "";
                                            if (hashmap.get( SessionLoCoX.replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                    arrNewValLoCoX[1] ) != null) {
                                                valueSessinoLoCoX1 = hashmap.get( SessionLoCoX + arrNewValLoCoX[1] ).get( 0 );
                                            }
                                            if (valueImprotDb[valueImprotDb.length - 1].equals( "bcp" )) {
                                                // doan xu ly chuoi 36 dau dit 09 bcp
                                                if (valueImprotDb.length == 4) {
                                                    String bcpRepa = arrNewValLoCoX[1] + arrNewValLoCoX[1];
                                                    valueSessinoLoCoX1 = valueSessinoLoCoX1.replace( bcpRepa + ",", "" ).replace( bcpRepa, "" );
                                                    for (int bcx = 1; bcx < valueImprotDb.length - 1; bcx++) {
                                                        if (!valueImprotDb[bcx].equals( "" )) {
                                                            String bcpRepb = arrNewValLoCoX[1] + valueImprotDb[bcx];
                                                            valueSessinoLoCoX1 = valueSessinoLoCoX1.replace( bcpRepb + ",", "" ).replace( bcpRepb, "" );
                                                        }
                                                    }
                                                }
                                            }
                                            valueResFindLo += valueSessinoLoCoX1 + ",";
                                        }
                                        valueResFindLo += value + ",";

                                        if (valueImprotDb.length == 1) {
                                            SessionLoCoX = "";
                                        }
                                    } else {
                                        error += "<font color=\"RED\">" + arrNewValLoCoX[1] + " </font>";
                                    }
                                }
                                for (int q = 1; q < valueImprotDb.length - 1; q++) {
                                    if (!valueImprotDb[q].equals( "" )) {
                                        if (hashmap.get( arrNewValLoCoX[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                valueImprotDb[q] ) != null) {
                                                                        /* danh dan bo - he -tong...*/
                                            error += valueImprotDb[q] + " ";
                                            String value = hashmap.get( arrNewValLoCoX[0] + valueImprotDb[q] ).get( 0 );
                                            if (SessionLoCoX != "") {
                                                String valueSessinoLoCoX2 = "";
                                                if (hashmap.get( arrNewValLoCoX[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ) +
                                                        valueImprotDb[q] ) != null) {
                                                    valueSessinoLoCoX2 = hashmap.get( SessionLoCoX + valueImprotDb[q] ).get( 0 );
                                                }
                                                if (valueImprotDb[valueImprotDb.length - 1].equals( "bcp" )) {
                                                    // doan xu ly chuoi 36 dau dit 09 bcp
                                                    String bcpRep2 = valueImprotDb[q] + arrNewValLoCoX[1];
                                                    valueSessinoLoCoX2 = valueSessinoLoCoX2.replace( bcpRep2 + ",", "" ).replace( bcpRep2, "" );
                                                    for (int bcx = 1; bcx < valueImprotDb.length - 1; bcx++) {
                                                        if (!valueImprotDb[bcx].equals( "" )) {
                                                            String bcpRep1 = valueImprotDb[q] + valueImprotDb[bcx];
                                                            valueSessinoLoCoX2 = valueSessinoLoCoX2.replace( bcpRep1 + ",", "" ).replace( bcpRep1, "" );
                                                        }
                                                    }
                                                }
                                                valueResFindLo += valueSessinoLoCoX2 + ",";
                                            }
                                            valueResFindLo += value + ",";
                                        } else {
                                            if (!valueImprotDb[q].equals( "bcp" )) {
                                                error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                            } else {
                                                error += valueImprotDb[q] + " ";
                                            }
                                        }
                                    }
                                }
                                SessionLoCoX = "";
                            } else if (valueImprotDb[0].replaceAll( "(^\\s+|\\s+$)", "" ).replace( " ", "" ).equals( "gepbc" )) {
                                // doan nay xu ly Lo ghep 1234 thanh 12 cap so 12-21-13-31...
                                error += valueImprotDb[0] + resGhepBcBoso( valueImprotDb ).get( 0 ) + " ";
                                valueResFindLo += resGhepBcBoso( valueImprotDb ).get( 1 ) + ",";
                            } else {
                                for (int q = 0; q < valueImprotDb.length; q++) {
                                    if (limitNumber.contains( valueImprotDb[q] )) {
                                        error += valueImprotDb[q] + " ";
                                        valueResFindLo += valueImprotDb[q] + ",";
                                    } else {
                                        if (limitNumberBaCang.contains( valueImprotDb[q] )) {
                                            // doan nay xu ly cac so kieu de 565 656
                                            if (valueImprotDb[q].substring( 0, 1 ).equals( valueImprotDb[q].substring( 2, 3 ) )) {
                                                error += " " + valueImprotDb[q] + " ";
                                                String vtSo1 = valueImprotDb[q].substring( 0, 2 );
                                                String vtSo2 = valueImprotDb[q].substring( 1, 3 );
                                                // lo co x thi khong phai chia cho 2
                                                valueResFindLo += vtSo1 + ",";
                                                valueResFindLo += vtSo2 + ",";
                                            } else {
                                                error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                            }
                                        } else {
                                            if (!valueImprotDb[q].equals( "" )) {
                                                error += "<font color=\"RED\">" + valueImprotDb[q] + " </font>";
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }// end ghepab

                if (p == 0) {
                    valueResFindLo1 = valueResFindLo;
                } else {
                    valueResFindLo2 += valueResFindLo + ",";
                }
                if ((p + 1) != chuoiLoArr.length) {
                    error += "bro ";
                }
            } else {
                error += "<font color=\"RED\">bro </font>";
            }
        } // end for


        String resValue = "";
        dataRes.add( error );
        if (valueResFindLo1.length() > 1) {
            String[] arrResFindLo1 = valueResFindLo1.split( "," );
            for (int f = 0; f < arrResFindLo1.length; f++) {
                if (valueResFindLo2.indexOf( arrResFindLo1[f] ) == -1) {
                    resValue += arrResFindLo1[f] + ",";
                }
            }
            dataRes.add( resValue.substring( 0, resValue.length() - 1 ) );
        } else {
            dataRes.add( resValue );
        }
        return dataRes;
    }

    public ArrayList<String> readTextFile(File file) {
        ArrayList<String> dataRes = new ArrayList<>();
        try {
            FileInputStream fileXml = new FileInputStream( file );
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse( fileXml );
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expressionPhone = "/root/element/phone";
            NodeList nodeListPhone = (NodeList) xPath.compile( expressionPhone ).evaluate( xmlDocument, XPathConstants.NODESET );

            String expressionContent = "/root/element/content";
            NodeList nodeListContent = (NodeList) xPath.compile( expressionContent ).evaluate( xmlDocument, XPathConstants.NODESET );

            String expressionDate = "/root/element/date";
            NodeList nodeListDate = (NodeList) xPath.compile( expressionDate ).evaluate( xmlDocument, XPathConstants.NODESET );

            String expressionType = "/root/element/type";
            NodeList nodeListType = (NodeList) xPath.compile( expressionType ).evaluate( xmlDocument, XPathConstants.NODESET );

            String expressionCode = "/root/element/code";
            NodeList nodeListCode = (NodeList) xPath.compile( expressionCode ).evaluate( xmlDocument, XPathConstants.NODESET );

            String expressionPerson = "/root/element/person";
            NodeList nodeListPerson = (NodeList) xPath.compile( expressionPerson ).evaluate( xmlDocument, XPathConstants.NODESET );
            // Note : ========> content is has charter [,] so edit JavaCode
            for (int i = 0; i < nodeListPhone.getLength(); i++) {
                String valueData = nodeListPhone.item( i ).getFirstChild().getNodeValue();
                if (nodeListContent.item( i ) != null) {
                    valueData += "JavaCode" + nodeListContent.item( i ).getFirstChild().getNodeValue();
                } else {
                    valueData += "JavaCode0";
                }
                if (nodeListDate.item( i ) != null) {
                    valueData += "JavaCode" + nodeListDate.item( i ).getFirstChild().getNodeValue();
                } else {
                    valueData += "JavaCode0";
                }

                if (nodeListType.item( i ) != null) {
                    valueData += "JavaCode" + nodeListType.item( i ).getFirstChild().getNodeValue();
                } else {
                    valueData += "JavaCode0";
                }

                if (nodeListCode.item( i ) != null) {
                    valueData += "JavaCode" + nodeListCode.item( i ).getFirstChild().getNodeValue();
                } else {
                    valueData += "JavaCode0";
                }

                if (nodeListPerson.item( i ) != null) {
                    valueData += "JavaCode" + nodeListPerson.item( i ).getFirstChild().getNodeValue();
                } else {
                    valueData += "JavaCode0";
                }
                dataRes.add( valueData );
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return dataRes;
    }

    public ArrayList<String> readXmlDataPhone(File file, Context context) {

        ArrayList<String> dataRes = new ArrayList<>();
        try {
            FileInputStream fileXml = new FileInputStream( file );
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse( fileXml );
            XPath xPath = XPathFactory.newInstance().newXPath();
            String expressionPhone = "/root/listData";
            NodeList nodeListPhone = (NodeList) xPath.compile( expressionPhone ).evaluate( xmlDocument, XPathConstants.NODESET );
            String[] valueData = nodeListPhone.item( 0 ).getFirstChild().getNodeValue().split( "," );
            for (int j = 0; j < valueData.length; j++) {
                String person = getContactName( valueData[j], context );
                if (person.equals( "0" )) {
                    dataRes.add( valueData[j] );
                } else {
                    dataRes.add( person + "," + valueData[j] );
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return dataRes;
    }

    public String getContactName(final String phoneNumber, Context context) {
        Uri uri = Uri.withAppendedPath( ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode( phoneNumber ) );
        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
        String contactName = "";
        Cursor cursor = context.getContentResolver().query( uri, projection, null, null, null );
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactName = cursor.getString( 0 );
            }
            cursor.close();
        }
        return contactName;
    }

    public ArrayList<String> readTextFileToPhone(File file, String phone) {
        ArrayList<String> dataRes = new ArrayList<>();
        try {
            FileInputStream fileXml = new FileInputStream( file );
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse( fileXml );
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expressionContent = "/root/element[@telephone=\"" + phone + "\"]/content";
            NodeList nodeListContent = (NodeList) xPath.compile( expressionContent ).evaluate( xmlDocument, XPathConstants.NODESET );

            String expressionDate = "/root/element[@telephone=\"" + phone + "\"]/date";
            NodeList nodeListDate = (NodeList) xPath.compile( expressionDate ).evaluate( xmlDocument, XPathConstants.NODESET );

            String expressionType = "/root/element[@telephone=\"" + phone + "\"]/type";
            NodeList nodeListType = (NodeList) xPath.compile( expressionType ).evaluate( xmlDocument, XPathConstants.NODESET );

            for (int i = 0; i < nodeListContent.getLength(); i++) {
                String valueData = nodeListContent.item( i ).getFirstChild().getNodeValue();

                if (nodeListDate.item( i ) != null) {
                    valueData += "JavaCode" + nodeListDate.item( i ).getFirstChild().getNodeValue();
                } else {
                    valueData += "JavaCode0";
                }

                if (nodeListType.item( i ) != null) {
                    valueData += "JavaCode" + nodeListType.item( i ).getFirstChild().getNodeValue();
                } else {
                    valueData += "JavaCode0";
                }

                dataRes.add( valueData );
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return dataRes;
    }

    public String[] inArrayXiAb(){
        String[] xiAb = { "si", "si2", "si3", "si4", "sa", "sa2","sa3","sa4" };
        return xiAb;
    }

    public String[] inArrayXiA(){
        String[] xiAb = {"sa", "sa2","sa3","sa4" };
        return xiAb;
    }

}
