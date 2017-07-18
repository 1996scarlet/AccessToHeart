package access.to.heart.fragment

import access.to.heart.Bean.Heart
import access.to.heart.Bean.ProfileUser
import access.to.heart.HTTPAround.MyTemplateObserver
import access.to.heart.HeartRateMonitor
import access.to.heart.R
import access.to.heart.utils.GlobalOptions
import android.Manifest
import android.content.Intent
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_measure.*

/**
 * Project AccessToHeart.
 * Created by 旭 on 2017/7/11.
 */
class MeasureFragment : BaseFragment() {

    var heartLine: String = ""
    var heartBeat: String = ""

    private val stateType = arrayOf("休息", "热身", "有氧", "极限")

    override fun init() {

        rxPermissions = RxPermissions(activity)

        start.setOnClickListener {
            rxPermissions
                    .request(Manifest.permission.CAMERA)
                    .subscribe {
                        if (it){
                            startActivityForResult(Intent(context, HeartRateMonitor::class.java), 10086)
                        }
                        else Toast.makeText(context, "没有获得权限", Toast.LENGTH_SHORT).show()
                    }
        }

        save.setOnClickListener {
            save.visibility = View.GONE
            radio_group.visibility = View.GONE
            start.text = getString(R.string.start_measure)

            bmp_text.append("-${stateType[findStateId()]}")

            postMyHeartRecord()
        }

        mWebView.apply {
            settings.javaScriptEnabled = true
            scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
            loadUrl("file:///android_asset/webCharts.html")
        }
    }

    private fun postMyHeartRecord() {
        cloudAPI.postHeart(Heart(HeartBeat = heartBeat.toInt(),
                HeartLine = heartLine,
                HeartState = findStateId(),
                Profile = ProfileUser(Id = GlobalOptions.nowProfileId)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MyTemplateObserver<String>() {
                    override fun onError(e: Throwable) {
                        Toast.makeText(mActivity, "保存成功", Toast.LENGTH_SHORT).show()
                    }

                    override fun onNext(t: String) {
                        Toast.makeText(mActivity, t, Toast.LENGTH_SHORT).show()
                    }
                })
    }

    private fun findStateId(): Int {
        when (radio_group.checkedRadioButtonId) {
            R.id.zero -> return 0
            R.id.one -> return 1
            R.id.two -> return 2
            R.id.three -> return 3
            else -> return 0
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) return
        when (resultCode) {
            10086 -> {
                heartLine = data.getStringExtra("heartLine")
                heartBeat = data.getStringExtra("heartBeats")
                mWebView.loadUrl("javascript:initChart('${heartLine}')")
                bmp_text.text = "${heartBeat}-bmp"
                start.text = getString(R.string.drop_and_reclip)
                save.visibility = View.VISIBLE
                radio_group.visibility = View.VISIBLE
            }
        }
    }

    override val layoutId: Int
        get() = R.layout.fragment_measure
}