package com.github.project3.dto.camp;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class CampPageResponse {
	private List<CampResponse> content;
	private int pageNumber;
	private int pageSize;
	private long totalElements;
	private int totalPages;

	public CampPageResponse(Page<CampResponse> page) {
		this.content = page.getContent();
		this.pageNumber = page.getNumber();
		this.pageSize = page.getSize();
		this.totalElements = page.getTotalElements();
		this.totalPages = page.getTotalPages();
	}
}
