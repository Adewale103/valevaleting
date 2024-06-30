package com.project.valevaleting.dto.response;

import com.project.valevaleting.enums.ResponseStatus;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageDto {
    private ResponseStatus status;
    private List<?> data;
    private boolean hasMore;
    private int currentPage;
    private int totalPages;
    private long totalItems;


    public static PageDto build(Page<?> page, List<?> data) {
        return PageDto.builder().totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .totalItems(page.getTotalElements())
                .hasMore(page.hasNext())
                .status(ResponseStatus.SUCCESS)
                .data(data).build();
    }
}
