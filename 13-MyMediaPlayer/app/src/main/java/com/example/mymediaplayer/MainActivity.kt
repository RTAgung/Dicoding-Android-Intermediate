package com.example.mymediaplayer

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.mymediaplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val seekbarProgressCycle = 200

    private var boundStatus = false
    private lateinit var player: MusicService

    private var isReady: Boolean = false
    private var runnable: Runnable? = null
    private var handler: Handler = Handler(Looper.getMainLooper())

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            boundStatus = false
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val myBinder = service as MusicService.LocalBinder
            player = myBinder.getService
            boundStatus = true

            getServiceData()
        }
    }

    private fun getServiceData() {
        player.isReady.observe(this) {
            isReady = it
            if (isReady) {
                val sec = (player.mediaPlayer?.duration ?: 0) / 1000
                val min = sec / 60
                binding.seekBar.max = (sec * 1000) / seekbarProgressCycle
                binding.tvDue.text =
                    "${String.format("%02d", min)}:${String.format("%02d", sec % 60)}"
                binding.seekBar.setOnSeekBarChangeListener(object :
                    SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                        if (b) player.seekMedia(i * seekbarProgressCycle)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                    }
                })

                initSeekBar()
            } else {
                if (runnable != null) handler.removeCallbacks(runnable!!)
            }
        }
        player.isPlaying.observe(this) {
            changeButtonState(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.seekBar.layout(0,0,0,0)

        val permission = android.Manifest.permission.POST_NOTIFICATIONS
        val requestCode = 1

        ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)

        initMediaService()

        binding.btnPlay.setOnClickListener {
            if (isReady) {
                player.playOrPauseMedia()
            }
        }
        binding.btnPause.setOnClickListener {
            if (isReady) {
                player.playOrPauseMedia()
            }
        }
        binding.btnStop.setOnClickListener {
            player.stopMedia()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (boundStatus) {
            unbindService(connection)
            boundStatus = false
            player.stopForeground(Service.STOP_FOREGROUND_DETACH)
            player.stopSelf()
        }
    }

    override fun onResume() {
        super.onResume()
        if (runnable != null) handler.removeCallbacks(runnable!!)
        if (isReady) {
            initSeekBar()
        }
    }

    override fun onStop() {
        super.onStop()
        if (runnable != null) handler.removeCallbacks(runnable!!)
    }

    private fun changeButtonState(isPlaying: Boolean) {
        if (isPlaying) {
            binding.btnPlay.isEnabled = false
            binding.btnPause.isEnabled = true
            binding.btnStop.isEnabled = true
        } else {
            binding.btnPlay.isEnabled = true
            binding.btnPause.isEnabled = false
            binding.btnStop.isEnabled = false
        }
    }

    private fun initMediaService() {
        val mediaIntent = Intent(this, MusicService::class.java)
        mediaIntent.putExtra(MusicService.EXTRA_MEDIA, R.raw.yoasobi)
        startService(mediaIntent)
        bindService(mediaIntent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun initSeekBar() {
        runnable = Runnable {
            Log.d("TAG", "initSeekBar: ${player.mediaPlayer?.currentPosition}")
            binding.seekBar.progress =
                (player.mediaPlayer?.currentPosition ?: 0) / seekbarProgressCycle
            if (runnable != null) handler.postDelayed(
                runnable!!, (seekbarProgressCycle / 2).toLong()
            )
        }
        if (runnable != null) handler.postDelayed(runnable!!, (seekbarProgressCycle / 2).toLong())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, you can show notifications
        } else {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            val requestCode = 1

            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }
}