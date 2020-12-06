package com.chydee.notekeeper.ui.voice

import android.Manifest
import android.animation.Animator
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.chydee.notekeeper.R
import com.chydee.notekeeper.databinding.RecordNoteSheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class RecordNoteBottomSheet : BottomSheetDialogFragment() {

    private lateinit var listener: OnClickListener
    private lateinit var binding: RecordNoteSheetLayoutBinding

    private lateinit var lottieView: LottieAnimationView

    private var pauseOffset: Long = 0
    private var running = false

    companion object {
        fun instanceOfThis(listener: OnClickListener) =
                RecordNoteBottomSheet()
                        .apply {
                            this.listener = listener
                        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = RecordNoteSheetLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLottie()
        setupChronometer()
        binding.playPauseCircle.setOnCheckedChangeListener { _, isChecked ->
            if (ContextCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(requireContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                val permissions = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                ActivityCompat.requestPermissions(requireActivity(), permissions, 0)
            } else {
                onRecord(isChecked)
            }
        }


        /* binding.stopRecordingBtn.setOnClickListener {
             stopRecordingAnimation()
             listener.onStopRecording()
         }*/
    }

    private fun setupChronometer() {
        binding.durationCounter.apply {
            format = "Duration: %s"
            base = SystemClock.elapsedRealtime()
        }
    }

    private fun onRecord(start: Boolean) = if (start) {
        startRecording()
    } else {
        pauseRecording()
    }

    interface OnClickListener {
        fun onStartRecording()
        fun onPauseRecording()
        fun onStopRecording()
    }

    private fun startRecording() {
        listener.onStartRecording()
        lottieView.playAnimation()
        binding.durationCounter.apply {
            base = SystemClock.elapsedRealtime() - pauseOffset
            start()
        }
        running = true
    }

    private fun stopRecording() {
        listener.onStopRecording()
        lottieView.pauseAnimation()
        binding.durationCounter.apply {
            base = SystemClock.elapsedRealtime() - this.base
            stop()
        }
        running = true
    }

    private fun pauseRecording() {
        listener.onPauseRecording()
        lottieView.pauseAnimation()
        if (running) {
            binding.durationCounter.apply {
                stop()
                pauseOffset = SystemClock.elapsedRealtime() - this.base
            }
            running = false
        }
    }

    fun resetChronometer(v: View?) {
        binding.durationCounter.base = SystemClock.elapsedRealtime()
        pauseOffset = 0
    }

    private fun setupLottie() {
        lottieView = binding.recordingAnimation
        lottieView.setAnimation(R.raw.recording)
        lottieView.setSafeMode(true)
        lottieView.repeatCount = LottieDrawable.INFINITE
        lottieView.pauseAnimation()
        lottieView.addAnimatorListener(object :
                Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                Log.e("Animation:", "started")
            }

            override fun onAnimationEnd(animation: Animator) {
                Log.e("Animation:", "ended")

            }

            override fun onAnimationCancel(animation: Animator) {
                Log.e("Animation:", "cancel")
            }

            override fun onAnimationRepeat(animation: Animator) {
                Log.e("Animation:", "repeat")
            }
        })
    }

    override fun onStop() {
        super.onStop()
        lottieView.pauseAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
        lottieView.pauseAnimation()
    }

}