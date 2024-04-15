package com.example.assignmentimageapp.screen

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.assignmentimageapp.ImageRepository
import com.example.assignmentimageapp.R
import com.example.assignmentimageapp.Utils
import com.example.assignmentimageapp.cache.BitmapLruCache
import com.example.assignmentimageapp.network.NetworkResponse
import com.example.assignmentimageapp.viewModel.ImageViewModel
import com.example.assignmentimageapp.viewModel.ImageViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.URL


@Composable
fun MainScreen(
    modifier: Modifier = Modifier, bitmapLruCache: BitmapLruCache
) {
    val repository = ImageRepository()
    val viewModel: ImageViewModel = viewModel(factory = ImageViewModelFactory(repository))

    val context = LocalContext.current


    LaunchedEffect(key1 = true) {
        if (Utils.isInternetAvailable(context)) {
            viewModel.getImage()
        }
    }

    val imageState = viewModel.imageLoadingState.collectAsState()

    val lazyListState = rememberLazyGridState()
    val nextloading = viewModel.nextloading.collectAsState()

    val fetchNextpage: Boolean by remember {
        derivedStateOf {
            val itemCount = imageState.value?.data?.size ?: return@derivedStateOf false
            val lastVisibleItem = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: return@derivedStateOf false
            return@derivedStateOf lastVisibleItem >= itemCount - 5
        }
    }

    LaunchedEffect(fetchNextpage) {
        if (fetchNextpage) {
            viewModel.getImage()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        contentAlignment = Alignment.Center
    ) {
        when (imageState.value) {
            is NetworkResponse.Loading -> {
                if (Utils.isInternetAvailable(context)) {
                    CircularProgressIndicator()
                } else {
                    Utils.showNetworkAlertDialog()
                }
            }

            is NetworkResponse.Success -> {
                val placeHolder = BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.image_placeholder
                ).asImageBitmap()
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LazyVerticalGrid(
                        state = lazyListState,
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        itemsIndexed(imageState.value?.data ?: emptyList()) { index, item ->
                            LoadAndCacheImage(
                                url = item.urls.small,
                                bitmapLruCache = bitmapLruCache,
                                placeHolder=placeHolder
                            )
                        }
                    }
                }
            }

            else -> {
                Utils.ShowAlertDialog(title = "Error", message = imageState.value?.error.toString())
            }
        }
        if (nextloading.value) {
            CircularProgressIndicator(
                modifier
                    .padding(40.dp)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun LoadAndCacheImage(
    url: String?,
    bitmapLruCache: BitmapLruCache,
    placeHolder: ImageBitmap
) {
    val bitmapState = remember(url) {
        mutableStateOf<ImageBitmap?>(null)
    }

    LaunchedEffect(url) {
        val cachedBitmap = bitmapLruCache.get(url)
        if (cachedBitmap != null) {
            bitmapState.value = cachedBitmap.asImageBitmap()
            return@LaunchedEffect
        }

        var inputStream: InputStream? = null
        try {
            inputStream = withContext(Dispatchers.IO) {
                URL(url).openStream()
            }
            withContext(Dispatchers.IO) {
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    bitmapLruCache.put(url, bitmap)
                    bitmapState.value = bitmap.asImageBitmap()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            withContext(Dispatchers.IO) { inputStream?.close() }
        }
    }


    val imageBitmap = bitmapState.value ?: placeHolder

    Image(
        bitmap = imageBitmap,
        contentDescription = "Image",
        modifier = Modifier
            .height(250.dp)
            .padding(10.dp),
        contentScale = ContentScale.Crop
    )
}