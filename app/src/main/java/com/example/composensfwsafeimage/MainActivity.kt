package com.example.composensfwsafeimage

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composensfwsafeimage.ui.theme.ComposeNsfwSafeImageTheme
import com.github.nikhilbiju67.composensfwsafeimage.SafeImage
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeNsfwSafeImageTheme {
                var list = listOf(
                    "https://example.com/image1.jpg",
                )
                var modelSheet = rememberModalBottomSheetState()
                val scope = rememberCoroutineScope()
                var skipsafety by remember { mutableStateOf(false) }
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Column(
                        Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        list.forEach {
                            SafeImage(
                                skipSafety = skipsafety, showImageOnClick = false, UnSafeView = {
                                    Column(
                                        modifier = Modifier
                                            .verticalScroll(rememberScrollState())
                                            .fillMaxWidth()
                                            .height(200.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(Icons.Filled.Warning, contentDescription = "Warning")
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text("This image is not safe for work",
                                            modifier = Modifier.clickable {
                                                scope.launch {
                                                    modelSheet.show()
                                                }
                                                Log.d("MainActivity", "Text clicked")
                                            })
                                    }
                                },
//                            onClickImage = {
//
//                                skipsafety = !skipsafety
//                                Log.d("MainActivity", "Image clicked")
//                            },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp), imageUrl = it
                            )
                        }

                    }
                    if (modelSheet.isVisible) ModalBottomSheet(onDismissRequest = { /*TODO*/ }) {
                        ImageWarningBottomSheet {
                            skipsafety = !skipsafety
                            scope.launch {
                                modelSheet.hide()
                            }
                        }

                    }

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Composable
fun ImageWarningBottomSheet(
    onClickShowImage: () -> Unit
) {
    Column(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("This image is not safe for work Do you want to show it?")
        Spacer(modifier = Modifier.height(16.dp))
        ElevatedButton(onClick = {
            onClickShowImage()
        }) {
            Text(text = "Show Image")

        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeNsfwSafeImageTheme {
        Greeting("Android")
    }
}