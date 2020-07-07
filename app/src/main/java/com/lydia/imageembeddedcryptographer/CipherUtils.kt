package com.lydia.imageembeddedcryptographer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.pow

class CipherUtils {


    companion object {



        // provides a character capacity of its cube, in this case 125
        val mod_constant = 5
        val start_char_int_constant = 33
        val end_char_int_constant = 126
        val startCodon = "5n#"
        val stopCodon = "+a$"

        val no_result = "no result"

        public fun isStartsWithStartCodon(bitmap: Bitmap): Boolean {
            var result = true

            var count = 0
            for (j in 0 until bitmap.height){
                for (i in 0 until bitmap.width){
                    if (count== startCodon.length)
                        break
                    val pixel = bitmap.getPixel(i,j)
                    val crChar = convertPixelColorToInt(pixel).toChar()
                    if(crChar != startCodon[count].toChar())
                    {
                        return false
                    }
                    count++
                }
            }

            return result
        }

        
        public fun decodeScript(bitmap: Bitmap): String {
            var result = ""

            var count = 0
            for (j in 0 until bitmap.height){
                for (i in 0 until bitmap.width){

                    if (result.length == 3 && !result.equals(startCodon))
                        return no_result

                    if (result.endsWith(stopCodon))
                        return result.substring(startCodon.length, result.length - stopCodon.length)

                    val pixel = bitmap.getPixel(i,j)
                    CommonUtils.Log("i, j: "+i+", "+j+"   convertPixelColorToInt(pixel).toChar(): "+(convertPixelColorToInt(pixel)- start_char_int_constant).toChar())
                    result += (convertPixelColorToInt(pixel)- start_char_int_constant).toChar()

                    count++
                }
            }

            return no_result
        }

        public fun encodeScript(bitmap: Bitmap, script: String): Bitmap {

            try
            {
                // Create a bitmap of the same size
                val bitmapCopy = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888)
                // Create a canvas  for new bitmap
                val c = Canvas(bitmapCopy)
                // Draw your old bitmap on it.
                c.drawBitmap(bitmap, 0.0f, 0.0f, Paint())



                val newScript = startCodon + script + stopCodon

                CommonUtils.Log("script: "+script+"   ***   newScript: "+newScript)

                //if there is no capacity to encode on the image, the image is not large enough return null
                if (bitmapCopy.width * bitmapCopy.height < newScript.length)
                //TODO display an alert dialog
                    return bitmap


                var count = 0
                for (j in 0 until bitmapCopy.height){
                    for (i in 0 until bitmapCopy.width){
                        //When encoding completed return the result
                        if (count == newScript.length)
                            return bitmapCopy

                        CommonUtils.Log("step 3 i: "+i+",  j: "+j)
                        val pixel = bitmapCopy.getPixel(i,j)
                        val crCharInt = convertPixelColorToInt(pixel) - start_char_int_constant
                        var wantedCharInt = newScript.get(count).toInt()

                        CommonUtils.Log("step 3b: wantedChar: "+newScript.get(count)+ ",   wantedCharInt: " +wantedCharInt)
                        // if wanted char int out of scope replace it with a single gap character

                        if (wantedCharInt < start_char_int_constant || wantedCharInt > end_char_int_constant)
                        {
                            wantedCharInt = " ".get(0).toInt()
                        }

                        var colorInt = getSuitableColor(crCharInt, wantedCharInt, pixel)
                        CommonUtils.Log("step 5 colorInt: "+colorInt)
                        bitmapCopy.setPixel(i,j, colorInt)

                        count++
                    }
                }

                return bitmapCopy
            }
            catch (e: Exception) {

                CommonUtils.Log("exception: "+e.message)
                return null as Bitmap
            }

        }

        private fun getSuitableColor(crCharInt: Int, wantedCharInt: Int, pixel: Int): Int {
            var r = Color.red(pixel)
            var g = Color.green(pixel)
            var b = Color.blue(pixel)

            //calculates needed mod values for color components
            var remainingWantedCharInt = wantedCharInt
            CommonUtils.Log("\n\nremainingWantedCharInt a: "+remainingWantedCharInt)
            val needed_r_mod = remainingWantedCharInt / (mod_constant.toDouble().pow(2)).toInt()
            remainingWantedCharInt -= (needed_r_mod * mod_constant.toDouble().pow(2)).toInt()
            CommonUtils.Log("remainingWantedCharInt b: "+remainingWantedCharInt)
            val needed_g_mod = remainingWantedCharInt / (mod_constant.toDouble().pow(1)).toInt()
            remainingWantedCharInt -= (needed_g_mod * mod_constant.toDouble().pow(1)).toInt()
            CommonUtils.Log("remainingWantedCharInt c: "+remainingWantedCharInt)
            val needed_b_mod = remainingWantedCharInt


            CommonUtils.Log("step 5d original  r: "+r+",  g: "+g+",  b: "+b)
            CommonUtils.Log("step 5d needed mod  r: "+needed_r_mod+",  g: "+needed_g_mod+",  b: "+needed_b_mod)

            r -= r % mod_constant - needed_r_mod
            g -= g % mod_constant - needed_g_mod
            b -= b % mod_constant - needed_b_mod


            if (r < 0)
                r += mod_constant
            if (g < 0)
                g += mod_constant
            if (b < 0)
                b += mod_constant

            CommonUtils.Log("step 5e arranged r: "+r+",  g: "+g+",  b: "+b)
            CommonUtils.Log("step 5f  getIntFromRGB: " + (getIntFromRGB(r,g,b)) )

            return Color.rgb(r,g,b)
            //return getIntFromRGB(r,g,b)
        }

        fun getIntFromRGB(r: Int, g: Int, b: Int): Int
        {
            var rgb: Int = r
            rgb = (rgb shl 8) + g
            rgb = (rgb shl 8) + b
            return rgb
        }


        private fun convertPixelColorToInt(pixel: Int):Int{
            val resultD = (Color.blue(pixel)%mod_constant) * mod_constant.toDouble().pow(0) + (Color.green(pixel)%mod_constant) * mod_constant.toDouble().pow(1) + (Color.red(pixel)%mod_constant) * mod_constant.toDouble().pow(2)
            return resultD.toInt() + start_char_int_constant
        }

    }

}

