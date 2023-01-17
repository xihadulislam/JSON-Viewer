package com.xihadulislam.jsonviewer.ext

import java.util.regex.Pattern

object Utils {
    private val urlPattern = Pattern.compile(
        "^((https|http|ftp|rtsp|mms)?://)"
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
                + "|" // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
                + "[a-z]{2,6})" // first level domain- .com or .museum
                + "(:[0-9]{1,4})?" // 端口- :80
                + "((/?)|" // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$"
    )


    fun isUrl(str: String?): Boolean {
        return urlPattern.matcher(str).matches()
    }

    fun jsonFormat(jsonStr: String?): String {
        if (jsonStr == null) return ""
        var level = 0
        val builder = StringBuilder()
        for (element in jsonStr) {
            if (level > 0 && '\n' == builder[builder.length - 1]) {
                builder.append(getHierarchyStr(level))
            }
            when (element) {
                '{', '[' -> {
                    builder.append(element).append("\n")
                    level++
                }
                ',' -> builder.append(element).append("\n")
                '}', ']' -> {
                    builder.append("\n")
                    level--
                    builder.append(getHierarchyStr(level))
                    builder.append(element)
                }
                else -> builder.append(element)
            }
        }
        return builder.toString()
    }

    fun getHierarchyStr(hierarchy: Int): String {
        val levelStr = StringBuilder()
        for (levelI in 0 until hierarchy) {
            levelStr.append("      ")
        }
        return levelStr.toString()
    }
}