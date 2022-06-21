package io.matrix.retracer.service;

import io.matrix.retracer.dto.StackTraceDTO;
import io.matrix.retracer.parser.StackParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RetraceService {
    private MappingService mappingService;
    private StackParser parser;

    public RetraceService(@Autowired MappingService mappingService, @Autowired StackParser parser) {
        this.mappingService = mappingService;
        this.parser = parser;
    }

    //TODO: 可以使用LruCache，在内存中只保留最常用的几个methodMapping
    private final Map<String, Map<Integer, String>> cache = new HashMap<>();

    public StackTraceDTO retrace(
            String appId,
            String appVersionName,
            Long appVersionCode,
            StackTraceDTO stackTraceDTO
    ) {
        synchronized (cache) {
            String mappingName = appId + ":" + appVersionName + ":" + appVersionCode;
            Map<Integer, String> mapping = cache.get(mappingName);
            if (mapping == null) {
                mapping = mappingService.getMapping(appId, appVersionName, appVersionCode);
                cache.put(mappingName, mapping);
            }
            String retracedStackKey = parser.parseStackKey(stackTraceDTO.getStackKey(), mapping);
            String retracedStack = parser.parseStack(stackTraceDTO.getStackTrace(), mapping);
            return new StackTraceDTO(retracedStackKey, retracedStack);
        }
    }
}
