package access.to.heart

import access.to.heart.utils.ImageProcessing
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.hardware.Camera
import android.hardware.Camera.PreviewCallback
import android.os.Bundle
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.util.Log
import android.view.SurfaceHolder
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_measure.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Project AccessToHeart.
 * Created by 旭 on 2017/5/15.
 */
class HeartRateMonitor : Activity() {

    enum class TYPE {
        GREEN, RED
    }

    private val TAG = "HeartRateMonitor"
    private val processing = AtomicBoolean(false)
    private var mToast: Toast? = null

    private var previewHolder: SurfaceHolder? = null
    private var camera: Camera? = null

    private lateinit var wakeLock: WakeLock

    private var averageIndex = 0
    private val averageArraySize = 4
    private val averageArray = IntArray(averageArraySize)

    var current = TYPE.GREEN

    private var beatsIndex = 0
    private val beatsArraySize = 3
    private val beatsArray = IntArray(beatsArraySize)
    private var beats = 0.0
    private var startTime: Long = 0

    private var heartLine: StringBuilder = StringBuilder()

    /**
     * {@inheritDoc}
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measure)

        previewHolder = preview.holder
        previewHolder!!.addCallback(surfaceCallback)
        previewHolder!!.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen")
    }

    /**
     * {@inheritDoc}
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    /**
     * {@inheritDoc}
     */
    public override fun onResume() {
        super.onResume()

        wakeLock.acquire()

        camera = Camera.open()

        startTime = System.currentTimeMillis()
    }

    /**
     * {@inheritDoc}
     */
    public override fun onPause() {
        super.onPause()

        wakeLock.release()

        camera!!.setPreviewCallback(null)
        camera!!.stopPreview()
        camera!!.release()
        camera = null
    }

    private fun showTextToast(msg: String) {
        if (mToast == null) {
            mToast = Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
        }
        mToast!!.show()
    }

    private val previewCallback = PreviewCallback { data, cam ->
        /**
         * {@inheritDoc}
         */
        if (data == null) throw NullPointerException()
        val size = cam.parameters.previewSize ?: throw NullPointerException()

        if (!processing.compareAndSet(false, true)) return@PreviewCallback

        val width = size.width
        val height = size.height

        val imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width)
//        Log.i(TAG, "imgAvg=" + imgAvg);
        if (imgAvg == 255 || imgAvg < 150) {
            showTextToast("请把手指指尖放在摄像头上")
            processing.set(false)
            return@PreviewCallback
        }

//        if (imgAvg < 150) return@PreviewCallback

        var averageArrayAvg = 0
        var averageArrayCnt = 0
        for (i in averageArray.indices) {
            if (averageArray[i] > 0) {
                averageArrayAvg += averageArray[i]
                averageArrayCnt++
            }
        }

        val rollingAverage = if (averageArrayCnt > 0) averageArrayAvg / averageArrayCnt else 0
        var newType = current
        if (imgAvg < rollingAverage) {
            newType = TYPE.RED
            if (newType != current) {
                beats++
                heartLine.append(0, 1, 2, 1)
//                Log.d(TAG, "BEAT!! beats="+beats);
            }
        } else if (imgAvg > rollingAverage) {
            newType = TYPE.GREEN
            heartLine.append(1)
        }

        if (averageIndex == averageArraySize) averageIndex = 0
        averageArray[averageIndex] = imgAvg
        averageIndex++

        // Transitioned from one state to another to the same
        if (newType != current) current = newType

        val endTime = System.currentTimeMillis()
        val totalTimeInSecs = (endTime - startTime) / 1000.0
        if (totalTimeInSecs >= 10) {
            val bps = beats / totalTimeInSecs
            val dpm = (bps * 60.0).toInt()
            if (dpm < 45 || dpm > 145) {
                startTime = System.currentTimeMillis()
                heartLine = StringBuilder()
                beats = 0.0
                processing.set(false)
                return@PreviewCallback
            }

            Log.d(TAG, "totalTimeInSecs=$totalTimeInSecs beats=$beats")
            Log.d(TAG, heartLine.toString())

            if (beatsIndex == beatsArraySize) beatsIndex = 0
            beatsArray[beatsIndex] = dpm
            beatsIndex++

            var beatsArrayAvg = 0
            var beatsArrayCnt = 0
            for (i in beatsArray.indices) {
                if (beatsArray[i] > 0) {
                    beatsArrayAvg += beatsArray[i]
                    beatsArrayCnt++
                }
            }
            val beatsAvg = beatsArrayAvg / beatsArrayCnt
            measure_start.text = beatsAvg.toString()
            setResult(10086, Intent()
                    .putExtra("heartBeats", beatsAvg.toString())
                    .putExtra("heartLine", heartLine.toString()))
            startTime = System.currentTimeMillis()
            heartLine = StringBuilder()
            beats = 0.0

            finish()
        }
        processing.set(false)
    }

    private val surfaceCallback = object : SurfaceHolder.Callback {

        /**
         * {@inheritDoc}
         */
        override fun surfaceCreated(holder: SurfaceHolder) {
            try {
                camera!!.setPreviewDisplay(previewHolder)
                camera!!.setPreviewCallback(previewCallback)
            } catch (t: Throwable) {
                Log.e("PreviewDemo", "Exception in setPreviewDisplay()", t)
            }

        }

        /**
         * {@inheritDoc}
         */
        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            val parameters = camera!!.parameters
            parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
            val size = getSmallestPreviewSize(width, height, parameters)
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height)
                Log.d(TAG, "Using width=" + size.width + " height=" + size.height)
            }
            camera!!.parameters = parameters
            camera!!.startPreview()
        }

        /**
         * {@inheritDoc}
         */
        override fun surfaceDestroyed(holder: SurfaceHolder) {
            // Ignore
        }
    }

    private fun getSmallestPreviewSize(width: Int, height: Int, parameters: Camera.Parameters): Camera.Size? {
        var result: Camera.Size? = null

        for (size in parameters.supportedPreviewSizes) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size
                } else {
                    val resultArea = result.width * result.height
                    val newArea = size.width * size.height

                    if (newArea < resultArea) result = size
                }
            }
        }

        return result
    }
}
