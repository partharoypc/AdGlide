package com.partharoypc.adglide.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageDownloader {
    private static final String TAG = "AdGlide.ImageDownloader";
    private static final ExecutorService executor = Executors.newFixedThreadPool(2);
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface ImageLoaderCallback {
        void onImageLoaded(Bitmap bitmap);

        void onError(Exception e);
    }

    public static void downloadImage(Context context, String urlString, ImageLoaderCallback callback) {
        if (urlString == null || urlString.isEmpty()) {
            mainHandler.post(() -> callback.onError(new IllegalArgumentException("URL is null or empty")));
            return;
        }

        executor.execute(() -> {
            try {
                // Determine persistent cache file name
                MessageDigest digest = MessageDigest.getInstance("MD5");
                digest.update(urlString.getBytes());
                String hash = Base64.encodeToString(digest.digest(), Base64.URL_SAFE | Base64.NO_WRAP);
                File cacheFile = new File(context.getFilesDir(), "adglide_house_" + hash + ".png");

                // Check offline cache first
                if (cacheFile.exists()) {
                    Bitmap offlineBitmap = BitmapFactory.decodeStream(new FileInputStream(cacheFile));
                    if (offlineBitmap != null) {
                        Log.d(TAG, "Loaded House Ad from offline disk cache");
                        mainHandler.post(() -> callback.onImageLoaded(offlineBitmap));
                        return;
                    }
                }

                // If no offline cache, download it
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);

                if (bitmap != null) {
                    // Save to offline cache
                    FileOutputStream fos = new FileOutputStream(cacheFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();

                    mainHandler.post(() -> callback.onImageLoaded(bitmap));
                } else {
                    mainHandler.post(() -> callback.onError(new Exception("Failed to decode image from " + urlString)));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error downloading image: " + e.getMessage());
                mainHandler.post(() -> callback.onError(e));
            }
        });
    }
}
