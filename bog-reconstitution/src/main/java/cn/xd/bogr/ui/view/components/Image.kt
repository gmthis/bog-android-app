package cn.xd.bogr.ui.view.components

import android.os.Build
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cn.xd.bogr.R
import cn.xd.bogr.net.entity.Image
import cn.xd.bogr.util.noRippleClickable
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest

@Composable
fun BoxScope.LoadingThumbImage(
    image: Image,
    onClick: (() -> Unit)? = null
) {
    var isLoading by remember {
        mutableStateOf(true)
    }
    var error by remember {
        mutableStateOf(0)
    }

    if (isLoading){
        CircularProgressIndicator(Modifier.align(Alignment.Center))
    }
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(if (error == 0) image.thumbnail() else if (error == 1) image.largePicture() else Image.errorImage)
            .crossfade(true)
            .decoderFactory(
                if (Build.VERSION.SDK_INT >= 28)
                    ImageDecoderDecoder.Factory()
                else
                    GifDecoder.Factory()
            )
            .build(),
        contentDescription = stringResource(id = R.string.con_img),
        onSuccess = {
            isLoading = false
        },
        onError = {
            if (error == 0){
                error = 1
            }else if (error == 1){
                error = 2
            }
        },
        modifier = (onClick?.let { Modifier.noRippleClickable(onClick = it) } ?: Modifier).fillMaxSize()
    )
}

@Composable
fun BoxScope.LoadingLargePicture(
    image: Image
){
    var isLoading by remember {
        mutableStateOf(true)
    }
    var error by remember {
        mutableStateOf(0)
    }

    if (isLoading){
        LoadingThumbImage(image = image)
        CircularProgressIndicator(Modifier.align(Alignment.Center))
    }
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(if (error == 0) image.largePicture() else if (error == 1) image.thumbnail() else Image.errorImage)
            .crossfade(true)
            .decoderFactory(
                if (Build.VERSION.SDK_INT >= 28)
                    ImageDecoderDecoder.Factory()
                else
                    GifDecoder.Factory()
            )
            .build(),
        contentDescription = stringResource(id = R.string.con_img),
        onSuccess = {
            isLoading = false
        },
        onError = {
            if (error == 0){
                error = 1
            }else if (error == 1){
                error = 2
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}