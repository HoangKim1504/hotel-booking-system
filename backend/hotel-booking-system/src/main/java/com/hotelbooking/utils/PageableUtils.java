package com.hotelbooking.utils;

import com.hotelbooking.dto.PageResponse;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

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

    /**
     * Phân trang thủ công cho một danh sách dữ liệu đã có sẵn.
     *
     * @param responsesList danh sách dữ liệu cần phân trang
     * @param currentPage   trang hiện tại, bắt đầu từ 1
     * @param pageSize      số record tối đa mỗi trang
     * @return PageResponse chứa dữ liệu và thông tin phân trang
     */
    public <T> PageResponse<T> addPagingAttributes(
            List<T> responsesList,
            int currentPage,
            int pageSize
    ) {
        int totalRecords = responsesList.size();
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        int fromIndex = (currentPage - 1) * pageSize;

        // Trang hiện tại vượt quá tổng số record
        if (fromIndex >= totalRecords) {
            return new PageResponse<>(
                    List.of(),
                    totalRecords,
                    totalPages,
                    currentPage,
                    pageSize
            );
        }

        int toIndex = Math.min(fromIndex + pageSize, totalRecords);

        // Lấy dữ liệu thuộc trang hiện tại
        List<T> items = responsesList.subList(fromIndex, toIndex);

        return new PageResponse<>(
                items,
                currentPage,
                pageSize,
                totalRecords,
                totalPages
        );
    }

}