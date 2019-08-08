package com.ikea.aceh.unicef.sip

import android.Manifest
import android.content.Context
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private val url = "http://61.8.64.242:8080"
    //    private val url = "https://enketo.ona.io/x/#IV5ehp5O"
    private val INPUT_FILE_REQUEST_CODE = 1
    private val FILECHOOSER_RESULTCODE = 1
    private val TAG = "SMILeActivity"
    //    private var webView: WebView? = null
    private var mUploadMessage: ValueCallback<Uri>? = null
    private var mCapturedImageURI: Uri? = null
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private var mCameraPhotoPath: String? = null
    private var mGeolocationRequestoOrigin = null
    private var mGeolocationCallback = null
    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions

    override fun onCreate(savedInstanceState: Bundle?) {
//        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
//        ActivityCompat.requestPermissions(this, permissions,0)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = listOf<String>(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        )


        // Initialize a new instance of ManagePermissions class
        managePermissions = ManagePermissions(this, list, PermissionsRequestCode)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) managePermissions.checkPermissions()


        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true
        webSettings.setGeolocationEnabled(true)
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.allowUniversalAccessFromFileURLs = true

        //settings.pluginState = WebSettings.PluginState.ON
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.mediaPlaybackRequiresUserGesture = false

        // Enable and setup web view cache
        webSettings.setAppCacheEnabled(true)
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings.setAppCachePath(cacheDir.path)
        webSettings.databaseEnabled = true
        webSettings.domStorageEnabled = true
//        webSettings.setGeolocationDatabasePath(filesDir.path);
        // WebView settings
        webView.fitsSystemWindows = true


        /*
            if SDK version is greater of 19 then activate hardware acceleration
            otherwise activate software acceleration
        */
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        webView.webViewClient = Client()
        webView.webChromeClient = ChromeClient()

        webView.loadUrl(url)


//        webView.webViewClient = Client()
//        webView.webChromeClient = ChromeClient()
//
//        val destinationURL : String = getString(R.string.app_url)
//        webView.loadUrl(destinationURL) //change with your website
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data)
                return
            }
            var results: Array<Uri>? = null
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = arrayOf(Uri.parse(mCameraPhotoPath))
                    }
                } else {
                    val dataString = data.dataString
                    if (dataString != null) {
                        results = arrayOf(Uri.parse(dataString))
                    }
                }
            }
            mFilePathCallback!!.onReceiveValue(results)
            mFilePathCallback = null
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data)
                return
            }
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == this.mUploadMessage) {
                    return
                }
                var result: Uri? = null
                try {
                    if (resultCode != Activity.RESULT_OK) {
                        result = null
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = if (data == null) mCapturedImageURI else data.data
                    }
                } catch (e: Exception) {
                    Toast.makeText(applicationContext, "activity :$e",
                            Toast.LENGTH_LONG).show()
                }

                mUploadMessage!!.onReceiveValue(result)
                mUploadMessage = null
            }
        }
        return
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        // Check if the key event was the Back button and if there's history
        if (keyCode == KeyEvent.KEYCODE_BACK && webView?.canGoBack()!!) {
            webView!!.goBack()
            return true
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event)
    }

    inner class Client : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            // If url contains mailto link then open Mail Intent
            if (url.contains("mailto:")) {
                // Could be cleverer and use a regex
                //Open links in new browser
                view.context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                // Here we can open new activity
                return true
            } else if (url.contains("pdf")) {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(browserIntent)
                return true
            } else if (url.startsWith("sms:")) {
                // Handle the sms: link
                val telIntent =  Intent(Intent.ACTION_SENDTO, Uri.parse(url))
                startActivity(telIntent)

                // Return true means, leave the current web view and handle the url itself
                return true
            } else {
                // Stay within this webview and load url
                view.loadUrl(url)
                return true
            }
            return true
        }

        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            // Page loading started
            // Do something
            toast("Page loading.")
        }

        override fun onPageFinished(view: WebView, url: String) {
            // Page loading finished
            // Display the loaded page title in a toast message
            toast("Page loaded: ${view.title}")
        }

        // Receive the permissions request result
        fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                       grantResults: IntArray) {
            when (requestCode) {
                PermissionsRequestCode -> {
                    val isPermissionsGranted = managePermissions
                            .processPermissionsResult(requestCode, permissions, grantResults)

                    if (isPermissionsGranted) {
                        // Do the task now
                        toast("Permission(s) granted.")
                    } else {
                        toast("Permission(s) denied.")
                    }
                    return
                }
            }
        }
    }

    inner class ChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            progress_bar.progress = newProgress
        }

        // For Android 5.0
        override fun onShowFileChooser(view: WebView, filePath: ValueCallback<Array<Uri>>, fileChooserParams: WebChromeClient.FileChooserParams): Boolean {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {
                mFilePathCallback!!.onReceiveValue(null)
            }
            mFilePathCallback = filePath
            var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent!!.resolveActivity(packageManager) != null) {
                // Create the File where the photo should go
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                    takePictureIntent!!.putExtra("PhotoPath", mCameraPhotoPath)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Log.e(TAG, "Unable to create Image File", ex)
                }

                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile!!.absolutePath
                    takePictureIntent!!.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile))
                } else {
                    takePictureIntent = null
                }
            }
            val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
            contentSelectionIntent.type = "image/*"


            var intentArray: Array<Intent>
            if (takePictureIntent != null) {
                intentArray = arrayOf(takePictureIntent)
            } else {
                intentArray = arrayOf(Intent())

            }
            val chooserIntent = Intent(Intent.ACTION_CHOOSER)
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)

            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE)
            return true
        }

        // openFileChooser for Android 3.0+
        @JvmOverloads
        fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String = "") {
            mUploadMessage = uploadMsg
            // Create AndroidExampleFolder at sdcard
            // Create AndroidExampleFolder at sdcard
            val imageStorageDir = File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), "AndroidExampleFolder")
            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs()
            }
            // Create camera captured image file path and name
            val file = File(
                    (imageStorageDir).toString() + File.separator + "IMG_"
                            + System.currentTimeMillis().toString()
                            + ".jpg")
            mCapturedImageURI = Uri.fromFile(file)
            // Camera capture image intent
            val captureIntent = Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI)
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "image/*"
            // Create file chooser intent
            val chooserIntent = Intent.createChooser(i, "Image Chooser")
            // Set camera intent to file chooser
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf<Parcelable>(captureIntent))
            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE)
        }

        fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String, capture: String) {
            openFileChooser(uploadMsg, acceptType)
        }

        override fun onGeolocationPermissionsShowPrompt(origin: String?, callback: GeolocationPermissions.Callback?) {
            super.onGeolocationPermissionsShowPrompt(origin, callback)
            callback?.invoke(origin, true, false)
        }

    }

}

// Extension function to show toast message
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
