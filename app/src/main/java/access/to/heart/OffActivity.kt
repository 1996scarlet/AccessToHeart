package access.to.heart

import android.Manifest
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.webkit.WebView
import android.widget.Toast
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_off.*

class OffActivity(override val layoutId: Int = R.layout.activity_off) : BaseActivity() {

    override fun init() {
        rxPermissions = RxPermissions(this)

        start.setOnClickListener {
            rxPermissions
                    .request(Manifest.permission.CAMERA)
                    .subscribe {
                        if (it) {
                            Toast.makeText(this, "请把手指指尖放在摄像头上", Toast.LENGTH_SHORT).show()
                            startActivityForResult(Intent(this, HeartRateMonitor::class.java), 10086)
                        } else Toast.makeText(this, "没有获得权限", Toast.LENGTH_SHORT).show()
                    }
        }

        mWebView.apply {
            settings.javaScriptEnabled = true
            scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
            loadUrl("file:///android_asset/webCharts.html")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) return
        when (resultCode) {
            10086 -> {
                mWebView.loadUrl("javascript:initChart('${data.getStringExtra("heartLine")}')")
                bmp_text.text = "${data.getStringExtra("heartBeats")}-bmp"

                AlertDialog.Builder(this)
                        .setMessage("注册/登录来享受更多功能").setTitle("加入我们")
                        .setPositiveButton("现在注册/登录", { _, _ -> finish() }).show()
            }
        }
    }

}
