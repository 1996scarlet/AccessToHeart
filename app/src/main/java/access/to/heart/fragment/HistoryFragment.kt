package access.to.heart.fragment

import access.to.heart.Adapter.BaseAdapter
import access.to.heart.Bean.Heart
import access.to.heart.HTTPAround.MyTemplateObserver
import access.to.heart.R
import access.to.heart.StatisticsActivity
import access.to.heart.utils.GlobalOptions
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.TextView
import com.jcodecraeer.xrecyclerview.XRecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_history.*

/**
 * Project AccessToHeart.
 * Created by 旭 on 2017/7/12.
 */
class HistoryFragment : BaseFragment() {

    private lateinit var historyAdapter: BaseAdapter<Heart>

    private val stateType = arrayOf("休息", "热身", "有氧", "极限")

    override fun init() {

        historyAdapter = BaseAdapter(R.layout.recycler_history) { view, item ->
            view.findViewById<TextView>(R.id.heart_beat).text = item.HeartBeat.toString()
            view.findViewById<TextView>(R.id.heart_state).text = stateType[item.HeartState]
            view.setOnClickListener {
                startActivity(Intent(activity, StatisticsActivity::class.java)
                        .putExtra("heartText", "${item.HeartBeat}-bmp-${stateType[item.HeartState]}")
                        .putExtra("heartLine", item.HeartLine)
                        .putExtra("heartId", item.Id)
                )
                activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            }
        }

        recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
            setLoadingListener(object : XRecyclerView.LoadingListener {
                override fun onRefresh() {
                    initData()
                    recycler_view.refreshComplete()
                }

                override fun onLoadMore() {
                    updateData()
                    recycler_view.loadMoreComplete()
                }
            })
        }

        initData()
    }

    private fun updateData() {
        cloudAPI.getHeart(query = "Profile.Id:${GlobalOptions.nowProfileId}"
                , offset = historyAdapter.items.size)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MyTemplateObserver<List<Heart>>() {
                    override fun onNext(t: List<Heart>) {
                        historyAdapter.updateItems(t as MutableList<Heart>)
                    }
                })
    }

    private fun initData() {
        cloudAPI.getHeart(query = "Profile.Id:${GlobalOptions.nowProfileId}")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : MyTemplateObserver<List<Heart>>() {
                    override fun onNext(t: List<Heart>) {
                        Log.i("TAG", t[0].HeartBeat.toString())
                        historyAdapter.resetItems(t as MutableList<Heart>)
                    }
                })
    }

    override val layoutId: Int
        get() = R.layout.fragment_history
}