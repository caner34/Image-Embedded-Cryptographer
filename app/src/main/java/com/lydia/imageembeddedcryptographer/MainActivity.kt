package com.lydia.imageembeddedcryptographer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException


class MainActivity : AppCompatActivity() {


    var permissionUtils: PermissionUtils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hideKeyboard()
        setOnClickActions()
        CommonUtils.Log(""+CipherUtils.isStartsWithStartCodon(ImageUtils.getImageFromImageView(imageView)))


    }

    // sets onClickListeners onCreate
    fun setOnClickActions(){

        buttonDecodeScript.setOnClickListener{
            val crBitmap = ImageUtils.getImageFromImageView(imageView)

            val decodedScript = CipherUtils.decodeScript(crBitmap)
            editText.setText( decodedScript )

            //CommonUtils.Log(""+CipherUtils.isStartsWithStartCodon(ImageUtils.getImageFromImageView(imageView)))
            //Log.d(LOG_TAG, "message "+getListOfChars().joinToString(", "))
        }

        buttonEncodeScript.setOnClickListener{
            val crBitmap = ImageUtils.getImageFromImageView(imageView)
            imageView.setImageBitmap(CipherUtils.encodeScript(crBitmap, editText.text.toString()))
            CommonUtils.Log("hopefully buttonEncodeScript")
            editText.setText("")
        }

        buttonUploadImage.setOnClickListener{
            if (PermissionUtils.isPermissionGranted(applicationContext, this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ImportPhoto()
            } else {
                PermissionUtils.setupPermissions(applicationContext, this@MainActivity)
                CommonUtils.AlertDialogDisplay(this@MainActivity, getString(R.string.permission_check_read_external_storage_permission_is_needed))
            }
        }
    }




    //PHOTO IMPORT OR TAKE
    private fun ImportPhoto() {
        OpenFileDialogAndGetPhotoPath()
    }


    private fun OpenFileDialogAndGetPhotoPath() {
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "Select a file"), 123)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        // To Import Photo
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            val selectedFileUri: Uri? =
                data?.data //The uri with the location of the file
            SetImportedPhotoToImageView(selectedFileUri)
        }

    }



    private fun SetImportedPhotoToImageView(imageUri: Uri?) {
        var bitmap: Bitmap? = null
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (imageUri != null) {
                    ImageDecoder.createSource(this.contentResolver, imageUri)
                }
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        imageView.setImageBitmap(bitmap)
    }


    fun getListOfChars(): ArrayList<Char>{
        var result = arrayListOf<Char>()
        for (i in 33..126){
            val a = i.toChar()
            result.add(a)
        }
        return result
    }

    //hides soft keyboard popup onCreate
    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        // else {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        // }
    }
}

