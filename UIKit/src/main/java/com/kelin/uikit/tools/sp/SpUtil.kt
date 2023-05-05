package com.kelin.uikit.tools.sp

import android.content.Context
import android.content.SharedPreferences.Editor


/**
 * **描述:** SharedPreferences 相关的工具类。
 *
 *
 * **创建人:** kelin
 *
 *
 * **创建时间:** 2019/4/16  10:37 AM
 *
 *
 * **版本:** v 1.0.0
 */
object SpUtil {

    /**
     * 保存一个boolean值到配置文件中
     *
     * @param context 上下文
     * @param key     要保存的键
     * @param value   要保存的值
     */
    fun putBoolean(context: Context, key: String, value: Boolean) {
        getEdit(context).putBoolean(key, value).commit()
    }

    /**
     * 保存一个boolean值到配置文件中
     *
     * @param context 上下文
     * @param key     要保存的键
     * @param value   要保存的值
     */
    fun putBooleanAsync(context: Context, key: String, value: Boolean) {
        getEdit(context).putBoolean(key, value).apply()
    }

    /**
     * 从配置文件中获取一个boolean类型的配置信息
     *
     * @param context 上下文
     * @param key     要获取的键
     * @param def     当获取失败时返回的默认值
     * @return 返回指定key所对应的值
     */
    fun getBoolean(context: Context, key: String, def: Boolean): Boolean {
        return getSp(context).getBoolean(key, def)
    }

    /**
     * 保存一个String值到配置文件中
     *
     * @param context 上下文
     * @param key     要保存的键
     * @param value   要保存的值
     */
    fun putString(context: Context, key: String, value: String) {
        getEdit(context).putString(key, value).commit()
    }

    /**
     * 保存一个String值到配置文件中
     *
     * @param context 上下文
     * @param key     要保存的键
     * @param value   要保存的值
     */
    fun putStringAsync(context: Context, key: String, value: String) {
        getEdit(context).putString(key, value).apply()
    }

    /**
     * 从配置文件中获取一个String类型的配置信息
     *
     * @param context 上下文
     * @param key     要获取的键
     * @return 返回指定key所对应的值
     */
    fun getString(context: Context, key: String): String {
        return getSp(context).getString(key, "") ?: ""
    }

    fun putSecureString(context: Context, key: String, value: String) {
        getSecureSp(context).edit().putString(key, value).commit()
    }

    fun getSecureString(context: Context, key: String): String {
        return getSecureSp(context).getString(key, "")?:""
    }

    fun getSecureString(context: Context, key: String, defValue: String): String {
        return getSecureSp(context).getString(key, defValue)?:""
    }

    /**
     * 保存一个int值到配置文件中
     *
     * @param context 上下文
     * @param key     要保存的键
     * @param value   要保存的值
     */
    fun putInt(context: Context, key: String, value: Int) {
        getEdit(context).putInt(key, value).commit()
    }

    fun putLong(context: Context, key: String, value: Long) {
        getEdit(context).putLong(key, value).commit()
    }

    /**
     * 从配置文件中获取一个int类型的配置信息
     *
     * @param context 上下文
     * @param key     要获取的键
     * @return 返回指定key所对应的值
     */
    fun getInt(context: Context, key: String, defValue: Int): Int {
        return getSp(context).getInt(key, defValue)
    }

    /**
     * 从配置文件中获取一个int类型的配置信息
     *
     * @param context 上下文
     * @param key     要获取的键
     * @return 返回指定key所对应的值
     */
    fun getLong(context: Context, key: String, defValue: Long): Long {
        return getSp(context).getLong(key, defValue)
    }

    /**
     * 获取所有键值对
     *
     * @param context 上下文
     * @return 返回一个Map集合
     */
    fun getAll(context: Context): Map<String, *> {
        return getSp(context).all
    }

    /**
     * 获取一个指定Key的集合
     *
     * @param context 上下文
     * @param key     指定的Key
     * @return 返回一个 Set<String> 集合
    </String> */
    fun getStringSet(context: Context, key: String): Set<String>? {
        return getSp(context).getStringSet(key, null)
    }


    /**
     * 这个方法是根据指定的key删除一个键值对
     *
     * @param context 上下文
     * @param key     要删除的键
     */
    fun remove(context: Context, key: String) {
        getEdit(context).remove(key).commit()
    }

    /**
     * 清空全部任容
     *
     * @param context 上下文
     */
    fun clear(context: Context) {
        getEdit(context).clear()
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param context 上下文
     * @param key     要查询的key
     * @return 存在返回true, 不存在返回false
     */
    fun contains(context: Context, key: String): Boolean {
        return getSp(context).contains(key)
    }

    /**
     * 这个方法是返回一个sharedPreferences的Edit编辑器
     *
     * @param context 上下文
     * @return 返回一个Edit编辑器。
     */
    private fun getEdit(context: Context): Editor {
        return getSp(context).edit()
    }

    private fun getSp(context: Context) = context.getSharedPreferences("${context.packageName}.SharedPreferences", Context.MODE_PRIVATE)

    private fun getSecureSp(context: Context): SecurePreferences {
        return SecurePreferences(
            context,
            context.packageName,
            "${context.packageName}.SecureSharedPreferences"
        )
    }
}
