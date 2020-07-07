package com.lydia.imageembeddedcryptographer

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


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

