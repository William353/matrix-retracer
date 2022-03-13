package io.matrix.retracer.dto

import javax.validation.constraints.NotBlank

data class StackTraceDTO(
//    @field:NotBlank
//    val stackKey: String,
    @field:NotBlank
    val stackTrace: String
)
