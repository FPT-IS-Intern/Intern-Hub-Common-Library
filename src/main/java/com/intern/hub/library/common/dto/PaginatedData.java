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
public class PaginatedData<T> {

  Collection<T> items;

  long totalItems;

  int totalPages;

  /**
   * Creates an empty PaginatedData instance.
   * @return an empty PaginatedData
   * @param <T> the type of the data payload
   */
  public static <T> PaginatedData<T> empty() {
    return PaginatedData.<T>builder()
        .items(List.of())
        .totalItems(0)
        .totalPages(0)
        .build();
  }

}
