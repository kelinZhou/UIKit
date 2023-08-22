package com.kelin.uikit.delegate

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kelin.uikit.R
import com.kelin.uikit.flyweight.adapter.CommonRecyclerViewAdapter
import com.kelin.uikit.flyweight.adapter.NoMoreDataHandler
import com.kelin.uikit.flyweight.adapter.SuperRecyclerViewAdapter
import com.kelin.uikit.flyweight.callback.ItemHolderGenerator
import com.kelin.uikit.flyweight.callback.OnItemEventListener
import com.kelin.uikit.flyweight.decoration.DecorationStyles
import com.kelin.uikit.flyweight.viewholder.AbsItemViewHolder
import com.kelin.uikit.presenter.ViewPresenter
import com.kelin.uikit.tools.AppLayerErrorCatcher
import com.kelin.uikit.widget.statelayout.StatePage

import java.util.ArrayList

/**
 * **描述:** 如果你的页面是一个列表页面的话你就应该继承该类。这样可以省去你很多的模板代码。
 *
 *
 * **创建人:** kelin
 * **创建时间:** 2019/3/29  1:33 PM
 * **版本:** v 1.0.0
 */
abstract class CommonListWrapperDelegate<VC : CommonListWrapperDelegate.CommonListDelegateCallback<I>, I, D> :
    CommonViewDelegate<VC, D>(), CommonRecyclerViewAdapter.ItemTypeTranslator<I>, ItemHolderGenerator<AbsItemViewHolder<I>, I>, NoMoreDataHandler {

    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var mMovingPosition = -1
    private var mIsMoving = false
    private var rvListView: RecyclerView? = null

    protected val layoutManager: RecyclerView.LayoutManager?
        get() = mLayoutManager

    private val mOnScrollListener = object : RecyclerView.OnScrollListener() {
        var curState = RecyclerView.SCROLL_STATE_IDLE
        override fun onScrollStateChanged(view: RecyclerView, newState: Int) {
            super.onScrollStateChanged(view, newState)
            curState = newState
            this@CommonListWrapperDelegate.onRecyclerViewScrollStateChanged(view, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            this@CommonListWrapperDelegate.onRecyclerViewScrolled(recyclerView, dx, dy, curState)

            //在这里进行第二次滚动（最后的100米！）
            if (mIsMoving) {
                mIsMoving = false
                //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                val n = mMovingPosition - (mLayoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                if (0 <= n && n < recyclerView.childCount) {
                    //获取要置顶的项顶部离RecyclerView顶部的距离
                    val top = recyclerView.getChildAt(n).top
                    //最后的移动
                    recyclerView.scrollBy(0, top)
                }
            }
        }
    }

    override val pageStateFlags: Int
        get() = StatePage.HAVE_EMPTY_STATE or super.pageStateFlags

    override val rootLayoutId: Int
        get() = R.layout.common_list

    protected open val recyclerViewId: Int
        get() = R.id.list_view

    protected val recyclerView: RecyclerView
        get() {
            return if (rvListView != null) {
                rvListView!!
            } else {
                getView<RecyclerView>(recyclerViewId)?.apply {
                    rvListView = this
                } ?: throw NullPointerException("RecyclerView is null object!")
            }
        }

    @Suppress("UNCHECKED_CAST")
    protected val adapter: SuperRecyclerViewAdapter<I, AbsItemViewHolder<I>>
        get() = adapterOrNull ?: throw NullPointerException("The Adapter must be null!")

    @Suppress("UNCHECKED_CAST")
    protected val adapterOrNull: SuperRecyclerViewAdapter<I, AbsItemViewHolder<I>>?
        get() = rvListView?.adapter as? SuperRecyclerViewAdapter<I, AbsItemViewHolder<I>>

    protected val visiblePositions: IntArray
        get() {
            return if (mLayoutManager is LinearLayoutManager) {
                val first = (mLayoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                val last = (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                intArrayOf(first, last)
            } else {
                val lastChildView = mLayoutManager!!.getChildAt(mLayoutManager!!.childCount - 1)
                val firstChildView = mLayoutManager!!.getChildAt(0)

                val firstPosition = if (firstChildView == null) 0 else mLayoutManager!!.getPosition(firstChildView)
                val lastPosition = if (lastChildView == null) 0 else mLayoutManager!!.getPosition(lastChildView)
                intArrayOf(firstPosition, lastPosition)
            }
        }

    protected val visibleItems: List<I>
        get() {
            val pos = visiblePositions
            val start = pos[0]
            val end = pos[1]

            if (start < 0 || end < 0) {
                return emptyList()
            }

            val adapter = adapter
            val len = end - start
            val count = adapter.itemCount
            if (start > count - 1 || end > count - 1) {
                return emptyList()
            }

            val r = ArrayList<I>()
            for (i in 0 until len + 1) {
                r.add(adapter.getItem(start + i))
            }
            return r

        }

    interface CommonListDelegateCallback<D> : CommonViewDelegateCallback {

        /**
         * 是否显示没有更多数据的样式。
         */
        val canShowNoMoreStyle: Boolean

        fun onListItemClick(itemView: View, position: Int, item: D)

        fun onListItemChildClick(itemView: View, position: Int, v: View, item: D)

        fun onListItemLongClick(itemView: View, position: Int, item: D)

        fun onLoadMore(retry: Boolean): Boolean

        fun isLoadMoreEnable(): Boolean
    }


    override fun bindView(viewPresenter: ViewPresenter<VC>, savedInstanceState: Bundle?) {
        super.bindView(viewPresenter, savedInstanceState)
        setupRecyclerView(recyclerView)
    }

    override fun unbindView() {
        super.unbindView()
        rvListView = null
    }

    @CallSuper
    override fun setInitialData(data: D) {
        val listData = extractInitialDataList(data)
        if (listData.isEmpty()) {
            removeAllItem()
        } else {
            adapter.setData(listData)
        }
    }

    @CallSuper
    fun addMoreData(data: D) {
        adapter.addAll(extractInitialDataList(data), true)
    }

    protected abstract fun extractInitialDataList(initialData: D): Collection<I>


    private fun setupRecyclerView(recyclerView: RecyclerView?) {
        recyclerView!!.setHasFixedSize(true)
        // 设置Item增加、移除动画
        val animator = DefaultItemAnimator()
        animator.addDuration = 200
        animator.changeDuration = 100
        animator.removeDuration = 200
        animator.moveDuration = 200
        recyclerView.itemAnimator = animator
        // 设置布局管理器（LinearLayoutManager 现行管理器，支持横向、纵向。
        // GridLayoutManager 网格布局管理器
        // StaggeredGridLayoutManager 瀑布就式布局管理器）
        mLayoutManager = onCreateLayoutManager()
        recyclerView.layoutManager = mLayoutManager
        val option = decorationOption
        if (option != null) {
            DecorationStyles.setDecorationStyle(recyclerView, option)
        }
        recyclerView.addOnScrollListener(mOnScrollListener)

        // 添加分割线
        //recyclerView.addItemDecoration(new RecyclerViewItemDecoration(context, RecyclerViewItemDecoration.HORIZONTAL_LIST));

        if (recyclerView.adapter == null) {
            val adapter = onCreateAdapter()
            recyclerView.adapter = adapter
        }
    }

    protected open val decorationOption: DecorationStyles.Options?
        get() = null

    protected open fun onCreateLayoutManager(): RecyclerView.LayoutManager {
        return object : LinearLayoutManager(context, VERTICAL, false) {
            override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
                try {
                    super.onLayoutChildren(recycler, state)
                } catch (e: IndexOutOfBoundsException) {
                    AppLayerErrorCatcher.throwException(e)
                }
            }
        }
    }

    protected open fun onRecyclerViewScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

    }

    protected open fun onRecyclerViewScrolled(recyclerView: RecyclerView, dx: Int, dy: Int, curState: Int) {

    }

    @get:LayoutRes
    protected open val loadMoreLayoutRes: Int
        get() = R.layout.layout_load_more

    @get:LayoutRes
    protected open val loadMoreFailedRes: Int
        get() = R.layout.layout_load_more_failed

    @get:LayoutRes
    protected open val loadMoreNoMoreRes: Int
        get() = R.layout.layout_no_more_data

    /**
     * 创建adapter
     */
    protected open fun onCreateAdapter(): SuperRecyclerViewAdapter<I, AbsItemViewHolder<I>> {
        return CommonRecyclerViewAdapter(this, this, CommonOnItemEventListenerImpl(), this).apply {
            if (viewCallback.isLoadMoreEnable()) {
                setLoadMoreView(loadMoreLayoutRes, loadMoreFailedRes, loadMoreNoMoreRes, loadMoreOffset) {
                    viewCallback.onLoadMore(it)
                }
            }
        }
    }

    override fun onShowNoMore(): Boolean {
        return viewCallback.canShowNoMoreStyle
    }

    protected open val loadMoreOffset: Int
        get() = 2

    /**
     * 当条目被点击时调用。
     * @param position 被点击的条目的索引。
     * @param item 当前条目的数据模型。
     * @return 如果返回true则表示自己消费，返回false则会继续想ViewPresenter中传递。
     */
    protected open fun onItemClick(position: Int, item: I): Boolean {
        return false
    }

    /**
     * 当条目被长按时调用。
     * @param position 被点击的条目的索引。
     * @param item 当前条目的数据模型。
     * @return 如果返回true则表示自己消费，返回false则会继续想ViewPresenter中传递。
     */
    protected open fun onItemLongClick(position: Int, item: I): Boolean {
        return false
    }

    /**
     * 当条目中的子View被点击时调用。
     * @param position 被点击的条目的索引。
     * @param v 被点击的子View。
     * @param item 当前条目的数据模型。
     * @return 如果返回true则表示自己消费，返回false则会继续想ViewPresenter中传递。
     */
    protected open fun onItemChildClick(position: Int, v: View, item: I): Boolean {
        return false
    }

    open fun showLoadMoreError(err: String) {}

    open fun moveToPosition(position: Int) {
        mMovingPosition = position
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is LinearLayoutManager) {
            // 先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
            val firstItem = layoutManager.findFirstVisibleItemPosition()
            val lastItem = layoutManager.findLastVisibleItemPosition()

            // 然后区分情况
            when {
                position <= firstItem -> // 当要置顶的项在当前显示的第一个项的前面时
                    recyclerView.scrollToPosition(position)
                position <= lastItem -> {
                    // 当要置顶的项已经在屏幕上显示时
                    val top = recyclerView.getChildAt(position - firstItem).top
                    recyclerView.scrollBy(0, top)
                }
                else -> {
                    // 当要置顶的项在当前显示的最后一项的后面时
                    recyclerView.scrollToPosition(position)
                    // 这里这个变量是用在RecyclerView滚动监听里面的
                    mIsMoving = true
                }
            }
        }
    }

    fun setNoMoreData(noMoreData: Boolean) {
        adapter.setNoMoreData(noMoreData)
    }

    fun isNoMoreData(): Boolean {
        return adapter.isNoMoreData()
    }

    fun setLoadMoreFinish() {
        adapter.setLoadMoreFinish()
    }

    fun getItem(position: Int): I {
        return adapter.getItem(position)
    }

    fun getItemOrNull(position: Int): I? {
        return adapter.getItemOrNull(position)
    }

    fun getLastItem(): I? {
        return adapter.dataList.lastOrNull()
    }

    fun setLoadMoreFailed() {
        adapter.setLoadMoreFailed()
    }

    open fun addItem(data: I) {
        adapter.addItem(data)
    }

    fun addItem(position: Int, data: Collection<I>, refresh: Boolean = true) {
        adapter.addItem(position, data, refresh)
    }

    open fun addItem(position: Int, data: I) {
        adapter.addItem(position, data)
    }

    fun notifyItemChanged(data: I) {
        adapter.notifyItem(data)
    }

    fun notifyItemChanged(position: Int, data: I) {
        adapter.removeItem(position, false)
        adapter.addItem(position, data, false)
        adapter.notifyItemChanged(position)
    }

    fun notifyDataSetChanged() {
        adapter.notifyDataSetChanged()
    }

    fun notifyItemChanged(position: Int) {
        adapter.notifyItemChanged(position)
    }

    fun removeItem(position: Int, refresh: Boolean = true) {
        adapter.removeItem(position, refresh)
        if (adapter.dataList.isEmpty()) {
            showEmptyView()
        }
    }

    fun removeItem(position: Int, count: Int, refresh: Boolean = true) {
        adapter.removeItem(position, count, refresh)
        if (adapter.dataList.isEmpty()) {
            showEmptyView()
        }
    }

    fun removeItem(refresh: Boolean = true, finder: (cell: I) -> Boolean) {
        val indexed = adapter.dataList.indexOfFirst(finder)
        if (indexed >= 0) {
            removeItem(indexed, refresh)
            if (adapter.dataList.isEmpty()) {
                showEmptyView()
            }
        }
    }

    fun removeAllItem() {
        adapter.clear(true)
        showEmptyView()
    }

    fun getItem(data: I): Int {
        return adapter.getItemPosition(data)
    }

    internal inner class CommonOnItemEventListenerImpl : OnItemEventListener<I> {

        override fun onItemClick(itemView: View, position: Int, item: I) {
            if (!this@CommonListWrapperDelegate.onItemClick(position, item)) {
                viewCallback.onListItemClick(itemView, position, item)
            }
        }

        override fun onItemLongClick(itemView: View, position: Int, item: I) {
            if (!this@CommonListWrapperDelegate.onItemLongClick(position, item)) {
                viewCallback.onListItemLongClick(itemView, position, item)
            }
        }

        override fun onItemChildClick(itemView: View, v: View, position: Int, item: I) {
            if (!this@CommonListWrapperDelegate.onItemChildClick(position, v, item)) {
                viewCallback.onListItemChildClick(itemView, position, v, item)
            }
        }
    }
}
