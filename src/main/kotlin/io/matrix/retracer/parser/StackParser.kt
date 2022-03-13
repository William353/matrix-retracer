package io.matrix.retracer.parser

import org.springframework.stereotype.Service

private const val TEXT_PARSE_ERROR = "parse error"

/**
 * Stack中，方法间的分割符
 */
private const val STACK_SPLIT = " "

/**
 * 解析后，每个方法的分隔符
 */
private const val STACK_RETRACED_SPLIT = " "

@Service
class StackParser {

    fun parseStack(stack: String?, map: Map<Int, String>): String {
        stack ?: return ""
        val sb = StringBuilder()
        return kotlin.runCatching {
            val lines = stack.split(STACK_SPLIT).toTypedArray()
            for (line in lines) {
                if (line.isEmpty()) {
                    continue
                }
                val args: Array<String?> = line.split(",".toRegex()).toTypedArray()
                val method = args[1]!!.toInt()
                val isContainKey = map.containsKey(method)
                if (!isContainKey) {
                    print("error!!!")
                    continue
                }
                args[1] = map[method]
                sb.append(args[0]).append(",").append(args[1]).append(",").append(args[2]).append(",")
                    .append("${args[3].toString().trimIndent()}ms")
                sb.append(STACK_RETRACED_SPLIT)
            }
            sb.toString()
        }.getOrElse { TEXT_PARSE_ERROR }
    }

    fun parseStackKey(stackKey: String?, map: Map<Int, String>): String {
        stackKey ?: return ""
        return kotlin.runCatching {
            val keys = stackKey.split("|").toTypedArray()
            val stringBuilder = StringBuilder()
            for (key in keys) {
                if (key.isNotEmpty()) {
                    continue
                }
                val method = key.toInt()
                stringBuilder.append(map[method] ?: "").append("|")
            }
            stringBuilder.toString()
        }.getOrElse { TEXT_PARSE_ERROR }
    }

}