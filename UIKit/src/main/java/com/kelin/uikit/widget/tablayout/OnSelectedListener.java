package com.kelin.uikit.widget.tablayout;

/**
 * **描述:** 选中监听。
 *
 * **创建人:** kelin
 *
 * **创建时间:** 2021/8/16 7:15 PM
 *
 * **版本:** v 1.0.0
 */
public interface OnSelectedListener {
    /**
     * 被选中时调用。
     * @param index 当前被选中的索引。
     */
    void onSelected(int index);
}
