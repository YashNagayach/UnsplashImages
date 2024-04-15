package com.example.assignmentimageapp.cache

import android.graphics.Bitmap
import android.util.LruCache

interface ImageCache {
    fun getBitmap(url: String): Bitmap?
    fun putBitmap(url: String, bitmap: Bitmap)
}

class BitmapLruCache(maxSize: Int = defaultCacheSize) : LruCache<String, Bitmap>(maxSize),
    ImageCache {

    companion object {
        private  var defaultCacheSize = (Runtime.getRuntime().maxMemory() / 1024 / 8).toInt()
    }

    override fun getBitmap(url: String): Bitmap? {
        return get(url)
    }

    override fun putBitmap(url: String, bitmap: Bitmap) {
        put(url, bitmap)
    }

    override fun sizeOf(key: String, value: Bitmap): Int {
        // Calculate bitmap size in kilobytes
        return value.byteCount / 1024
    }
}
