package com.example.demoweb.utils;


import com.example.demoweb.dto.request.ApiListBaseRequest;
import com.example.demoweb.enums.TypeSort;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


@UtilityClass
public class FilterDataUtil {
    public static PageRequest buildPageRequest(ApiListBaseRequest filter) {
        if (TypeSort.DESC.equals(filter.getOrderDirection())) {
            return PageRequest.of(filter.getPage(), filter.getSize(),
                    Sort.by(filter.getOrderBy()).descending());
        }
        return PageRequest.of(filter.getPage(), filter.getSize(),
                Sort.by(filter.getOrderBy()).ascending());
    }

    public static PageRequest buildPageRequest(Integer page, Integer pageSize, String field,
                                               TypeSort type) {
        if (TypeSort.DESC.equals(type)) {
            return PageRequest.of(page, pageSize, Sort.by(field).descending());
        }
        return PageRequest.of(page, pageSize, Sort.by(field).ascending());
    }

    public static PageRequest buildPageRequestDefault(String field, Integer page, Integer pageSize) {
        return PageRequest.of(page, pageSize, Sort.by(field).ascending());
    }
}
