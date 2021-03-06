package access.to.heart

import access.to.heart.fragment.HistoryFragment
import access.to.heart.fragment.MeasureFragment
import access.to.heart.fragment.ProfileFragment
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Project AccessToHeart.
 * Created by 旭 on 2017/5/15.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_view_pager.apply {
            adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
                override fun getCount(): Int = 3

                override fun getItem(position: Int): Fragment {
                    val fragment: Fragment
                    when (position) {
                        0 -> fragment = MeasureFragment()
                        1 -> fragment = HistoryFragment()
                        2 -> fragment = ProfileFragment()
                        else -> return Fragment()
                    }
                    fragment.arguments = intent.extras
                    return fragment
                }
            }

            offscreenPageLimit = 3
        }

        navigation.material()
                .addItem(android.R.drawable.ic_menu_compass, "测量")
                .addItem(android.R.drawable.ic_menu_report_image, "记录")
                .addItem(android.R.drawable.ic_menu_search, "我的")
                .build().setupWithViewPager(main_view_pager)
    }
}
