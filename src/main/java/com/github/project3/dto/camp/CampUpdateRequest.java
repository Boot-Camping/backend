package com.github.project3.dto.camp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampUpdateRequest {
	private Integer id;
	private String name;
	private Integer price;
	private String addr;
	private Integer maxNum;
	private Integer standardNum;
	private Integer overCharge;
	private List<MultipartFile> imageFiles;
	private String description;
	private List<String> categories;

}
