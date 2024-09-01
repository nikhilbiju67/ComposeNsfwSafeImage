package com.github.nikhilbiju67.composensfwsafeimage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.github.nikhilbiju67.composensfwsafeimage.NsfWBlocker.isNSFW
import io.github.devzwy.nsfw.NSFWHelper

object NsfWBlocker {
    private const val CONFIDENCE_THRESHOLD: Float = 0.7F

    fun initNSFW(applicationContext: Context) {
        NSFWHelper.openDebugLog()
        NSFWHelper.initHelper(
            context = applicationContext,
            isOpenGPU = false,
        )

    }

    fun isNSFW(
        bitmap: Bitmap?,
        confidenceThreshold: Float = CONFIDENCE_THRESHOLD,
    ): Boolean {
        if (bitmap == null) {
            return false
        }
        val score = NSFWHelper.getNSFWScore(bitmap).nsfwScore
        return score > confidenceThreshold

    }

}

@Composable
fun SafeImage(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    skipSafety: Boolean = false,
    showImageOnClick: Boolean = false,
    onClickImage: (SafeImageData) -> Unit = {},
    loadingView: @Composable () -> Unit = { ImageLoader(modifier) },
    UnSafeView: (@Composable (SafeImageData) -> Unit)? = null,
    SafeView: @Composable (SafeImageData) -> Unit = {
        SafeView(it, modifier, onClickImage = onClickImage)
    }
) {
    var imageBitmap by remember { mutableStateOf(SafeImageData(imageUrl = "")) }
    var imageLoading by remember { mutableStateOf(false) }
    var isImageSafe by remember { mutableStateOf(false) }

    val context = LocalContext.current

    suspend fun loadAndCheckImageSafety(url: String) {
        imageLoading = true
        try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false)
                .build()

            val result = (loader.execute(request) as? SuccessResult)?.drawable
            val bitmap = (result as? BitmapDrawable)?.bitmap
            val isSafe = bitmap?.let { !isNSFW(it) } ?: false

            imageBitmap = SafeImageData(imageUrl = url, bitmap = bitmap, isSafe = isSafe)
            safeImageMap[url] = imageBitmap
            isImageSafe = isSafe
        } finally {
            imageLoading = false
        }
    }

    LaunchedEffect(imageUrl) {
        imageUrl?.let {
            if (!safeImageMap.containsKey(it)) {
                loadAndCheckImageSafety(it)
            } else {
                imageBitmap = safeImageMap[it]!!
                isImageSafe = imageBitmap.isSafe
            }
        }
    }
    LaunchedEffect(skipSafety) {
        if (skipSafety) {
            isImageSafe = true
        }

    }

    when {
        imageLoading -> loadingView()
        isImageSafe || skipSafety -> SafeView(imageBitmap)
        else -> UnSafeView?.invoke(imageBitmap) ?: UnsafeView(imageBitmap, onSafeClick = {
            onClickImage(imageBitmap)
            if (showImageOnClick) {
                isImageSafe = it
            }
        })
    }
}

@Composable
private fun SafeView(
    imageBitmap: SafeImageData,
    modifier: Modifier,
    onClickImage: (SafeImageData) -> Unit
) {
    imageBitmap.bitmap?.asImageBitmap()?.let {
        Image(
            bitmap = it,
            contentDescription = "Image",
            contentScale = ContentScale.FillWidth,
            modifier = modifier
                .clickable { onClickImage(imageBitmap) }
        )
    }
}

@Composable
private fun UnsafeView(
    imageBitmap: SafeImageData,
    onSafeClick: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .clickable {
                safeImageMap[imageBitmap.imageUrl] =
                    safeImageMap[imageBitmap.imageUrl]?.copy(isSafe = true) ?: return@clickable
                onSafeClick(true)
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Unsafe Image",
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = "Unsafe Image",
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ImageLoader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
    )
}

data class SafeImageData(
    val imageUrl: String,
    val bitmap: Bitmap? = null,
    val isSafe: Boolean = false,
    val loading: Boolean = false
)

var safeImageMap: MutableMap<String, SafeImageData> = mutableMapOf()

