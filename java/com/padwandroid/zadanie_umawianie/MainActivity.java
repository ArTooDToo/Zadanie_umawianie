package com.padwandroid.zadanie_umawianie;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends Activity {

    @Bind(R.id.lista) ListView lv;

    ArrayList<String> arrayList = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private int pageCount = 0;
    private final static String TAG = MainActivity.class.getSimpleName();
    public String TAG_1 = "Wczytywanie dni: ";
    public String TAG_2 = "Wczytywanie godzin: ";
    int posss;
    String[]  pliki = {"dni.txt", "dni2.txt"};
    String sciezka = pliki[0];
    int licznik_plikow = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        setFAdapter(arrayList);

    }
    // wczytanie następnego pliku
    public String NextFile(){
        licznik_plikow = 1;
        sciezka = pliki[1];
        return sciezka;
    }

    private void setFAdapter(List<String> response){
        try{
            lv.setOnScrollListener(onScrollListener());
            LoadMoreData();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lv.setAdapter(adapter);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String fullName = lv.getItemAtPosition(position).toString();
                            Toast.makeText(getApplicationContext(), fullName , Toast.LENGTH_SHORT).show();
                        }

                    });
                }
            });


        }catch (Exception e){

        }
    }

    //wczytywanie więcej danych przy przewijaniu
    private AbsListView.OnScrollListener onScrollListener(){
        return new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                int threshold = 1;
                int count = lv.getCount();
                int lastpostition = lv.getLastVisiblePosition();


                if (scrollState == SCROLL_STATE_IDLE) {
                    if (lv.getLastVisiblePosition() >= count - threshold && pageCount < 2) {
                        Log.i(TAG, "Wczytywanie więcej danych");
                        if(licznik_plikow < 1){
                            NextFile();
                            LoadMoreData();
                            adapter.notifyDataSetChanged();
                            lv.invalidateViews();
                            lv.smoothScrollToPosition(lastpostition+1);
                        }else{
                            Toast.makeText(getApplicationContext(), "Nie ma więcej wolnych terminów",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                }



            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        };
    }


    //Wczytaj tablice z JSON
    public void LoadMoreData(){

        try{
            //Odczyt pliku json
            JSONObject obj = new JSONObject(loadJSON());
            JSONObject jsonObject = (JSONObject) obj;
            //odczyt głównej tablicy json
            JSONArray dni = (JSONArray) jsonObject.get("days");

            for(int i=0; i<dni.length(); i++){
                JSONObject dni_inside = dni.getJSONObject(i);
                Log.i(TAG_1, dni_inside.getString("day"));
                arrayList.add("Dzień " + dni_inside.getString("day"));
                //Wyswietlenie godzin
                JSONArray godz = (JSONArray)dni_inside.get("hours");
                for (int j=0;j<godz.length();j++){
                    JSONObject godz_inside = godz.getJSONObject(j);
                    Log.i(TAG_2, godz_inside.getString("h"));
                    arrayList.add(godz_inside.getString("h"));

                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }


    }

    //pobierz plik JSON z pliku
    public String loadJSON(){
        String json = null;
        try{
            InputStream is = getAssets().open(sciezka);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        }catch (IOException e ){
            e.printStackTrace();
            return null;
        }

        return json;
    }
}








