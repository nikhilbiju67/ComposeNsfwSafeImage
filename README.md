# ComposeNsfwSafeImage

`ComposeNsfwSafeImage` is an Android Jetpack Compose library that provides a composable for displaying images safely by detecting NSFW (Not Safe For Work) content. The library leverages `NSFWHelper` to check the safety of images before displaying them.

## Installation

Add the JitPack repository to your `settings.gradle` file:
## DEMO
## DEMO
![ComposeNsfwSafeImage Demo](https://i.ibb.co/3BJcGGW/ezgif-7-a20967c60b.gif)
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

Then, add the dependency to your build.gradle file:

implementation("com.github.nikhilbiju67:ComposeNsfwSafeImage:1.0.7")

Usage
Initialize the NSFW Helper
You need to initialize the NSFWHelper in your Application class:

```kotlin

import android.app.Application
import com.github.nikhilbiju67.composensfwsafeimage.NsfWBlocker

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NsfWBlocker.initNSFW(this)
    }
}

import androidx.compose.runtime.Composable
import com.github.nikhilbiju67.composensfwsafeimage.SafeImage

@Composable
fun MyScreen() {
    SafeImage(
        imageUrl = "https://example.com/image.jpg",
        skipSafety = false,
        showImageOnClick = true,
        onClickImage = {
            // Handle image click
        },
        UnSafeView = {
            // Customize unsafe view
        },
        SafeView = {
            // Customize safe view
        }
    )
}

Parameters
imageUrl: The URL of the image to be loaded.
skipSafety: If true, the image will be displayed without safety checks.
showImageOnClick: If true, the image will be shown when clicked, even if marked unsafe.
onClickImage: Callback when the image is clicked.
loadingView: Composable to show while the image is loading.
UnSafeView: Custom composable for unsafe images.
SafeView: Custom composable for safe images.
