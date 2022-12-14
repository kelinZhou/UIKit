package com.kelin.uikit.widget.statelayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FixedRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {

    private var mEmptyView: View? = null
    private var mOnEmptyStatusChangedListener: ((isEmpty: Boolean) -> Unit)? = null
    private var onSizeChangedListener: ((recyclerView: FixedRecyclerView, w: Int, h: Int, oldW: Int, oldH: Int) -> Unit)? = null


    private var mAdapterDataObserver = FixedAdapterDataObserver()

    private var mMovingPosition = -1
    private var mIsMoving = false

    val isInFilterMode: Boolean
        get() = false


    fun setEmptyView(emptyView: View?) {
        mEmptyView = emptyView
        checkFocus()
    }


    internal fun checkFocus() {
        val empty = adapter == null || adapter!!.itemCount == 0

        mOnEmptyStatusChangedListener?.invoke(empty)

        if (mEmptyView != null) {
            updateEmptyStatus(empty)
        }
    }

    fun setOnEmptyStatusChangedListener(listener: (isEmpty: Boolean) -> Unit) {
        mOnEmptyStatusChangedListener = listener
    }

    private fun updateEmptyStatus(empty: Boolean) {
        var isEmpty = empty
        if (isInFilterMode) {
            isEmpty = false
        }
        if (isEmpty) {
            if (mEmptyView != null) {
                mEmptyView!!.visibility = View.VISIBLE
                visibility = View.INVISIBLE
            } else {
                // If the caller just removed our empty view, make sure the list view is visible
                visibility = View.VISIBLE
            }

            // We are now GONE, so pending layouts will not be dispatched.
            // Force one here to make sure that the state of the list matches
            // the state of the adapter.

            /*if (mDataChanged) {
                this.onLayout(false, mLeft, mTop, mRight, mBottom);
            }*/
        } else {
            if (mEmptyView != null) mEmptyView!!.visibility = View.GONE
            visibility = View.VISIBLE
        }
    }


    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        adapter?.registerAdapterDataObserver(mAdapterDataObserver)
        super.setAdapter(adapter)
        checkFocus()
    }

    fun addScrollListener() {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (mIsMoving) {
                    mIsMoving = false
                    // ????????????????????????????????????????????????mIndex???????????????????????????RecyclerView????????????
                    val layoutManager = layoutManager
                    if (layoutManager is LinearLayoutManager) {

                        val n = mMovingPosition - layoutManager.findFirstVisibleItemPosition()
                        if (n in 0 until childCount) {
                            // ??????????????????????????????RecyclerView???????????????
                            val top = getChildAt(n).top
                            // ???????????????
                            scrollBy(0, top)
                        }
                    }

                }
            }
        })
    }


    /**
     * ?????????LinearLayoutManager.scrollToPositionWithOffset(pos, 0);????????????????????????????????????????????????????????????????????????????????????setStackFromEnd(true);????????????OK
     *
     * @param n
     */
    fun moveToPosition(n: Int) {
        mMovingPosition = n
        val layoutManager = layoutManager
        if (layoutManager is LinearLayoutManager) {
            // ??????RecyclerView???LayoutManager????????????????????????????????????Position
            val firstItem = layoutManager.findFirstVisibleItemPosition()
            val lastItem = layoutManager.findLastVisibleItemPosition()

            // ??????????????????
            if (n <= firstItem) {
                // ????????????????????????????????????????????????????????????
                scrollToPosition(n)
            } else if (n <= lastItem) {
                // ?????????????????????????????????????????????
                val top = getChildAt(n - firstItem).top
                scrollBy(0, top)
            } else {
                // ????????????????????????????????????????????????????????????
                scrollToPosition(n)
                // ???????????????????????????RecyclerView?????????????????????
                mIsMoving = true
            }
        }
    }

    fun setOnSizeChangedListener(listener: (recyclerView: FixedRecyclerView, w: Int, h: Int, oldW: Int, oldH: Int) -> Unit) {
        onSizeChangedListener = listener
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        onSizeChangedListener?.invoke(this, w, h, oldw, oldh)
    }

    inner class FixedAdapterDataObserver : AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            checkFocus()
        }


        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            super.onItemRangeChanged(positionStart, itemCount)
            checkFocus()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            checkFocus()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            checkFocus()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            checkFocus()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            super.onItemRangeChanged(positionStart, itemCount, payload)
            checkFocus()
        }
    }
}