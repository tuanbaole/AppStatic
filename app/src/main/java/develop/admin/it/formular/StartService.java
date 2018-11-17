package develop.admin.it.formular;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Created by IT-PC on 11/12/2018.
 */

public class StartService extends Service {
    GlobalClass controller = new GlobalClass();
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        StartService.SendSmsObserver smsObeserver = (new StartService.SendSmsObserver(new Handler()));
        ContentResolver contentResolver = this.getContentResolver();
        contentResolver.registerContentObserver( Uri.parse("content://sms"),true, smsObeserver);
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class SendSmsObserver extends ContentObserver {
        private String lastSmsId;

        public SendSmsObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            // save the message to the SD card here
            Uri uriSMSURI = Uri.parse("content://sms");
            Cursor cur = getContentResolver().query(uriSMSURI, null, null, null, null);
            cur.moveToNext();
            String content = cur.getString(cur.getColumnIndex("body"));
            String smsNumber = cur.getString(cur.getColumnIndex("address"));
            String idMessage = cur.getString(cur.getColumnIndex("_id"));
            String type = cur.getString(cur.getColumnIndex("type"));
            String time = cur.getString(cur.getColumnIndex("date"));
            long dateSms = Long.parseLong(time);
            String vnTime = controller.converTimeMill("yyyy-MM-dd HH:mm:ss", dateSms);
            String vnDate = controller.converTimeMill("yyyy-MM-dd", dateSms);

            if (smsNumber == null || smsNumber.length() <= 0) {
                smsNumber = "Unknown";
            }
            Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(smsNumber));
            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
            String preson = "0";
            Cursor cursor= getContentResolver().query(uri,projection,null,null,null);
            if (cursor != null) {
                if(cursor.moveToFirst()) {
                    preson = cursor.getString(0);
                }
                cursor.close();
            }

            if (type.equals("1") || type.equals("2")) {
                smsNumber = smsNumber.replace( "+84","0");
                String code = idMessage;
                String filename = vnDate + ".xml";
                String path = String.valueOf( getApplicationContext().getFilesDir() );
                File file = new File(getApplicationContext().getFilesDir(),filename);
                if(!file.exists()){
                    createXml(filename,smsNumber,content,type,preson,code,vnTime);
                } else {
                    String dataPhone = readXmlDataPhone(file);
                    ArrayList<String> dataXml = ReadFileXml(file);
                    editXml(file,filename,smsNumber,content,type,preson,code,dataPhone,vnTime, dataXml);
                }
            }

