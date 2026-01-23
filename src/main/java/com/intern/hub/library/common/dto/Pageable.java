package com.intern.hub.library.common.dto;

import java.util.Collection;
import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = PRIVATE)
public class Pageable <T> {

  Collection<T> items;

  long totalItems;

  int totalPages;

  public static <T> Pageable<T> empty() {
    return Pageable.<T>builder()
        .items(List.of())
        .totalItems(0)
        .totalPages(0)
        .build();
  }

}
