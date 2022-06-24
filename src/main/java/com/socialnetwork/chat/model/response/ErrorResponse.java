package com.socialnetwork.chat.model.response;

import com.socialnetwork.chat.model.enums.ErrorCodeException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class ErrorResponse {

    @Schema(
        example = "Chat room exists",
        description = "Explanation of the error")
    private String message;

    @Schema(
        example = "1026",
        description = "Some unique reserved code")
    private ErrorCodeException errorCode;
}
