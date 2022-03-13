package io.matrix.retracer.controller

import io.matrix.retracer.REGEXP_SEMVER
import io.matrix.retracer.dto.StackTraceDTO
import io.matrix.retracer.service.MappingService
import io.matrix.retracer.service.RetraceService
import io.swagger.annotations.Api
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

@Api("Retrace")
@RestController
@RequestMapping("/api/retrace")
@Validated
class RetraceController(
    @Autowired private val retraceService: RetraceService
) {

    @PostMapping("/{appId}")
    fun retrace(
        @NotBlank
        @PathVariable("appId")
        appId: String,

        @Pattern(regexp = REGEXP_SEMVER)
        @RequestParam("appVersionName")
        appVersionName: String,

        @Min(1L)
        @RequestParam("appVersionCode")
        appVersionCode: Long,

        @Valid
        @RequestBody
        stackTraces: List<StackTraceDTO>
    ): List<StackTraceDTO> = stackTraces.map {
        retraceService.retrace(appId, appVersionName, appVersionCode, it)
    }

}