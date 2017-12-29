package com.bg.car;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

/**
 * Created by BinGe on 2017/12/29.
 */

public class ImageCache {

    private static ImageCache cache;

    public static ImageCache share() {
        if (cache == null) {
            cache = new ImageCache();
        }
        return cache;
    }

    private LruCache<String, Bitmap> lruCache;

    private ImageCache() {
        int MaxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);// kB
        int cacheSize = MaxMemory / 8;
        lruCache = new LruCache<String, Bitmap>(cacheSize) {
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;// KB
            }
        };
    }

    public Bitmap get(String key) {
        if (key == null) {
            return null;
        }
        return lruCache.get(key);
    }

    public void put(String key, Bitmap bitmap) {
        if (key == null || bitmap == null) {
            return;
        }
        lruCache.put(key, bitmap);
    }

}
