package com.xihadulislam.jsonviewer.adapter

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xihadulislam.jsonviewer.base.BaseJsonViewerAdapter
import com.xihadulislam.jsonviewer.ext.Utils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener


class JsonViewerAdapter : BaseJsonViewerAdapter<JsonViewerAdapter.JsonItemViewHolder?> {
    private var jsonStr: String? = null
    private var mJSONObject: JSONObject? = null
    private var mJSONArray: JSONArray? = null
    private var clicked = false

    constructor(jsonStr: String?) {
        this.jsonStr = jsonStr
        var `object`: Any? = null
        try {
            `object` = JSONTokener(jsonStr).nextValue()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        if (`object` != null && `object` is JSONObject) {
            mJSONObject = `object`
        } else if (`object` != null && `object` is JSONArray) {
            mJSONArray = `object`
        } else {
            throw IllegalArgumentException("jsonStr is illegal.")
        }
    }

    constructor(jsonObject: JSONObject?) {
        mJSONObject = jsonObject
        requireNotNull(mJSONObject) { "jsonObject can not be null." }
    }

    constructor(jsonArray: JSONArray?) {
        mJSONArray = jsonArray
        requireNotNull(mJSONArray) { "jsonArray can not be null." }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JsonItemViewHolder {
        return JsonItemViewHolder(JsonViewHolder(parent.context))
    }

    override fun onBindViewHolder(holder: JsonItemViewHolder, position: Int) {
        val itemView: JsonViewHolder = holder.itemView as JsonViewHolder
        itemView.setTextSize(TEXT_SIZE_DP)
        itemView.setRightColor(BRACES_COLOR)
        if (mJSONObject != null) {
            if (position == 0) {
                itemView.hideLeft()
                itemView.hideIcon()
                itemView.showRight("{")
                return
            } else if (position == itemCount - 1) {
                itemView.hideLeft()
                itemView.hideIcon()
                itemView.showRight("}")
                return
            } else if (mJSONObject!!.names() == null) {
                return
            }
            val key = mJSONObject!!.names().optString(position - 1) // 遍历key
            val value = mJSONObject!!.opt(key)
            if (position < itemCount - 2) {
                handleJsonObject(key, value, itemView, true, 1)
            } else {
                handleJsonObject(key, value, itemView, false, 1) // 最后一组，结尾不需要逗号
            }
        }
        if (mJSONArray != null) {
            if (position == 0) {
                itemView.hideLeft()
                itemView.hideIcon()
                itemView.showRight("[")
                return
            } else if (position == itemCount - 1) {
                itemView.hideLeft()
                itemView.hideIcon()
                itemView.showRight("]")
                return
            }
            val value = mJSONArray!!.opt(position - 1) // 遍历array
            if (position < itemCount - 2) {
                handleJsonArray(value, itemView, true, 1)
            } else {
                handleJsonArray(value, itemView, false, 1) // 最后一组，结尾不需要逗号
            }
        }
    }


    private fun handleJsonObject(
        key: String,
        value: Any,
        itemView: JsonViewHolder,
        appendComma: Boolean,
        hierarchy: Int
    ) {
        val keyBuilder = SpannableStringBuilder(Utils.getHierarchyStr(hierarchy))
        keyBuilder.append("\"").append(key).append("\"").append(":")
        keyBuilder.setSpan(
            ForegroundColorSpan(KEY_COLOR),
            0,
            keyBuilder.length - 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        keyBuilder.setSpan(
            ForegroundColorSpan(BRACES_COLOR),
            keyBuilder.length - 1,
            keyBuilder.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        itemView.showLeft(keyBuilder)
        handleValue(value, itemView, appendComma, hierarchy)
    }


    private fun handleJsonArray(
        value: Any,
        itemView: JsonViewHolder,
        appendComma: Boolean,
        hierarchy: Int
    ) {
        itemView.showLeft(SpannableStringBuilder(Utils.getHierarchyStr(hierarchy)))
        handleValue(value, itemView, appendComma, hierarchy)
    }


    private fun handleValue(
        value: Any?,
        itemView: JsonViewHolder,
        appendComma: Boolean,
        hierarchy: Int
    ) {
        val valueBuilder = SpannableStringBuilder()
        if (value is Number) {
            valueBuilder.append(value.toString())
            valueBuilder.setSpan(
                ForegroundColorSpan(NUMBER_COLOR),
                0,
                valueBuilder.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else if (value is Boolean) {
            valueBuilder.append(value.toString())
            valueBuilder.setSpan(
                ForegroundColorSpan(BOOLEAN_COLOR),
                0,
                valueBuilder.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        } else if (value is JSONObject) {
            itemView.showIcon(true)
            valueBuilder.append("Object{...}")
            valueBuilder.setSpan(
                ForegroundColorSpan(BRACES_COLOR),
                0,
                valueBuilder.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            itemView.setIconClickListener(
                JsonItemClickListener(
                    value,
                    itemView,
                    appendComma,
                    hierarchy + 1
                )
            )
            if (clicked) {
                itemView.clickIcon()
            }
        } else if (value is JSONArray) {
            itemView.showIcon(true)
            valueBuilder.append("Array[").append(value.length().toString()).append("]")
            val len = valueBuilder.length
            valueBuilder.setSpan(
                ForegroundColorSpan(BRACES_COLOR),
                0,
                6,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            valueBuilder.setSpan(
                ForegroundColorSpan(NUMBER_COLOR),
                6,
                len - 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            valueBuilder.setSpan(
                ForegroundColorSpan(BRACES_COLOR),
                len - 1,
                len,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            itemView.setIconClickListener(
                JsonItemClickListener(
                    value,
                    itemView,
                    appendComma,
                    hierarchy + 1
                )
            )
            if (clicked) {
                itemView.clickIcon()
            }
        } else if (value is String) {
            itemView.hideIcon()
            valueBuilder.append("\"").append(value.toString()).append("\"")
            if (Utils.isUrl(value.toString())) {
                valueBuilder.setSpan(
                    ForegroundColorSpan(TEXT_COLOR),
                    0,
                    1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                valueBuilder.setSpan(
                    ForegroundColorSpan(URL_COLOR),
                    1,
                    valueBuilder.length - 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                valueBuilder.setSpan(
                    ForegroundColorSpan(TEXT_COLOR),
                    valueBuilder.length - 1,
                    valueBuilder.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                valueBuilder.setSpan(
                    ForegroundColorSpan(TEXT_COLOR),
                    0,
                    valueBuilder.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        } else if (valueBuilder.length == 0 || value == null) {
            itemView.hideIcon()
            valueBuilder.append("null")
            valueBuilder.setSpan(
                ForegroundColorSpan(NULL_COLOR),
                0,
                valueBuilder.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        if (appendComma) {
            valueBuilder.append(",")
        }
        itemView.showRight(valueBuilder)
    }

    override fun expandAll() {
        clicked = true
        notifyDataSetChanged()
    }

    override fun collapseAll() {
        clicked = false
        notifyDataSetChanged()
    }

    internal inner class JsonItemClickListener(
        private val value: Any?,
        itemView: JsonViewHolder,
        appendComma: Boolean,
        hierarchy: Int
    ) :
        View.OnClickListener {
        private val itemView: JsonViewHolder
        private val appendComma: Boolean
        private val hierarchy: Int
        private var isCollapsed = true
        private val isJsonArray: Boolean

        init {
            this.itemView = itemView
            this.appendComma = appendComma
            this.hierarchy = hierarchy
            isJsonArray = value != null && value is JSONArray
        }

        override fun onClick(view: View) {
            if (itemView.childCount === 1) { // 初始（折叠） --> 展开""
                isCollapsed = false
                itemView.showIcon(false)
                itemView.tag = itemView.getRightText()
                itemView.showRight(if (isJsonArray) "[" else "{")
                val array =
                    if (isJsonArray) value as JSONArray? else (value as JSONObject?)!!.names()
                var i = 0
                while (array != null && i < array.length()) {
                    val childItemView = JsonViewHolder(itemView.context)
                    childItemView.setTextSize(TEXT_SIZE_DP)
                    childItemView.setRightColor(BRACES_COLOR)
                    val childValue = array.opt(i)
                    if (isJsonArray) {
                        handleJsonArray(
                            childValue,
                            childItemView,
                            i < array.length() - 1,
                            hierarchy
                        )
                    } else {
                        (value as JSONObject?)!!.opt(
                            childValue as String?
                        )?.let {
                            handleJsonObject(
                                childValue as String,
                                it, childItemView, i < array.length() - 1, hierarchy
                            )
                        }
                    }
                    itemView.addViewNoInvalidate(childItemView)
                    i++
                }
                val childItemView = JsonViewHolder(itemView.context)
                childItemView.setTextSize(TEXT_SIZE_DP)
                childItemView.setRightColor(BRACES_COLOR)
                val builder: StringBuilder = StringBuilder(Utils.getHierarchyStr(hierarchy - 1))
                builder.append(if (isJsonArray) "]" else "}").append(if (appendComma) "," else "")
                childItemView.showRight(builder)
                itemView.addViewNoInvalidate(childItemView)
                itemView.requestLayout()
                itemView.invalidate()
            } else {                            // 折叠 <--> 展开
                val temp: CharSequence = itemView.getRightText()
                itemView.showRight(itemView.getTag() as CharSequence)
                itemView.tag = temp
                itemView.showIcon(!isCollapsed)
                for (i in 1 until itemView.childCount) {
                    itemView.getChildAt(i).visibility = if (isCollapsed) View.VISIBLE else View.GONE
                }
                isCollapsed = !isCollapsed
            }
        }
    }

    inner class JsonItemViewHolder(itemView: JsonViewHolder) :
        RecyclerView.ViewHolder(itemView) {
        lateinit var itemView: JsonViewHolder

        init {
            setIsRecyclable(false)
        }
    }

    override fun getItemCount(): Int {
        if (mJSONObject != null) {
            return if (mJSONObject!!.names() != null) {
                mJSONObject!!.names().length() + 2
            } else {
                2
            }
        }
        return if (mJSONArray != null) {
            mJSONArray!!.length() + 2
        } else 0
    }
}