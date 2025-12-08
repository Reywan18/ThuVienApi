package com.tlu.thuvien.application.mapper;

import com.tlu.thuvien.api.dto.response.book.BookResponse;
import com.tlu.thuvien.domain.entity.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookResponse toResponse(Book book);
}