package access.to.heart.fragment

import access.to.heart.Bean.User
import access.to.heart.HTTPAround.MyTemplateObserver
import access.to.heart.R
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_user.*

/**
 * Project AccessToHeart.
 * Created by 旭 on 2017/7/12.
 */
class UserFragment : BaseFragment() {

    override fun init() {
        login_button.apply {
            setOnClickListener {
                cloudAPI.getUser(query = "Id:${et_userId.text},Password:${et_password.text}")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : MyTemplateObserver<List<User>>() {
                            override fun onError(e: Throwable) {
                                et_password.error = "密码错误"
                            }

                            override fun onNext(t: List<User>) {
                                if (t.isNotEmpty()) {
                                    nowUser = t[0]
                                }
                            }
                        })


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
                                Toast.makeText(mActivity, "注册成功 正在自动登录", Toast.LENGTH_SHORT).show()
                            }

                            override fun onNext(t: String) {
                                et_userId.error = "该ID已经被注册"
                            }
                        })
            }
        }

    }

    override val layoutId: Int
        get() = R.layout.fragment_user
}