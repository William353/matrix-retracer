package io.matrix.retracer.controller;

import io.matrix.retracer.dto.StackTraceDTO;
import io.matrix.retracer.service.RetraceService;
import io.swagger.annotations.Api;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

import static io.matrix.retracer.Constants.REGEXP_SEMVER;

@Api("Retrace")
@RestController
@RequestMapping("/api/retrace")
@Validated
public class RetraceController {
    private final RetraceService retraceService;

    public RetraceController(@Autowired @NotNull RetraceService retraceService) {
        this.retraceService = retraceService;
    }

    @PostMapping("/{appId}")
    public List<StackTraceDTO> retrace(
            @NotBlank
            @PathVariable("appId")
                    String appId,

            @Pattern(regexp = REGEXP_SEMVER)
            @RequestParam("appVersionName")
                    String appVersionName,

            @Min(1L)
            @RequestParam("appVersionCode")
                    Long appVersionCode,

            @Valid
            @RequestBody
                    List<StackTraceDTO> stackTraces
    ) {
        List<StackTraceDTO> list = new ArrayList<>();
        for (StackTraceDTO trace : stackTraces) {
            list.add(retraceService.retrace(appId, appVersionName, appVersionCode, trace));
        }
        return list;
    }
}
