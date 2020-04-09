package tooka.io.musicplayer

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import tooka.io.musicplayer.MainActivity
import java.lang.Exception
import java.util.*


class MyService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    var myUri = Uri.parse("https://dls.music-fa.com/tagdl/downloads/Aron%20Afshar%20-%20Gisoo%20Parishan%20(128).mp3")
    private var timer: Timer?=null

    override fun onCreate() {
        super.onCreate()
        val intentFilter = IntentFilter()
        intentFilter.addAction("tooka.io.services.music.player")
        registerReceiver(broadcastReceiver, intentFilter)
        startMusic()

    }


    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        //mediaPlayer.start()
        return START_NOT_STICKY
    }


    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer?.stop()
        timer?.cancel()
        unregisterReceiver(broadcastReceiver)
    }

    private fun startMusic() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer?.setDataSource(this, myUri)
        mediaPlayer?.prepare()
        mediaPlayer?.seekTo(mediaPlayer!!.currentPosition)
        mediaPlayer?.setOnCompletionListener {
            if (it.currentPosition != 0) {
                it.seekTo(0)
            }

        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1!!.action == "tooka.io.services.music.player") {

                val state = p1.getIntExtra("STATE", 0)

                if (state == MainActivity.PLAY) {
                    if (mediaPlayer == null) {
                        startMusic()
                    }
                    mediaPlayer?.start()
                    startTimer()
                } else if (state == MainActivity.PAUSE) {
                    mediaPlayer?.pause()
                } else if (state == MainActivity.STOP) {
                    timer?.cancel()
                    if (mediaPlayer?.currentPosition != 0) {
                        mediaPlayer?.seekTo(0)
                    }

                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
                }else if (state==MainActivity.FORWARD){
                    val cPos=mediaPlayer?.currentPosition
                    mediaPlayer?.seekTo(cPos!!+5000)
                } else if (state == MainActivity.BACKWARD) {
                    val cPos = mediaPlayer?.currentPosition
                    mediaPlayer?.seekTo(cPos!! - 5000)
                }else if (state==MainActivity.SEEKCHANGE){
                   val seekPosition=p1.getIntExtra("SEEK_POSITION",0)
                    mediaPlayer?.seekTo(seekPosition)
                }


            }


        }
    }

    private fun sendDurationBroadcast(duration: Int=0, position: Int = 0) {
        val intent = Intent()
        intent.action = "tooka.io.services.music.player.seek.bar"
        intent.putExtra("DURATION", duration)
        intent.putExtra("CURRENT_POSITION", position)
        sendBroadcast(intent)
    }

    private fun startTimer() {
        timer = Timer()
        timer?.schedule(MyTimerTask(), 0, 1000)
    }


    inner class MyTimerTask : TimerTask() {
        override fun run() {
            val duration = mediaPlayer?.duration
            val currentPosition = mediaPlayer?.currentPosition
            try {
                sendDurationBroadcast(duration!!, currentPosition!!)
            }catch (e:Exception){e.printStackTrace()}

        }

    }

}