package com.ndhzs.timeplan.weight.timeselectview.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ndhzs.timeplan.weight.timeselectview.bean.TSViewDayBean
import com.ndhzs.timeplan.weight.timeselectview.layout.VpLayout
import com.ndhzs.timeplan.weight.timeselectview.utils.TSViewInternalData
import kotlin.collections.ArrayList

/**
 * @author 985892345
 * @email 2767465918@qq.com
 * @date 2021/4/24
 * @description
 */
class TSViewVpAdapter(dayBeans: ArrayList<TSViewDayBean>, data: TSViewInternalData, viewPager2: ViewPager2, showNowTimeLinePosition: Int) : RecyclerView.Adapter<TSViewVpAdapter.ViewHolder>() {


    /**
     * 设置内部ScrollView的滑动监听
     */
    fun setOnScrollListener(l: (scrollY: Int, itemPosition: Int) -> Unit) {
        mData.mOnScrollListener = {scrollY, vpPosition ->
            mScrollY = scrollY
            l.invoke(scrollY, vpPosition)
        }
    }

    /**
     * 得到当前item的ScrollView的滑动量
     */
    fun getTimeLineScrollY(): Int {
        return mScrollY
    }

    fun notifyAllItemRefresh() {
        notifyDataSetChanged()
    }

    /**
     * 让某个位置的item刷新
     */
    fun notifyItemRefresh(position: Int, isBackToCurrentTime: Boolean) {
        notifyItemChanged(position, listOf(NOTIFY_ITEM_REFRESH, isBackToCurrentTime))
    }

    /**
     * 使内部的ScrollView直接瞬移，调用的ScrollView的scrollTo方法
     */
    fun timeLineScrollTo(scrollY: Int) {
        notifyItemChanged(mViewPager2.currentItem, listOf(NOTIFY_ITEM_SCROLL_TO, scrollY))
    }

    /**
     * 使内部的ScrollView较缓慢地滑动，并有回弹动画
     */
    fun timeLineSlowlyScrollTo(scrollY: Int) {
        notifyItemChanged(mViewPager2.currentItem, listOf(NOTIFY_ITEM_SLOWLY_SCROLL_TO, scrollY))
    }

    companion object {

        /**
         * 用于在[getItemViewType]，返回哪个position显示时间线的
         */
        private const val SHOW_NOW_TIME_LINE_POSITION = 0

        /**
         * 用于在[getItemViewType]，返回哪些position不显示时间线的
         */
        private const val NOT_SHOW = 1

        /**
         * 用于[onBindViewHolder]中判断，此时说明是[notifyItemRefresh]调用的notifyItemChanged
         */
        private const val NOTIFY_ITEM_REFRESH = 0

        /**
         * 用于[onBindViewHolder]中判断，此时说明是[timeLineScrollTo]调用的notifyItemChanged
         */
        private const val NOTIFY_ITEM_SCROLL_TO = 1

        /**
         * 用于[onBindViewHolder]中判断，此时说明是[timeLineSlowlyScrollTo]调用的notifyItemChanged
         */
        private const val NOTIFY_ITEM_SLOWLY_SCROLL_TO = 2


    }

    private val mDayBeans = dayBeans
    private val mData = data
    private val mViewPager2 = viewPager2
    private val mShowNowTimeLinePosition = showNowTimeLinePosition

    private var mScrollY = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = VpLayout(parent.context, mData, mViewPager2, mDayBeans[0].day, viewType == SHOW_NOW_TIME_LINE_POSITION)
        view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        }else {
            payloads.forEach {
                val list = it as List<*>
                when (list[0] as Int) {
                    NOTIFY_ITEM_REFRESH -> {
                        holder.mVpLayout.refresh()
                        if (list[1] as Boolean) {
                            holder.mVpLayout.backCurrentTime()
                        }
                    }
                    NOTIFY_ITEM_SCROLL_TO -> {
                        holder.mVpLayout.timeLineScrollTo(list[1] as Int)
                    }
                    NOTIFY_ITEM_SLOWLY_SCROLL_TO -> {
                        holder.mVpLayout.timeLineSlowlyScrollTo(list[1] as Int)
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vpLayout = holder.mVpLayout
        vpLayout.initialize(mDayBeans[position], position)
    }

    override fun getItemCount(): Int {
        return mDayBeans.size
    }

    override fun getItemViewType(position: Int): Int {
        if (position == mShowNowTimeLinePosition) {
            return SHOW_NOW_TIME_LINE_POSITION
        }
        return NOT_SHOW
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        holder.mVpLayout.onViewDetachedFromWindow()
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.mVpLayout.onViewRecycled()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mVpLayout = itemView as VpLayout
    }
}