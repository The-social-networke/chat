package com.socialnetwork.chat.model.response;

import com.socialnetwork.chat.model.enums.ErrorCodeException;
import com.socialnetwork.chat.util.FieldViolationsError;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Map;

@Data
@Jacksonized
@Builder
public class ErrorViolationRequest {

    private String message;

    private ErrorCodeException errorCode;

    private Map<String, List<FieldViolationsError>> violations;
}
