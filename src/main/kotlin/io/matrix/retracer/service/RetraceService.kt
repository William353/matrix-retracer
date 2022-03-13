package io.matrix.retracer.service

import io.matrix.retracer.dto.StackTraceDTO
import io.matrix.retracer.parser.StackParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RetraceService(
    @Autowired private val mappingService: MappingService,
    @Autowired private val parser: StackParser
) {

    private val cache: MutableMap<String, Map<Int, String>> = mutableMapOf()

    fun retrace(
        appId: String,
        appVersionName: String,
        appVersionCode: Long,
        stackTraceDTO: StackTraceDTO
    ): StackTraceDTO = synchronized(cache) {
        cache.getOrPut("${appId}:${appVersionName}:${appVersionCode}") {
            mappingService.getMapping(appId, appVersionName, appVersionCode)
        }
    }.let {
        StackTraceDTO(
//            stackKey = parser.parseStackKey(stackTraceDTO.stackKey, it),
            stackTrace = parser.parseStack(stackTraceDTO.stackTrace, it)
        )
    }

}