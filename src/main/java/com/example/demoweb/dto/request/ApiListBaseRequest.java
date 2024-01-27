package com.example.demoweb.dto.request;

import com.example.demoweb.enums.TypeSort;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiListBaseRequest {
    protected String orderBy = "serial";
    protected TypeSort orderDirection = TypeSort.ASC;
    protected Integer size = 10;
    protected Integer page = 0;
}
