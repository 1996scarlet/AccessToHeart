package access.to.heart.Adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Project HealthFlowers.
 * Created by 旭 on 2017/5/27.
 */

class BaseAdapter<T>(val layoutResourceId: Int, var items: MutableList<T> = mutableListOf(), val init: (View, T) -> Unit) :
        RecyclerView.Adapter<BaseAdapter.ViewHolder<T>>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> =
            ViewHolder(LayoutInflater.from(parent.context).inflate(layoutResourceId, parent, false), init)

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) =
            holder.bindForecast(items[position])

    override fun getItemCount() = items.size

    fun resetItems(items: MutableList<T>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun updateItems(items: MutableList<T>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    class ViewHolder<in T>(view: View, val init: (View, T) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bindForecast(item: T) = with(item) {
            init(itemView, item)
        }
    }
}