package com.hotelbooking.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@UtilityClass
public class PageableUtils {

    public Pageable createPageable(int currentPage, int pageSize, String sortBy, String order) {
        Pageable pageable;

        // Set vị trí trang hiện tại và lượng record max mỗi trang
        // Lưu ý: API bắt đầu từ page = 1, Spring Data Pageable bắt đầu từ page = 0
        int pageNumber = currentPage - 1;

        // Không truyền sortBy → chỉ pagination
        if (sortBy == null || sortBy.isBlank()) {
            pageable = PageRequest.of(pageNumber, pageSize);
        } else {
            pageable = PageRequest.of(
                    pageNumber,
                    pageSize,
                    Sort.Direction.fromString(order),
                    sortBy
            );
        }

        return pageable;
    }

}