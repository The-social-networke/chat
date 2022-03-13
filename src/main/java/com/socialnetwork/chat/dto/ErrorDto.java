package com.socialnetwork.chat.dto;

import com.socialnetwork.chat.util.enums.ErrorCodeException;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class ErrorDto {

    private String message;

    private ErrorCodeException errorCode;
}
