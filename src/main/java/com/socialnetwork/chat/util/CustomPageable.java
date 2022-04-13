package com.socialnetwork.chat.util;

import io.swagger.annotations.ApiParam;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Data
public class CustomPageable {

    @Positive(message = "Size should be positive")
    @Min(value = 5, message = "The number of items per page should be in the range of 5 to 20")
    @Max(value = 20, message = "The number of items per page should be in the range of 5 to 20")
    @ApiParam(value = "Amount of items per page is in the range of 5 to 20", defaultValue = "10")
    private Integer size = 10;

    @PositiveOrZero(message = "Current page should be positive or zero")
    @ApiParam(value = "Current set of page elements", defaultValue = "0")
    private Integer offset = 0;

    private Sort sort = Sort.unsorted();

    public Pageable toPageable(){
        return PageRequest.of(offset, size, sort);
    }
}
