package com.chydee.notekeeper.ui.voice

import android.animation.Animator
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.chydee.notekeeper.R
import com.chydee.notekeeper.databinding.RecordNoteSheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber

/**
 *  @author: Desmond Ngwuta
 *  @email: desmondchidi311@gmail.com
 */
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = RecordNoteSheetLayoutBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLottie()
        setupChronometer()
        onClickEvents()
    }

    /**
     *  Handle OnClickEvents by setting OnClickListener or OnCheckedChangeListener
     */
    private fun onClickEvents() {
        binding.playPauseCircle.setOnCheckedChangeListener { _, isChecked ->
            onRecord(isChecked)
        }

        binding.stopRecording.setOnClickListener {
            stopRecording()
            listener.onStopRecording()
            dismiss()
        }
    }

    /**
     *  Set up Chronometer
     */
    private fun setupChronometer() {
        binding.durationCounter.apply {
            format = "Duration: %s"
            base = SystemClock.elapsedRealtime()
        }
    }

    /**
     *  Starts recording if
     *  @param start is true and pauses Recording if it is false
     */
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

    /**
     *  Start Recording
     *  Play Lottie Animation
     */
    private fun startRecording() {
        listener.onStartRecording()
        lottieView.playAnimation()
        binding.durationCounter.apply {
            base = SystemClock.elapsedRealtime() - pauseOffset
            start()
        }
        running = true
    }

    /**
     *  Stop Recording
     *  Pause Lottie Animation
     */
    private fun stopRecording() {
        listener.onStopRecording()
        lottieView.pauseAnimation()
        binding.durationCounter.apply {
            base = SystemClock.elapsedRealtime() - this.base
            stop()
        }
        running = true
        resetChronometer()
    }

    /**
     *  Pause Recording
     *  Pause Lottie Animation
     */
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

    /**
     * Resets Chronometer when called
     */
    private fun resetChronometer() {
        binding.durationCounter.base = SystemClock.elapsedRealtime()
        pauseOffset = 0
    }

    /**
     * Set up lottie animation
     */
    private fun setupLottie() {
        lottieView = binding.recordingAnimation
        lottieView.setAnimation(R.raw.recording)
        lottieView.setSafeMode(true)
        lottieView.repeatCount = LottieDrawable.INFINITE
        lottieView.pauseAnimation()
        lottieView.addAnimatorListener(object :
            Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                Timber.tag("Animation:").e("started")
            }

            override fun onAnimationEnd(animation: Animator) {
                Timber.tag("Animation:").e("ended")
            }

            override fun onAnimationCancel(animation: Animator) {
                Timber.tag("Animation:").e("cancel")
            }

            override fun onAnimationRepeat(animation: Animator) {
                Timber.tag("Animation:").e("repeat")
            }
        })
    }

    override fun onStop() {
        super.onStop()
        // Pause Lottie Animation when BottomSheetDialogFragment is stopped
        lottieView.pauseAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Pause Lottie Animation when BottomSheetDialogFragment is destroyed
        lottieView.pauseAnimation()
    }
}