            cur.close();

        }

        public boolean smsChecker(String sms) {
            boolean flagSMS = true;

            if (sms.equals(lastSmsId)) {
                flagSMS = false;
            }
            else {
                lastSmsId = sms;
            }
            return flagSMS;
        }

    }

    public void createXml(String filename, String phone,String content,String type,String person,String code,String date) {
        FileOutputStream fos;
        try {
            fos = openFileOutput(filename, Context.MODE_APPEND);
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument(null, Boolean.valueOf(true));
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            serializer.startTag(null, "root");
            serializer.startTag(null, "listData");
            serializer.text(phone);
            serializer.endTag(null, "listData");

            serializer.startTag(null, "element");
            serializer.attribute(null, "telephone", phone);

            serializer.startTag(null, "phone");
            serializer.text(phone);
            serializer.endTag(null, "phone");

            serializer.startTag(null, "content");
            serializer.text(content);
            serializer.endTag(null, "content");

            serializer.startTag(null, "date");
            serializer.text(date);
            serializer.endTag(null, "date");

            serializer.startTag( null, "type" );
            serializer.text( type );
            serializer.endTag( null, "type" );

            serializer.startTag( null, "code" );
            serializer.text( code );
            serializer.endTag( null, "code" );

            serializer.startTag(null, "person");
            serializer.text(person);
            serializer.endTag(null, "person");

            serializer.endTag(null, "element");

            serializer.endTag(null, "root");
            serializer.endDocument();
            serializer.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readXmlDataPhone(File file) {
        String valueData = "";
        try {
            FileInputStream fileXml = new FileInputStream(file);
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder =  builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(fileXml);
            XPath xPath =  XPathFactory.newInstance().newXPath();
            String expressionPhone = "/root/listData";
            NodeList nodeListPhone = (NodeList) xPath.compile(expressionPhone).evaluate(xmlDocument, XPathConstants.NODESET);
            valueData = nodeListPhone.item(0).getFirstChild().getNodeValue();
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
        return valueData;
    }

    public ArrayList<String> ReadFileXml(File file) {
        ArrayList<String> dataRes = new ArrayList<>();
        FileInputStream fileXml = null;
        try {
            fileXml = new FileInputStream(file);
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder =  builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(fileXml);
            XPath xPath =  XPathFactory.newInstance().newXPath();

            String expressionPhone = "/root/element/phone";
            NodeList nodeListPhone = (NodeList) xPath.compile(expressionPhone).evaluate(xmlDocument, XPathConstants.NODESET);

            String expressionContent = "/root/element/content";
            NodeList nodeListContent = (NodeList) xPath.compile(expressionContent).evaluate(xmlDocument, XPathConstants.NODESET);

            String expressionDate = "/root/element/date";
            NodeList nodeListDate = (NodeList) xPath.compile(expressionDate).evaluate(xmlDocument, XPathConstants.NODESET);

            String expressionType = "/root/element/type";
            NodeList nodeListType = (NodeList) xPath.compile(expressionType).evaluate(xmlDocument, XPathConstants.NODESET);

            String expressionCode = "/root/element/code";
            NodeList nodeListCode = (NodeList) xPath.compile(expressionCode).evaluate(xmlDocument, XPathConstants.NODESET);

            String expressionPerson = "/root/element/person";
            NodeList nodeListPerson = (NodeList) xPath.compile(expressionPerson).evaluate(xmlDocument, XPathConstants.NODESET);

            for (int i = 0; i < nodeListPhone.getLength(); i++) {
                String valueRes = "";
                valueRes += nodeListPhone.item( i ).getFirstChild().getNodeValue();
                valueRes += "JavaCode" + nodeListContent.item( i ).getFirstChild().getNodeValue();
                valueRes += "JavaCode" + nodeListDate.item( i ).getFirstChild().getNodeValue();
                valueRes += "JavaCode" + nodeListType.item( i ).getFirstChild().getNodeValue();
                valueRes += "JavaCode" + nodeListCode.item( i ).getFirstChild().getNodeValue();
                valueRes += "JavaCode" + nodeListPerson.item( i ).getFirstChild().getNodeValue();
                dataRes.add(valueRes);
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

    public void editXml(File file, String filename, String phone, String content,String type,String person,
                        String code, String dataPhone, String date,ArrayList<String> dataXml) {
        FileOutputStream fileos = null;
        try {
            fileos =new FileOutputStream(file);
            if (fileos!=null){
                Context context= getApplicationContext();
                fileos = context.openFileOutput(filename, Context.MODE_APPEND);
            };
            if (dataPhone.indexOf( phone ) == -1 ) {
                dataPhone += "," + phone;
            }
            XmlSerializer serializer = Xml.newSerializer();
            try{
                serializer.setOutput(fileos, "UTF-8");
                serializer.startDocument(null, Boolean.valueOf(true));
                serializer.startTag( null, "root" );

                serializer.startTag(null, "listData");
                serializer.text(dataPhone);
                serializer.endTag(null, "listData");

                boolean requiredSms = true;
                for (int i = 0; i < dataXml.size();i++) {
                    String [] importDataXml = dataXml.get( i ).split( "JavaCode" );
                    serializer.startTag(null, "element");
                    serializer.attribute(null, "telephone", importDataXml[0]);

                    serializer.startTag( null, "phone" );
                    serializer.text( importDataXml[0] );
                    serializer.endTag( null, "phone" );

                    serializer.startTag( null, "content" );
                    serializer.text( importDataXml[1] );
                    serializer.endTag( null, "content" );

                    serializer.startTag( null, "date" );
                    serializer.text( importDataXml[2] );
                    serializer.endTag( null, "date" );

                    serializer.startTag( null, "type" );
                    serializer.text( importDataXml[3] );
                    serializer.endTag( null, "type" );

                    serializer.startTag( null, "code" );
                    serializer.text( importDataXml[4] );
                    serializer.endTag( null, "code" );

                    serializer.startTag( null, "person" );
                    serializer.text( importDataXml[5] );
                    serializer.endTag( null, "person" );
                    serializer.endTag(null, "element");
                }

                int lengSms = dataXml.size() - 1;
                String [] importDataXmlEnd = dataXml.get(lengSms).split( "JavaCode" );
                if (importDataXmlEnd[0].equals( phone ) && importDataXmlEnd[1].equals( content ) &&
                        importDataXmlEnd[3].equals( type ) && importDataXmlEnd[4].equals( code )) {
                    requiredSms = false;
                }

                if (requiredSms && code != "" && code != null) {
                    serializer.startTag(null, "element");
                    serializer.attribute(null, "telephone", phone);

                    serializer.startTag( null, "phone" );
                    serializer.text( phone );
                    serializer.endTag( null, "phone" );

                    serializer.startTag( null, "content" );
                    serializer.text( content );
                    serializer.endTag( null, "content" );

                    serializer.startTag( null, "date" );
                    serializer.text( date );
                    serializer.endTag( null, "date" );

                    serializer.startTag( null, "type" );
                    serializer.text( type );
                    serializer.endTag( null, "type" );

                    serializer.startTag( null, "code" );
                    serializer.text( code );
                    serializer.endTag( null, "code" );

                    serializer.startTag( null, "person" );
                    serializer.text( person );
                    serializer.endTag( null, "person" );

                    serializer.endTag(null, "element");
                }

                serializer.endTag(null, "root");
                serializer.endDocument();
                serializer.flush();
                fileos.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
