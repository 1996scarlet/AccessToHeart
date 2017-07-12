package access.to.heart.fragment

import access.to.heart.Bean.Heart
import access.to.heart.HTTPAround.MyTemplateObserver
import access.to.heart.R
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.jcodecraeer.xrecyclerview.XRecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_history.*

/**
 * Project AccessToHeart.
 * Created by 旭 on 2017/7/12.
 */
class HistoryFragment : BaseFragment() {
    override fun init() {

        recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            //adapter = infoAdapter
            setLoadingListener(object : XRecyclerView.LoadingListener {
                override fun onRefresh() {
                    //initData()
                    CheckUser()
                    recycler_view.refreshComplete()
                }

                override fun onLoadMore() {
                    //updateData()
                    recycler_view.loadMoreComplete()
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        CheckUser()
        cloudAPI.getHeart(query = "Profile.User.Id:${nowUser.Id}")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MyTemplateObserver<List<Heart>>() {
                    override fun onNext(t: List<Heart>) {
                        Log.i("TAG", t[0].HeartLine)
                    }
                })
    }


    private fun CheckUser() {
        if (nowUser.Id == 0) {
            userId.text = "请先登录然后刷新"
        } else {
            userId.visibility = View.GONE
        }
    }

    override val layoutId: Int
        get() = R.layout.fragment_history
}