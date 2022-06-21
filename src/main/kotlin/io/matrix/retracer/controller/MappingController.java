package io.matrix.retracer.controller;

import io.matrix.retracer.service.MappingService;
import io.matrix.retracer.service.RetraceService;
import io.swagger.annotations.Api;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.HashMap;
import java.util.Map;

import static io.matrix.retracer.Constants.REGEXP_SEMVER;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Api("MethodMapping")
@Controller
@RestController
@RequestMapping("/api/mapping")
@Validated
public class MappingController {
    private MappingService mappingService;

    public MappingController(@Autowired @NotNull MappingService mappingService) {
        this.mappingService = mappingService;
    }

    @PostMapping(name = "/{appId}", consumes = MULTIPART_FORM_DATA_VALUE)
    public Map upload(
            @NotBlank
            @PathVariable("appId")
                    String appId,

            @Pattern(regexp = REGEXP_SEMVER)
            @RequestParam("appVersionName")
                    String appVersionName,

            @Min(1L)
            @RequestParam("appVersionCode")
                    Long appVersionCode,

            @RequestParam("file")
                    MultipartFile file
    ) {
        try {
            mappingService.saveMapping(appId, appVersionName, appVersionCode, file);
            Map<String, Object> map = new HashMap<>();
            map.put("appId", appId);
            map.put("appVersionName", appVersionName);
            map.put("appVersionCode", appVersionCode);
            map.put("file", appId);
            Map<String, Object> fileMap = new HashMap<>();
            fileMap.put("name", file.getName());
            fileMap.put("size", file.getSize());
            map.put("file", fileMap);
            return map;
        } catch (Exception e) {
            return null;
        }
    }

}
