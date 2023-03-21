package com.shehzad.memeshare;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    ProgressBar progressBar;
    ImageView imageView, shareMeme;
    String memeUrl = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        shareMeme = findViewById(R.id.shareMeme);

        loadMeme();

        Toast.makeText(this, "Touch the Meme for next Meme", Toast.LENGTH_LONG).show();

        shareMeme.setOnClickListener(view -> {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Hey, checkout this cool meme.... \n" + memeUrl);
            Intent chooser = Intent.createChooser(intent, "Share this meme using...");

            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(chooser);
            }
        });

       imageView.setOnTouchListener((v, event) -> {

           if(event.getAction() == MotionEvent.ACTION_DOWN) loadMeme();
           else if(event.getAction() == MotionEvent.ACTION_UP) loadMeme();
           return false;
       });
    }

    private void loadMeme() {
        progressBar = findViewById(R.id.prograssBar);
        progressBar.setVisibility(View.VISIBLE);
        final TextView textView = (TextView) findViewById(R.id.text);

        // Instantiate the RequestQueue.
        String url = "https://reddit.com/r/memes/random.json";
        //String url = "https://meme-api.com/gimme";

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        //memeUrl = response.getString("url");
                        JSONObject jsonObject = new JSONObject(String.valueOf(response));
                        JSONArray arrData = jsonObject.getJSONArray("data");
                        JSONObject objPostData = arrData.getJSONObject(0).getJSONObject("data");
                        memeUrl = objPostData.getString("url");
                        Log.d("Meme", "loadMeme: "+ memeUrl);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Glide.with(MainActivity.this).load(memeUrl).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.VISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    }).into(imageView);
                }, error -> {
            Toast.makeText(MainActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
        });


        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

}