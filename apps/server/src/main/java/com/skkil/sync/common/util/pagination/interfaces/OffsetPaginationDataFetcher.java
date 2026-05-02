package com.skkil.sync.common.util.pagination.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@FunctionalInterface
public interface OffsetPaginationDataFetcher<T> {

  Page<T> fetch(Pageable pageable);
}
