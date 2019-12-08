package com.example.dublicate;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;


import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class OneActivity extends AppCompatActivity {

    private AdView mAdView;

    Button buttonadd, button, outputTXT;
    DatabaseHelper dbHelper;
    TextView txtShow, textTest;
    Parcelable state;
    ListView lvOne;
    int progress = 0;
    private static final int FILE_SELECT_CODE = 0;


    public static int PICK_FILE = 1;

    class Item{
        String name;
        int price;
        Item(String name, int price){

            this.name = name;
            this.price = price;
        }
    }


    //private AdView mAdView;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        buttonadd = findViewById(R.id.buttonadd);
        button = findViewById(R.id.button);
        outputTXT = findViewById(R.id.outputTXT);
        txtShow = findViewById(R.id.txtShow);
        textTest = findViewById(R.id.testText);
        lvOne = findViewById(R.id.items1);
        lvOne.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        final ItemsAdapter adapter = new ItemsAdapter();
        lvOne.setAdapter(adapter);


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });


        dbHelper = new DatabaseHelper(this);
        lvOne.setChoiceMode(ListView.CHOICE_MODE_SINGLE);



        if(state != null) {
            lvOne.onRestoreInstanceState(state);
        }


        outputTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {



                SQLiteDatabase db9 = dbHelper.getReadableDatabase();
                int numRows = (int) DatabaseUtils.queryNumEntries(db9, "words");
                String[] array1 = new String[numRows];
                int i = 0;
                db9.rawQuery("SELECT * FROM " + "words" + " ORDER BY " + "count", null);
                Cursor c = db9.query(DatabaseHelper.TABLE, null, null, null, null, null, "count desc");
                if (c.moveToFirst()) {

                    // определяем номера столбцов по имени в выборке
                    int idColIndex = c.getColumnIndex("id");
                    int wordColIndex = c.getColumnIndex("word");
                    int countColIndex = c.getColumnIndex("count");

                    do {
                        String empty = "";
                        if (c.getString(wordColIndex).equals(empty)) {

                            array1[i] = c.getString(wordColIndex) + ", ";
                            i++;

                        }

                    } while (c.moveToNext());

                } else
                    c.close();

                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Duplicate/word.txt";
                String txt = array1.toString();
                /*byte[] toWrite = txt.getBytes();
                try {
                    FileOutputStream fos = new FileOutputStream(filePath);
                    fos.write(toWrite);
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
*/

                try {
                    /*
                     * Создается объект файла, при этом путь к файлу находиться методом класcа Environment
                     * Обращение идёт, как и было сказано выше к внешнему накопителю
                     */
                    File myFile = new File(Environment.getExternalStorageDirectory().toString() + "/" + "word.txt");
                    myFile.createNewFile();                                         // Создается файл, если он не был создан
                    FileOutputStream outputStream = new FileOutputStream(myFile);   // После чего создаем поток для записи
                    outputStream.write(txt.getBytes());                            // и производим непосредственно запись
                    outputStream.close();
                    /*
                     * Вызов сообщения Toast не относится к теме.
                     * Просто для удобства визуального контроля исполнения метода в приложении
                     */
                } catch (Exception e) {
                    e.printStackTrace();
                }




                /*File file = new File("/storage/emulated/0/Download/", "word.txt");

                try {
                    *//*FileOutputStream fos=new FileOutputStream("/storage/emulated/0/Download/word.txt");*//*
                    file.createNewFile();
                    FileWriter writer = new FileWriter(file);

                        String text = array1.toString();
                        byte[] buffer = text.getBytes();
                        writer.write(text);
                        writer.flush();
                        writer.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
*/

            }
        });


        buttonadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, PICK_FILE);

                adapter.clear();
                adapter.notifyDataSetChanged();


                final SQLiteDatabase db5 = dbHelper.getWritableDatabase();
                db5.delete("words", null, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(OneActivity.this);
                builder.setTitle("Предупреждение")
                        .setMessage("После нажатие (Заполнить данные), будет идти загрузка, время ожидания зависит от объема текста.")
                        .setCancelable(false)
                        .setNegativeButton("Да понятно уже...",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();


            }
        });


        button.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick (View view) {




                adapter.clear();
                adapter.notifyDataSetChanged();

                final SQLiteDatabase db7 = dbHelper.getWritableDatabase();
                db7.delete("words", null, null);

                SQLiteDatabase db = dbHelper.getWritableDatabase();


                final String myTxt = txtShow.getText().toString();


                String s = myTxt.toLowerCase();
                String stopWords[] = {"is", "in", "of", "on", "to", "into", "are", "and", "at", "with", "a", "the", "for", "their", "from", "as",
                        "but", "i", "s", "there", "so", "an", "over", "when", "by", "or", "under", "if", "than", "same", "well", "those",
                        "off", "how", "these", "it", "them", "not", " *"};
                for (int i = 0; i < stopWords.length; i++) {
                    if (s.contains(stopWords[i])) {
                        s = s.replaceAll(stopWords[i] + "\\s+", "");
                    }
                }


                String text = deDup(s).toLowerCase();
                final String[] keys = text.split(" ");
                final String[] uniqueKeys;
                int count = 0;
                uniqueKeys = getUniqueKeys(keys);


                int i = 0;

                String sql = "INSERT INTO " + DatabaseHelper.TABLE + " VALUES(?,?,?);";
                SQLiteStatement sqLiteStatement = db.compileStatement(sql);
                db.beginTransaction();
                try {


                    for (String key : uniqueKeys) {

                        if (null == key) {
                            break;
                        }


                        for (String sTxt : keys) {

                            if (key.equals(sTxt)) {
                                count++;
                            }
                        }

                        sqLiteStatement.clearBindings();
                        sqLiteStatement.bindNull(1);
                        sqLiteStatement.bindString(2, key);
                        sqLiteStatement.bindLong(3, count);
                        sqLiteStatement.execute();

                        String empty = "";


                        count = 0;

                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                    SQLiteDatabase db2 = dbHelper.getReadableDatabase();
                    int numRows = (int) DatabaseUtils.queryNumEntries(db2, "words");
                    db2.rawQuery("SELECT * FROM " + "words" + " ORDER BY " + "count", null);
                    Cursor c = db2.query(DatabaseHelper.TABLE, null, null, null, null, null, "count desc");
                    if (c.moveToFirst()) {

                        // определяем номера столбцов по имени в выборке
                        int idColIndex = c.getColumnIndex("id");
                        int wordColIndex = c.getColumnIndex("word");
                        int countColIndex = c.getColumnIndex("count");

                        do {
                            String empty = "";
                            if (c.getString(wordColIndex).equals(empty)) {

                            } else if (c.getInt(countColIndex) > 5) {
                                adapter.add(new Item(c.getString(wordColIndex), c.getInt(countColIndex)));
                            }

                        } while (c.moveToNext());
                    } else

                        c.close();

                    adapter.notifyDataSetChanged();
                    db2.close();
                  //  progressBar.setVisibility(View.GONE);

                }



        });

        lvOne.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> adapterView, View view, int position, long l) {

                SQLiteDatabase db3 = dbHelper.getReadableDatabase();
                db3.rawQuery("SELECT * FROM " + "words" + " ORDER BY "+ "count", null);
                Cursor c2 = db3.query(DatabaseHelper.TABLE, null, null, null, null, null,"count desc" );
                int i = 0;
                int numRows = (int) DatabaseUtils.queryNumEntries(db3, "words");
                final String[] array1 = new String[numRows];
                if (c2.moveToFirst()) {

                    // определяем номера столбцов по имени в выборке
                    int idColIndex = c2.getColumnIndex("id");
                    int wordColIndex = c2.getColumnIndex("word");
                    int countColIndex = c2.getColumnIndex("count");

                    do {
                        String empty = "";
                        if(c2.getString(wordColIndex).equals(empty)){

                        }
                        else if(c2.getInt(countColIndex) > 5) {
                            array1[i] = c2.getString(wordColIndex);
                            i++;
                            // переход на следующую строку
                            // а если следующей нет (текущая - последняя), то false - выходим из цикла
                        }

                    } while (c2.moveToNext());
                } else

                    c2.close();



                final String selectedItem = array1[position];




                Runnable runnable = new Runnable() {

                    @Override
                    public void run () {

                        Message msg = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        String word = translate(selectedItem, "en-ru");
                        bundle.putString("Key", word);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }

        });






    }


    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }

    public static String[] getUniqueKeys (String[] keys)
    {
        String[] uniqueKeys = new String[keys.length];

        uniqueKeys[0] = keys[0];
        int uniqueKeyIndex = 1;
        boolean keyAlreadyExists = false;

        for(int i=1; i<keys.length ; i++)
        {
            for(int j=0; j<=uniqueKeyIndex; j++)
            {
                if(keys[i].equals(uniqueKeys[j]))
                {
                    keyAlreadyExists = true;
                }
            }

            if(!keyAlreadyExists)
            {
                uniqueKeys[uniqueKeyIndex] = keys[i];
                uniqueKeyIndex++;
            }
            keyAlreadyExists = false;
        }
        return uniqueKeys;
    }


    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String date = bundle.getString("Key");
            textTest.setText(date);
        }
    };






    private String readTextFile(Uri uri)
    {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try
        {
            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));

            String line = "";
            while ((line = reader.readLine()) != null)
            {
                builder.append(line);
            }
            reader.close();
        }
        catch (IOException e) {e.printStackTrace();}
        return builder.toString();
    }





    public String deDup(String s) {
        return new LinkedHashSet<String>(Arrays.asList(s.split("-"))).toString().replaceAll("(^\\[|\\]$)", "").replace(", ", "-");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE)
        {
            if (resultCode == RESULT_OK)
            {


                Uri uri = data.getData();
                String name = uri.getPath();
                String ext = FilenameUtils.getExtension(name);
                String fileContent = readTextFile(uri);

                if (ext.equals("fb2")) {
                    Document html = Jsoup.parse(fileContent);
                    String title = html.title();
                    String p = html.body().getElementsByTag("p").text();
                    txtShow.setText(p);
                }

                if (ext.equals("xml")) {
                    Document html = Jsoup.parse(fileContent);
                    String title = html.title();
                    String p = html.body().getElementsByTag("p").text();
                    txtShow.setText(p.replaceAll("[^a-zA-Z ]", " "));
                }

                if (ext.equals("txt")) {
                    txtShow.setText(fileContent.replaceAll("[^a-zA-Z ]", " "));
                }

            }



                //txtShow.setText(fileContentXml.replaceAll("[^a-zA-Z ]", " "));



                  /*  String fileContent = readXmlFile(uri);
                    txtShow.setText(fileContent);*/



            }
        }




    public String translate(String text, String lang) {
        byte out[] = ("text=" + text).getBytes();
        if (out.length>10000) {
            return "Error. Text too long";
        }
        String key = "trnsl.1.1.20191001T151309Z.1a5767049ff0ff2b.fa910499f0dd7649161636161ff637be7da7d63d"; //Подставьте сюда свой полученный ключ между кавычек
        String baseUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate";
        String finalUrl = baseUrl + "?lang=" + lang + "&key=" + key;
        try {
            URL url = new URL(finalUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", out.length + "");
            connection.setRequestProperty("Accept", "*/*");
            connection.getOutputStream().write(out);
            if (connection.getResponseCode() == 200) {
                JsonObject jobj = new JsonParser().parse(
                        new InputStreamReader(connection.getInputStream())).getAsJsonObject();
                JsonArray jarr = jobj.get("text").getAsJsonArray();
                return jarr.get(0).getAsString();
            } else {
                return "Error. Site response non 200";
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "Error";
        }
    }



    public class ItemsAdapter extends ArrayAdapter<Item> {
        public ItemsAdapter() { super(OneActivity.this, R.layout.item); }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = getLayoutInflater().inflate(R.layout.item, null);
            final Item item = getItem(position);
            ((TextView) view.findViewById(R.id.name)).setText(item.name);
            ((TextView) view.findViewById(R.id.price)).setText(String.valueOf(item.price));

            return view;
        }
    }

}
