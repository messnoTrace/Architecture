package com.notrace.network.util

/**
 * 字符工具
 * @author like
 */
object CharacterUtil {
    /**
     * 判断一个字符是否是中文
     * @param c
     * @return
     */
    private fun isChinese(c: Char): Boolean {
        // 根据字节码判断
        return c.toInt() in 0x4E00..0x9FA5
    }

    /**
     * 判断一个字符串是否含有中文
     * @param str
     * @return
     */
    fun containChinese(str: String?): Boolean {
        if (str == null) {
            return false
        }

        for (c in str.toCharArray()) {
            if (isChinese(c)) {
                // 有一个中文字符就返回
                return true
            }
        }
        return false
    }
}
