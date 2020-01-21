package com.example.dublicate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class OneActivity extends AppCompatActivity {

    private AdView mAdView;

    Button buttonadd, button, outputTXT, info;
    DatabaseHelper dbHelper;
    TextView txtShow, textTest, test;
    ProgressBar progressBar, pb;
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
        //progressBar = findViewById(R.id.progressbar);
        button = findViewById(R.id.button);
        outputTXT = findViewById(R.id.outputTXT);
        info = findViewById(R.id.info);
        txtShow = findViewById(R.id.txtShow);
        textTest = findViewById(R.id.testText);
        test = findViewById(R.id.test);
        lvOne = findViewById(R.id.items1);
        lvOne.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
       /* final ItemsAdapter adapter = new ItemsAdapter();
        lvOne.setAdapter(adapter);*/



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


        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(OneActivity.this);
                builder.setTitle("Путь к файлу")
                        .setMessage("Файл со словами находится по пути - Внутреняя память - /Android/Data/com.example.duplicate/")
                        .setCancelable(false)
                        .setNegativeButton("Ок",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        outputTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {



                SQLiteDatabase db3 = dbHelper.getReadableDatabase();
                db3.rawQuery("SELECT * FROM " + "words" + " ORDER BY "+ "count", null);
                Cursor c2 = db3.query(DatabaseHelper.TABLE, null, null, null, null, null,"count desc" );
                if (c2.moveToFirst()) {


                    int idColIndex = c2.getColumnIndex("id");
                    int wordColIndex = c2.getColumnIndex("word");
                    int countColIndex = c2.getColumnIndex("count");

                    do {
                        String empty = "";
                        if(c2.getString(wordColIndex).equals(empty)){

                        }
                        else if(c2.getInt(countColIndex) > 5) {


                            test.append(c2.getString(wordColIndex) + " ");

                        }

                    } while (c2.moveToNext());
                } else

                    c2.close();

                String newtext = test.getText().toString();

                File externalAppDir = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName());
                if (!externalAppDir.exists()) {
                    externalAppDir.mkdir();
                }

                File file = new File(externalAppDir , "Word.txt");
                try {
                    file.createNewFile();
                    FileOutputStream outputStream = new FileOutputStream(file);   // После чего создаем поток для записи
                    outputStream.write(newtext.getBytes());                            // и производим непосредственно запись
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });


        buttonadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, PICK_FILE);

                /*adapter.clear();
                adapter.notifyDataSetChanged();
*/

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




                SqlWrite mt = new SqlWrite();
                mt.execute();


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



    class SqlWrite extends AsyncTask<Void, Integer, Void> {


        String myTxt = txtShow.getText().toString();


        protected void onPreExecute(){


            progressBar = findViewById(R.id.progressbar);
            progressBar.setVisibility(View.VISIBLE);
            pb = findViewById(R.id.pb);

        }


        @Override
        protected Void doInBackground (Void... voids) {


            final SQLiteDatabase db7 = dbHelper.getWritableDatabase();
            db7.delete("words", null, null);

            SQLiteDatabase db = dbHelper.getWritableDatabase();
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
                    publishProgress(++i);
                    String empty = "";


                    count = 0;

                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }


            return null;
        }



        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            pb.setProgress(values[0]);
        }


        protected void onPostExecute(Void list){

            ItemsAdapter adapter = new ItemsAdapter();
            lvOne.setAdapter(adapter);
            pb = findViewById(R.id.pb);
            int cou = 0;



            adapter.clear();
            adapter.notifyDataSetChanged();



            SQLiteDatabase db2 = dbHelper.getReadableDatabase();
            int numRows = (int) DatabaseUtils.queryNumEntries(db2, "words");
            /*pb.setMax(numRows);*/
            db2.rawQuery("SELECT * FROM " + "words" + " ORDER BY " + "count", null);
            Cursor c = db2.query(DatabaseHelper.TABLE, null, null, null, null, null, "count desc");
            if (c.moveToFirst()) {


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

            progressBar = findViewById(R.id.progressbar);
            progressBar.setVisibility(View.GONE);


        }



    }








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
