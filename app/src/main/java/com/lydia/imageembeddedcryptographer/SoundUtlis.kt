package com.lydia.imageembeddedcryptographer

import android.content.Context
import android.media.MediaPlayer

class SoundUtlis {

    companion object{

        public enum class SOUNDS(val sourceID: Int){
            CLICK(R.raw.menu_selection),
            ENCODE(R.raw.swoosh_brighter),
            DECODE(R.raw.buttonchime02up)

        }

        fun playSound(soundSourceID: Int, context: Context){
            val mediaPlayer = MediaPlayer.create(context, soundSourceID)
            mediaPlayer.setVolume(1.0f, 1.0f)
            mediaPlayer.start()
        }

    }

}