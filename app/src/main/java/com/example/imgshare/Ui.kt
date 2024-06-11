package com.example.imgshare

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoUi(
    imgUrl: String,
    vm: DemoViewModel = viewModel()
) {

    val shareLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    val error = vm.error.collectAsState()


    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Share Image using Intents") },
            actions = {
                IconButton(onClick = {
                    vm.shareImage(imgUrl, shareLauncher)
                }) {
                    Icon(Icons.Default.Share, "Share Icon")
                }
            }
        )
    }) {
        Box(
            Modifier.padding(it),
        ) {
            NetworkImage(
                url = imgUrl,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 20.dp)
                    .fillMaxSize()

            )
            error.value?.let { error ->
                CustomSnackbar(
                    message = error,
                    onDismiss = { vm.resetError() },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

        }
    }
}

@Composable
fun NetworkImage(
    modifier: Modifier = Modifier,
    url: String,
    placeholder: Int = R.drawable.img_placeholder,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = FilterQuality.Medium,
    contentDescription: String? = null,
    enableCrossFade: Boolean = true
) {

    val context = LocalContext.current

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(url)
            .crossfade(enableCrossFade)
            .build(),
        placeholder = painterResource(id = placeholder),
        contentDescription = contentDescription,
        contentScale = contentScale,
        filterQuality = filterQuality,
        modifier = modifier
    )
}

@Composable
fun CustomSnackbar(message: String, onDismiss: () -> Unit, modifier: Modifier) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = visible) {
        if (visible) {
            delay(3000) // Show for 3 seconds
            onDismiss()
        }
    }

    if (visible) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(Color.Red)
                .padding(16.dp)
        ) {
            Text(
                text = message,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}