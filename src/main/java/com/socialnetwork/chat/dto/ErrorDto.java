package com.socialnetwork.chat.dto;

import com.socialnetwork.chat.util.enums.ErrorCodeException;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class ErrorDto {

    @ApiModelProperty(
        example = "Chat room exists",
        notes = "Explanation of the error")
    private String message;

    @ApiModelProperty(
        example = "1026",
        notes = "Some unique reserved code")
    private ErrorCodeException errorCode;
}
