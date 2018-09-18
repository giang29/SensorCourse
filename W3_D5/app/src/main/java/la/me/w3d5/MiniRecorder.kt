package la.me.w3d5

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.util.Log

import java.nio.ByteBuffer

class MiniRecorder : Runnable {
    val bufferSize = BUFFER_SIZE * 10 * 1024
    var currentRate = 0
        private set
    private var audioSource: AudioRecord? = null
    private var audioTrack: AudioTrack? = null

    private var threadSelf: Thread? = null
    var isEnable = false
    private var resume = false
    private val byteBuffer: ByteBuffer

    init {
        byteBuffer = ByteBuffer.allocate(bufferSize)
    }

    fun resume() {
        resume = true
        if (audioSource == null) {
            for (sampleRate in SAMPLE_RATES) {
                val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT)
                audioSource = AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize)

                if (audioSource!!.state == AudioRecord.STATE_INITIALIZED) {
                    currentRate = sampleRate
                    break
                }
            }

            if (audioSource != null && audioSource!!.state == AudioRecord.STATE_INITIALIZED) {
                audioSource!!.startRecording()
            } else {
                Log.e("!msg", "AudioRecord init everything failed")
            }
        }

        if (audioTrack == null) {
            val minBufferSize = AudioTrack.getMinBufferSize(currentRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT)
            audioTrack = AudioTrack(AudioManager.STREAM_VOICE_CALL, currentRate,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize, AudioTrack.MODE_STREAM)

            audioTrack!!.play()
        }

        if (threadSelf == null) {
            threadSelf = Thread(this)
            threadSelf!!.start()
        }
    }

    fun stop() {
        resume = false
    }

    override fun run() {
        val buffer = ByteArray(BUFFER_SIZE)
        while (resume) {
            if (!isEnable) continue
            if (!byteBuffer.hasRemaining()) {
                continue
            }
            val length = audioSource!!.read(buffer, 0, BUFFER_SIZE)
            if (length > 0) {
                if (length < byteBuffer.remaining())
                    byteBuffer.put(buffer, 0, length)

                audioTrack!!.write(buffer, 0, length)
            }
        }
        threadSelf = null
    }

    fun release() {
        audioSource!!.stop()
        audioSource!!.release()

        audioTrack!!.stop()
        audioTrack!!.release()
    }

    fun getBuffer(outBuffer: ByteBuffer) {
        outBuffer.put(byteBuffer.array(), 0, byteBuffer.position())
    }

    fun clearBuffer() {
        byteBuffer.clear()
    }

    companion object {
        private val SAMPLE_RATES = intArrayOf(8000, 11025, 16000, 22050, 44100)
        private val BUFFER_SIZE = 1024
    }
}
