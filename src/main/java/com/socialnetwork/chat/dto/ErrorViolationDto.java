package com.socialnetwork.chat.dto;

import com.socialnetwork.chat.util.FieldViolationsError;
import com.socialnetwork.chat.util.enums.ErrorCodeException;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Map;

@Data
@Jacksonized
@Builder
public class ErrorViolationDto {

    private String message;

    private ErrorCodeException errorCode;

    private Map<String, List<FieldViolationsError>> violations;
}
