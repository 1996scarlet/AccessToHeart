package access.to.heart

import access.to.heart.utils.GlobalOptions
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_person.*

class PersonActivity(override val layoutId: Int = R.layout.activity_person) : BaseActivity() {
    override fun init() {
        mWebView.apply {
            settings.javaScriptEnabled = true
            scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
            settings.allowUniversalAccessFromFileURLs = true
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    initData()
                }
            }
            loadUrl("file:///android_asset/personCharts.html")
        }
    }

    private fun initData() {
        val target = "http://139.199.34.237:8990/v1/heart/?query=Profile.Id:${GlobalOptions.nowProfileId}&fields=HeartState&limit=-1"
        mWebView.loadUrl("javascript:initChart('$target')")
    }
}
