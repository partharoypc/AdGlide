package com.partharoypc.adglide.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import com.partharoypc.adglide.util.AdGlideLog;

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
                // Determine persistent cache file name using standard hex MD5
                MessageDigest digest = MessageDigest.getInstance("MD5");
                byte[] bytes = digest.digest(urlString.getBytes());
                StringBuilder sb = new StringBuilder();
                for (byte b : bytes) {
                    sb.append(String.format("%02x", b));
                }
                String hash = sb.toString();
                File cacheFile = new File(context.getFilesDir(), "adglide_house_" + hash + ".png");

                // Check offline cache first
                if (cacheFile.exists()) {
                    try (FileInputStream fis = new FileInputStream(cacheFile)) {
                        Bitmap offlineBitmap = BitmapFactory.decodeStream(fis);
                        if (offlineBitmap != null) {
                            AdGlideLog.d(TAG, "Loaded House Ad from offline disk cache");
                            mainHandler.post(() -> callback.onImageLoaded(offlineBitmap));
                            return;
                        }
                    } catch (Exception e) {
                        AdGlideLog.e(TAG, "Error reading cache: " + e.getMessage());
                        cacheFile.delete(); // Delete corrupt cache file
                    }
                }

                // If no offline cache, download it
                AdGlideLog.d(TAG, "Downloading image from: " + urlString);
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                
                // Add common headers to avoid being blocked by CDNs/Servers
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 10; Mobile) AdGlideSDK/1.0");
                connection.setInstanceFollowRedirects(true);
                connection.setDoInput(true);
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw new Exception("Server returned HTTP " + responseCode + " for " + urlString);
                }

                try (InputStream input = connection.getInputStream()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(input);

                    if (bitmap != null) {
                        // Save to offline cache
                        try (FileOutputStream fos = new FileOutputStream(cacheFile)) {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.flush();
                        }
                        
                        AdGlideLog.d(TAG, "Successfully downloaded and cached image: " + urlString);
                        mainHandler.post(() -> callback.onImageLoaded(bitmap));
                    } else {
                        throw new Exception("Failed to decode image from " + urlString);
                    }
                } finally {
                    connection.disconnect();
                }
            } catch (Exception e) {
                AdGlideLog.e(TAG, "Error downloading image: " + e.getMessage());
                mainHandler.post(() -> callback.onError(e));
            }
        });
    }

}
