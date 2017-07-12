package access.to.heart

import access.to.heart.Interface.CloudAPI
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Project HealthFlowers.
 * Created by 旭 on 2017/5/16.
 */

abstract class BaseActivity : AppCompatActivity() {

    protected val CLOUD_BASE_URL = "http://139.199.34.237:8990/v1/"
    protected var cloudAPI: CloudAPI = Retrofit.Builder()
            .baseUrl(CLOUD_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(CloudAPI::class.java)

    protected lateinit var rxPermissions: RxPermissions
    protected lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        init()
    }

    protected fun initSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
    }

    protected abstract val layoutId: Int

    protected abstract fun init()
}
