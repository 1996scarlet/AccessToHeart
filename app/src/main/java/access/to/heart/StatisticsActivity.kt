package access.to.heart

import access.to.heart.HTTPAround.MyTemplateObserver
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_statistics.*

class StatisticsActivity(override val layoutId: Int = R.layout.activity_statistics) : BaseActivity() {
    override fun init() {

        val des = intent.getStringExtra("heartText")
        heart_text.text = des

        val beats = des.split('-')[0].toInt()

        mWebView.apply {
            settings.javaScriptEnabled = true
            scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
            loadUrl("file:///android_asset/webCharts.html")

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    loadUrl("javascript:initChart('${intent.getStringExtra("heartLine")}')")
                }
            }
        }

        delete.setOnClickListener {
            cloudAPI.deleteHeartById(intent.getIntExtra("heartId", 0))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : MyTemplateObserver<String>() {
                        override fun onNext(t: String) {
                            Toast.makeText(baseContext, "删除成功 请刷新列表", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    })
        }

        analyze.apply {
            if (beats < 60) {
                text = "脉搏过低 建议适当运动并补充碳水化合物"
            } else if (beats > 100) {
                text = "脉搏过高 建议在内心毫无波动的情况下测量"
            } else {
                text = "脉搏正常 请保持健康的生活习惯"
            }

            append("\n\n\n${resources.getText(R.string.about)}")
        }
    }
}
