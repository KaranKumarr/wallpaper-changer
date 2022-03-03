package com.example.wallpaperchanger;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            ApplicationInfo applicationInfo = getApplicationContext().getPackageManager().getApplicationInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            String api_key = bundle.getString("keyValue");

            loadWallpaper(api_key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void loadWallpaper(String api_key){

        String url = "https://api.unsplash.com/photos/random?orientation=portrait&client_id="+api_key;

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,url,null,
                response -> {
                    try{
                    JSONObject jsonObject = response.getJSONObject("urls");
                    String imageURL = jsonObject.getString("full");

                        ImageView wallpaperView = findViewById(R.id.wallpaper);
                        Glide.with(this).load(imageURL).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);

                                return false;
                            }
                        }).into(wallpaperView);

                    }catch(Error | JSONException e){
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("Exception","error when fetching wallpaper; "+error);
                    Toast.makeText(MainActivity.this,"Something went wrong, try again",Toast.LENGTH_LONG);
                }
                );

        // Add the request to the RequestQueue.
        new VolleySingleton(this).addToRequestQueue(jsonRequest);
    }

    public void nextWallpaper(View view) {

        try {
            ApplicationInfo applicationInfo = getApplicationContext().getPackageManager().getApplicationInfo(getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            String api_key = bundle.getString("keyValue");
            loadWallpaper(api_key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setWallpaper(View view) throws IOException {


//        To Save Image
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        ImageView wallpaper = findViewById(R.id.wallpaper);

        BitmapDrawable draw = (BitmapDrawable) wallpaper.getDrawable();
        Bitmap bitmap = draw.getBitmap();

            try {
                wallpaperManager.setBitmap(bitmap);
                Toast.makeText(MainActivity.this,"Wallpaper changed",Toast.LENGTH_LONG);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    public void searchWallpaper(View view) {
    }
}