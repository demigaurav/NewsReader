package com.example.newsreader;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ImageView image;
    String res;
    TextView title;
    TextView country;
    TextView descrip;
    JSONArray array;
    Button prevButton;
    Button nextButton;
    int ind=0;
    int lim;
    public void prev(View view){
        if(ind==lim-1){
            nextButton.setEnabled(true);
        }

        if(ind==1){
            prevButton.setEnabled(false);
        }

        ind--;
        Update(ind);
    }

    public void next(View view){
        if(ind==0){
            prevButton.setEnabled(true);
        }

        if(ind==lim-2){
            nextButton.setEnabled(false);
        }
        ind++;
        Update(ind);
    }

    public void open(View view){
        Intent news=new Intent(getApplicationContext(),news.class);
        news.putExtra("url", getUrl(array,ind));
        startActivity(news);
    }

    public String getTitle(JSONArray array,int i){
        JSONObject article=array.optJSONObject(i);
        String title=article.optString("title");
        return  title;
    }

    public String getDesc(JSONArray array,int i){
        JSONObject article=array.optJSONObject(i);
        String desc=article.optString("description");
        return  desc;
    }

    public String getImage(JSONArray array,int i){
        JSONObject article=array.optJSONObject(i);
        String imgUrl=article.optString("image_url");
        return  imgUrl;
    }

    public String getUrl(JSONArray array,int i){
        JSONObject article=array.optJSONObject(i);
        String url=article.optString("link");
        return  url;
    }

    public String getCountry(JSONArray array,int i){
        JSONObject article=array.optJSONObject(i);
        JSONArray coun;
        coun=article.optJSONArray("country");
        String country=coun.optString(0);



        return  country.toUpperCase(Locale.ROOT);
    }

    public void Update(int i){
        title.setText(getTitle(array,i));
        descrip.setText(getDesc(array,i));
        country.setText(getCountry(array,i));
        String imgurl=getImage(array,i);
        ImageDown ima= new ImageDown();
        Bitmap imgDownld=null;
        try {
            imgDownld=ima.execute(imgurl).get();
            image.setImageBitmap(imgDownld);
        }
        catch (Exception e){
            Log.i("Error","Failed");
            e.printStackTrace();
        }
    }


    public class json extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url=new URL(strings[0]);
                HttpURLConnection urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.connect();
                InputStream input=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(input);
                int i= reader.read();
                String result="";
                while(i!=-1) {
                    char temp = (char) i;
                    result += temp;
                    i= reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        }


    }

    public class ImageDown extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap image = BitmapFactory.decodeStream(input);
                return image;


            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String url = "https://newsdata.io/api/1/news?&apikey=pub_8308e7e257fe46728a2a8e7fb90227cc4611&language=en";
        res = "";
        json result = new json();
        try {
            res = result.execute(url).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(res);
            array=jsonObject.optJSONArray("results");
        } catch (Exception e) {
            e.printStackTrace();
        }

        lim=array.length();
        title=findViewById(R.id.textView10);
        country=findViewById(R.id.textView);
        descrip=findViewById(R.id.textView11);
        image=findViewById(R.id.imageView);
        prevButton=findViewById(R.id.button);
        nextButton=findViewById(R.id.button2);
        prevButton.setEnabled(false);
        Update(0);




    }


}