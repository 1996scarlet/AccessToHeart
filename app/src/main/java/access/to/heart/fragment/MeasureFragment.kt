package access.to.heart.fragment

import access.to.heart.HeartRateMonitor
import access.to.heart.R
import android.content.Intent
import android.view.View
import android.webkit.WebView
import kotlinx.android.synthetic.main.fragment_measure.*

/**
 * Project AccessToHeart.
 * Created by 旭 on 2017/7/11.
 */
class MeasureFragment : BaseFragment() {
    override fun init() {
        start.setOnClickListener {
            startActivityForResult(Intent(context, HeartRateMonitor::class.java), 10086)
        }

        save.setOnClickListener {
            save.visibility = View.GONE
            start.text = getString(R.string.start_measure)

            postMyHeartRecord()
        }

        mWebView.apply {
            settings.javaScriptEnabled = true
            scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
            loadUrl("file:///android_asset/webCharts.html")
        }
    }

    private fun postMyHeartRecord() {
//        cloudAPI.postHeart(Heart(HeartBeat = )
//        )
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : MyTemplateObserver<String>() {
//                    override fun onError(e: Throwable) {
//                        Toast.makeText(mActivity, "注册成功 正在自动登录", Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onNext(t: String) {
//                        et_userId.error = "该ID已经被注册"
//                    }
//                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) return
        when (resultCode) {
            10086 -> {
                mWebView.loadUrl("javascript:initChart('${data.getStringExtra("heartLine")}')")
                bmp_text.text = "${data.getStringExtra("heartBeats")}-bmp"
                start.text = getString(R.string.drop_and_reclip)
                save.visibility = View.VISIBLE
            }
        }
    }

    override val layoutId: Int
        get() = R.layout.fragment_measure
}