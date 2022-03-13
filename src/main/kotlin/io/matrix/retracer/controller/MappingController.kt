package io.matrix.retracer.controller

import io.matrix.retracer.REGEXP_SEMVER
import io.matrix.retracer.service.MappingService
import io.swagger.annotations.Api
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

@Api("MethodMapping")
@Controller
@RestController
@RequestMapping("/api/mapping")
@Validated
class MappingController(
    @Autowired private val mappingService: MappingService
) {

    @PostMapping("/{appId}", consumes = [MULTIPART_FORM_DATA_VALUE])
    fun upload(
        @NotBlank
        @PathVariable("appId")
        appId: String,

        @Pattern(regexp = REGEXP_SEMVER)
        @RequestParam("appVersionName")
        appVersionName: String,

        @Min(1L)
        @RequestParam("appVersionCode")
        appVersionCode: Long,

        @RequestParam("file")
        file: MultipartFile
    ): Map<*, *> {
        mappingService.saveMapping(appId, appVersionName, appVersionCode, file::transferTo)
        return mapOf(
            "appId" to appId,
            "appVersionName" to appVersionName,
            "appVersionCode" to appVersionCode,
            "file" to mapOf(
                "name" to file.name,
                "size" to file.size
            )
        )
    }

}