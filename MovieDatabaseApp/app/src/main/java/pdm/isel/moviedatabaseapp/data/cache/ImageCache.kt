package pdm.isel.moviedatabaseapp.data.cache

import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.toolbox.ImageLoader

class ImageCache : ImageLoader.ImageCache {
    private val MAX_SIZE = 20
    private val cache : LruCache<String,Bitmap> = LruCache(MAX_SIZE)

    override fun getBitmap(url: String?): Bitmap? = cache.get(url)

    override fun putBitmap(url: String, bitmap: Bitmap) {
            cache.put(url,bitmap)
    }
}