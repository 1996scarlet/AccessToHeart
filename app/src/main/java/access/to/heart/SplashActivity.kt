package access.to.heart

import access.to.heart.Bean.User
import access.to.heart.HTTPAround.MyTemplateObserver
import access.to.heart.Interface.CloudAPI
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class SplashActivity : AppCompatActivity() {

    private var cloudAPI: CloudAPI = Retrofit.Builder()
            .baseUrl("http://139.199.34.237:8990/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(CloudAPI::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        loginMethod(sharedPreferences.getInt("id", 0), sharedPreferences.getString("password", ""))
    }

    private fun loginMethod(id: Int, password: String) {
        cloudAPI.getUser(query = "Id:$id,Password:$password")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MyTemplateObserver<List<User>>() {
                    override fun onError(e: Throwable) {
                        startActivity(Intent(baseContext, LoginActivity::class.java))
                        finish()
                    }

                    override fun onNext(t: List<User>) {
                        startActivity(Intent(baseContext, MainActivity::class.java))
                        finish()
                    }
                })
    }
}
