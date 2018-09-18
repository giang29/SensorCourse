package la.me.w3d5

import java.io.DataOutputStream
import java.io.IOException
import java.nio.ByteBuffer

object WaveHeaderWriter {
    @Throws(IOException::class)
    fun rawToWave(numberOfChannels: Int, sampleRate: Int, bitPerSample: Int,
                  rawBuffer: ByteBuffer, outputStream: DataOutputStream) {

        // WAVE header
        writeString(outputStream, "RIFF") // chunk id
        writeInt(outputStream, 36 + rawBuffer.position()) // chunk size
        writeString(outputStream, "WAVE") // format
        writeString(outputStream, "fmt ") // subchunk 1 id
        writeInt(outputStream, 16) // subchunk 1 size
        writeShort(outputStream, 1.toShort()) // audio format (1 = PCM)
        writeShort(outputStream, numberOfChannels.toShort()) // number of channels
        writeInt(outputStream, sampleRate) // sample rate
        writeInt(outputStream, numberOfChannels * sampleRate * bitPerSample / 8) // byte rate = {number of channels} * {sample rate} * {bits per sample} / 8
        writeShort(outputStream, 2.toShort()) // block align
        writeShort(outputStream, bitPerSample.toShort()) // bits per sample
        writeString(outputStream, "data") // subchunk 2 id
        writeInt(outputStream, rawBuffer.position()) // subchunk 2 size

        outputStream.write(rawBuffer.array(), 0, rawBuffer.position())

    }

    @Throws(IOException::class)
    private fun writeInt(output: DataOutputStream, value: Int) {
        output.write(value)
        output.write(value shr 8)
        output.write(value shr 16)
        output.write(value shr 24)
    }

    @Throws(IOException::class)
    private fun writeShort(output: DataOutputStream, value: Short) {
        output.write(value.toInt())
        output.write(value.toInt() shr 8)
    }

    @Throws(IOException::class)
    private fun writeString(output: DataOutputStream, value: String) {
        for (i in 0 until value.length) {
            output.write(value[i].toInt())
        }
    }
}
