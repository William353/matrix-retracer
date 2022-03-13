package io.matrix.retracer.service

import io.matrix.retracer.MAPPING_TXT
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

/**
 * 混淆(mapping文件)相关
 */
@Service
class MappingService(
    @Value("${'$'}{retracer.dataDir}") private val dataDir: String,
) {
    private val absoluteDir: String by lazy {
        System.getProperty("user.dir") + dataDir
    }

    private val logger: Logger = LoggerFactory.getLogger(javaClass)


    fun saveMapping(appId: String, appVersionName: String, appVersionCode: Long, action: (File) -> Unit): File {
        val dir = File(absoluteDir, arrayOf(appId, appVersionName, appVersionCode).joinToString(File.separator))
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                logger.error("Failed to mkdirs `$dir`")
            }
        }

        val mapping = File(dir, MAPPING_TXT)
        if (mapping.exists()) {
            logger.warn("$mapping already exists")
            mapping.delete()
        }

        try {
            mapping.createNewFile()
        } catch (e: IOException) {
            logger.error("Create mapping file `${mapping.canonicalPath}` failed", e)
            throw e
        }

        logger.info("Saving mapping file `${mapping.canonicalPath}`")

        try {
            return mapping.also(action)
        } catch (e: Throwable) {
            logger.error("Save mapping file `${mapping.canonicalPath}` failed", e)
            throw e
        }
    }

    fun getMapping(appId: String, appVersionName: String, appVersionCode: Long): Map<Int, String> {
        return readMappingFile(getMappingFile(appId, appVersionName, appVersionCode))
    }

    private fun getMappingFile(appId: String, appVersionName: String, appVersionCode: Long): File {
        val dir = File(absoluteDir, arrayOf(appId, appVersionName, appVersionCode).joinToString(File.separator))
        return File(dir, MAPPING_TXT)
    }

    /**
     * 读mapdding到内存
     */
    private fun readMappingFile(file: File): Map<Int, String> {
        val methodMap = HashMap<Int, String>()
        kotlin.runCatching {
            BufferedReader(FileReader(file)).use { reader ->
                var tempString: String?
                while (reader.readLine().also { tempString = it } != null) {
                    tempString?.split(",".toRegex())?.toTypedArray()?.let {
                        methodMap[it.first().toInt()] = it[2].replace('\n', ' ')
                    }
                }
            }
        }
        return methodMap
    }

}