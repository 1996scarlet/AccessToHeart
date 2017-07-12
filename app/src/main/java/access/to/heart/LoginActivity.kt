package access.to.heart

import access.to.heart.Bean.User
import access.to.heart.HTTPAround.MyTemplateObserver
import android.content.Intent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_user.*

class LoginActivity(override val layoutId: Int = R.layout.fragment_user) : BaseActivity() {
    override fun init() {
        login_button.apply {
            setOnClickListener {
                loginMethod()
            }
        }

        register_button.apply {
            setOnClickListener {
                cloudAPI.postUser(User(Id = et_userId.text.toString().toInt()
                        , Password = et_password.text.toString()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : MyTemplateObserver<String>() {
                            override fun onError(e: Throwable) {
                                loginMethod()
                            }

                            override fun onNext(t: String) {
                                et_userId.error = "该ID已经被注册"
                            }
                        })
            }
        }
    }

    private fun loginMethod() {
        cloudAPI.getUser(query = "Id:${et_userId.text},Password:${et_password.text}")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MyTemplateObserver<List<User>>() {
                    override fun onError(e: Throwable) {
                        et_password.error = "密码错误"
                    }

                    override fun onNext(t: List<User>) {
                        goToMainActivity(t[0])
                    }
                })
    }

    private fun goToMainActivity(user: User) {

        writeToSharePreferences(user)

        startActivity(Intent(this, MainActivity::class.java)
                .putExtra("id", user.Id))
        finish()
    }

    private fun writeToSharePreferences(user: User) {
        initSharedPreferences()
        val edit = sharedPreferences.edit()
        edit.putInt("id", user.Id)
        edit.putString("password", et_password.text.toString())
        edit.apply()
    }
}
