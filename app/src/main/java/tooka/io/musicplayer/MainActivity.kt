package tooka.io.musicplayer

import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {
    companion object {
        const val PLAY = 100
        const val PAUSE = 101
        const val STOP = 102
        const val FORWARD = 103
        const val BACKWARD = 104
        const val SEEKCHANGE = 105

    }

    private var isPlaying = false

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction("tooka.io.services.music.player.seek.bar")
        registerReceiver(broadcastReceiver, intentFilter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this@MainActivity, MyService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }


        val anim = ObjectAnimator.ofFloat(imgCover, View.ROTATION, 0f, 360f).setDuration(4000)
        anim.repeatCount = Animation.INFINITE
        anim.interpolator = LinearInterpolator()



        imgPlayPause.setOnClickListener {

            isPlaying = !isPlaying
            if (isPlaying) {
                sendMusicBroadcast(PLAY)
                imgPlayPause.setImageResource(R.drawable.ic_pause_circle_filled)
                if (anim.isPaused) {
                    anim.resume()
                } else {
                    anim.start()
                }
            } else {
                sendMusicBroadcast(PAUSE)
                imgPlayPause.setImageResource(R.drawable.ic_play_circle_filled)
                anim.pause()
            }


        }

        imgRewind.setOnClickListener {
            sendMusicBroadcast(BACKWARD)
        }
        imgForward.setOnClickListener {
            sendMusicBroadcast(FORWARD)

        }
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2) {
                    logi(p1.toString())
                    sendMusicBroadcast(SEEKCHANGE, p1)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        txtExit.setOnClickListener {
            finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        sendMusicBroadcast(STOP)
        val intent = Intent(this@MainActivity, MyService::class.java)
        stopService(intent)
        unregisterReceiver(broadcastReceiver)


    }

    private fun sendMusicBroadcast(state: Int, seekPosition: Int = 0) {
        val intent = Intent()
        intent.action = "tooka.io.services.music.player"
        intent.putExtra("STATE", state)
        intent.putExtra("SEEK_POSITION", seekPosition)
        sendBroadcast(intent)
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1!!.action == "tooka.io.services.music.player.seek.bar") {

                val duration = p1.getIntExtra("DURATION", 0)
                val currentPosition = p1.getIntExtra("CURRENT_POSITION", 0)
                // logi("duration: " + duration.toLong())
                // logi("current position:" + formatDuration(currentPosition.toLong()))
                txtDuration.text = formatDuration(duration.toLong())
                txtCurrentPosition.text = formatDuration(currentPosition.toLong())
                seekbar.progress = currentPosition
                seekbar.max = duration


            }


        }
    }

    private fun formatDuration(duration: Long): String {
        var seconds = duration / 1000
        val minutes = seconds / 60
        seconds %= 60
        return String.format(Locale.ENGLISH, "%02d", minutes) + ":" + String.format(
            Locale.ENGLISH,
            "%02d",
            seconds
        )

    }

    fun rotateClockWise(view: View) {
        val rotate = RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 10000
        rotate.interpolator = LinearInterpolator()
        //rotate.setFillAfter(true);
        rotate.repeatCount = Animation.INFINITE
        rotate.repeatMode = Animation.RESTART
        view.startAnimation(rotate)
    }

}
