package leo.me.la.w3d5

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack

import java.nio.ShortBuffer



internal const val SAMPLE_RATE = 44100

class Player(sample: ShortArray) {

    private var thread: Thread? = null
    private var shouldContinue: Boolean = false
    private val sample = ShortBuffer.wrap(sample)
    private val numSample = sample.size
    fun playing(): Boolean {
        return thread != null
    }

    fun startPlayback() {
        if (thread != null)
            return

        // Start streaming in a thread
        shouldContinue = true
        thread = Thread(Runnable { play() })
        thread!!.start()
    }

    fun stopPlayback() {
        if (thread == null)
            return

        shouldContinue = false
        thread = null
    }

    private fun play() {
        var bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT)
        if (bufferSize == AudioTrack.ERROR || bufferSize == AudioTrack.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2
        }

        val audioTrack = AudioTrack(
            AudioManager.STREAM_MUSIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize,
            AudioTrack.MODE_STREAM
        )

        audioTrack.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
            override fun onPeriodicNotification(track: AudioTrack) {}

            override fun onMarkerReached(track: AudioTrack) {
                track.release()
            }
        })
        audioTrack.positionNotificationPeriod = SAMPLE_RATE / 30 // 30 times per second
        audioTrack.notificationMarkerPosition = numSample

        audioTrack.play()


        val buffer = ShortArray(bufferSize)
        sample.rewind()
        val limit = numSample
        var totalWritten = 0
        while (sample.position() < limit && shouldContinue) {
            val numSamplesLeft = limit - sample.position()
            val samplesToWrite: Int
            if (numSamplesLeft >= buffer.size) {
                sample.get(buffer)
                samplesToWrite = buffer.size
            } else {
                for (i in numSamplesLeft until buffer.size) {
                    buffer[i] = 0
                }
                sample.get(buffer, 0, numSamplesLeft)
                samplesToWrite = numSamplesLeft
            }
            totalWritten += samplesToWrite
            audioTrack.write(buffer, 0, samplesToWrite)
        }

        if (!shouldContinue) {
            audioTrack.release()
        }

    }
}
