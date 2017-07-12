package access.to.heart.fragment

import access.to.heart.Bean.ProfileUser
import access.to.heart.Bean.User
import access.to.heart.HTTPAround.MyStringObserver
import access.to.heart.HTTPAround.MyTemplateObserver
import access.to.heart.R
import android.util.Log
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_profile.*


/**
 * Project AccessToHeart.
 * Created by 旭 on 2017/7/12.
 */
class ProfileFragment : BaseFragment() {
    override fun init() {
//        spinner.apply {
//            adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, )
//        }

        add.setOnClickListener {
            cloudAPI.postProfile(ProfileUser(ProfileName = "",
                    User = User(Id = arguments["id"] as Int)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : MyStringObserver() {
                        override fun onNext(t: String) {
                            super.onNext(t)
                        }

                        override fun onError(e: Throwable) {
                            Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show()
                        }
                    })
        }

        getProfileFromApi()
    }

    private fun getProfileFromApi() {
        cloudAPI.getProfile(query = "User.Id:${arguments.getInt("id")}")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MyTemplateObserver<List<ProfileUser>>() {
                    override fun onNext(t: List<ProfileUser>) {
                        super.onNext(t)
                        Log.d("tag", t[0].ProfileName)
                    }
                })
    }

    override val layoutId: Int
        get() = R.layout.fragment_profile
}