package com.example.dublicate;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TwoActivity extends AppCompatActivity {


    private AdView mAdView;

    Button update;
    TextView txtShow, translation1;
    DatabaseHelper dbHelper;

    class Item{
        String name;
        int price;
        Item(String name, int price){

            this.name = name;
            this.price = price;
        }
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        txtShow = findViewById(R.id.txtShow);
        translation1 = findViewById(R.id.translation1);
        update = findViewById(R.id.update);
        dbHelper = new DatabaseHelper(this);

        mAdView = findViewById(R.id.adView2);
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

        final ListView lvOne2 = findViewById(R.id.items2);
        lvOne2.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        final ItemsAdapter adapter2 = new ItemsAdapter();
        lvOne2.setAdapter(adapter2);

        lvOne2.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                db.rawQuery("SELECT * FROM " + "words" + " ORDER BY "+ "count", null);
                Cursor c = db.query(DatabaseHelper.TABLE, null, null, null, null, null, "count desc" );
                if (c.moveToFirst()) {



                    int wordColIndex = c.getColumnIndex("word");
                    int countColIndex = c.getColumnIndex("count");

                    do {
                        if(c.getInt(countColIndex) <= 5) {
                            adapter2.add(new Item(c.getString(wordColIndex), c.getInt(countColIndex)));

                        }
                    } while (c.moveToNext());
                } else

                    c.close();

                adapter2.notifyDataSetChanged();
                db.close();
            }
        });


        lvOne2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> adapterView, View view, int position, long l) {

                SQLiteDatabase db2 = dbHelper.getReadableDatabase();
                db2.rawQuery("SELECT * FROM " + "words" + " ORDER BY "+ "count", null);
                Cursor c2 = db2.query(DatabaseHelper.TABLE, null, null, null, null, null,"count desc" );
                int i = 0;
                int numRows = (int) DatabaseUtils.queryNumEntries(db2, "words");
                final String[] array3 = new String[numRows];
                if (c2.moveToFirst()) {


                    int idColIndex = c2.getColumnIndex("id");
                    int wordColIndex = c2.getColumnIndex("word");
                    int countColIndex = c2.getColumnIndex("count");

                    do {
                        String empty = "";
                        if(c2.getString(wordColIndex).equals(empty)){

                        }
                        else if(c2.getInt(countColIndex) <= 5) {
                            array3[i] = c2.getString(wordColIndex);
                            i++;
                        }

                    } while (c2.moveToNext());
                } else

                    c2.close();

                final String selectedItem = array3[position];

                db2.close();
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

    public class ItemsAdapter extends ArrayAdapter<Item> {
        public ItemsAdapter() { super(TwoActivity.this, R.layout.item); }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view = getLayoutInflater().inflate(R.layout.item, null);
            final Item item = getItem(position);
            ((TextView) view.findViewById(R.id.name)).setText(item.name);
            ((TextView) view.findViewById(R.id.price)).setText(String.valueOf(item.price));

            return view;
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

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String date = bundle.getString("Key");
            translation1.setText(date);
        }
    };



}
