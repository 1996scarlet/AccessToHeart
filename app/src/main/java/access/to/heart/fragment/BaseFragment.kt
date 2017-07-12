package access.to.heart.fragment

import access.to.heart.Bean.User
import access.to.heart.Interface.CloudAPI
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tbruyelle.rxpermissions2.RxPermissions
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Project AccessToHeart.
 * Created by 旭 on 2017/5/15.
 */

abstract class BaseFragment : Fragment() {

    /**
     * 说明：返回当前View

     * @return view
     */

    protected val CLOUD_BASE_URL = "http://139.199.34.237:8990/v1/"
    protected lateinit var contentView: View
    protected lateinit var mActivity: Activity

    protected lateinit var rxPermissions: RxPermissions
    protected var nowUser: User = User()

    protected var cloudAPI: CloudAPI = Retrofit.Builder()
            .baseUrl(CLOUD_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(CloudAPI::class.java)

    /**
     * 说明：在此处保存全局的Context

     * @param context 上下文
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contentView = inflater.inflate(layoutId, container, false)
        return contentView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    /**
     * @return 返回该Fragment的layout id
     */
    protected abstract val layoutId: Int


    /**
     * 说明：创建视图时的初始化操作均写在该方法
     */
    protected abstract fun init()


    /**
     * 获取控件对象

     * @param id 控件id
     * *
     * @return 控件对象
     */
    fun findViewById(id: Int): View = contentView.findViewById(id)
}
