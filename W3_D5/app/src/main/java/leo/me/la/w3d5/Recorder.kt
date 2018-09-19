package leo.me.la.w3d5

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log

class Recorder(private val listener: AudioDataReceivedListener) {

    private var shouldContinue: Boolean = false
    private var thread: Thread? = null

    fun recording(): Boolean {
        return thread != null
    }

    fun startRecording() {
        if (thread != null)
            return

        shouldContinue = true
        thread = Thread(Runnable { record() })
        thread!!.start()
    }

    fun stopRecording() {
        if (thread == null)
            return

        shouldContinue = false
        thread = null
    }

    private fun record() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO)

        // buffer size in bytes
        var bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT)

        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
            bufferSize = SAMPLE_RATE * 2
        }

        val audioBuffer = ShortArray(bufferSize / 2)

        val record = AudioRecord(MediaRecorder.AudioSource.DEFAULT,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize)

        if (record.state != AudioRecord.STATE_INITIALIZED) {
            return
        }
        record.startRecording()


        var shortsRead: Long = 0
        while (shouldContinue) {
            val numberOfShort = record.read(audioBuffer, 0, audioBuffer.size)
            shortsRead += numberOfShort.toLong()

            // Notify waveform
            listener(audioBuffer)
        }

        record.stop()
        record.release()
    }
}
