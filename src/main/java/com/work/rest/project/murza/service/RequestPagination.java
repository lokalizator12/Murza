package com.work.rest.project.murza.service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Set;
public final class RequestPagination {
    private RequestPagination() {}
    public static Pageable page(int page, int size, Sort sort) {
        if (page < 0 || page > 100000 || size < 1 || size > 100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose a page size between 1 and 100 and a valid page number");
        return PageRequest.of(page, size, sort);
    }
    public static Sort sort(String field, String direction, String fallback, Set<String> allowed) {
        String selected = field == null || field.isBlank() ? fallback : field;
        if (!allowed.contains(selected)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This sorting field is not supported");
        try {return Sort.by(direction == null || direction.isBlank() ? Sort.Direction.ASC : Sort.Direction.fromString(direction), selected);}
        catch (IllegalArgumentException exception) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Choose ASC or DESC sorting");}
    }
}
