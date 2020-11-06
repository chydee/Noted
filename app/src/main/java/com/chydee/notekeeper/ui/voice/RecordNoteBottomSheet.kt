package com.chydee.notekeeper.ui.voice

import android.animation.Animator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.chydee.notekeeper.R
import com.chydee.notekeeper.databinding.RecordNoteSheetLayoutBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class RecordNoteBottomSheet : BottomSheetDialogFragment() {

    private lateinit var listener: OnClickListener
    private lateinit var binding: RecordNoteSheetLayoutBinding

    private lateinit var lottieView: LottieAnimationView

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
        binding.playPauseCircle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startRecordingAnimation()
                listener.onStartRecording()
            } else {
                stopRecordingAnimation()
                listener.onPauseRecording()
            }
        }


        binding.stopRecordingBtn.setOnClickListener {
            stopRecordingAnimation()
            listener.onStopRecording()
        }
    }

    interface OnClickListener {
        fun onStartRecording()
        fun onPauseRecording()
        fun onStopRecording()
    }

    private fun startRecordingAnimation() {
        lottieView.playAnimation()
    }

    private fun stopRecordingAnimation() {
        lottieView.pauseAnimation()
    }

    private fun setupLottie() {
        lottieView = binding.recordingAnimation
        lottieView.setAnimation(R.raw.recording_animation)
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
        stopRecordingAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecordingAnimation()
    }

}