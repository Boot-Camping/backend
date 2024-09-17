package com.github.project3.dto.mypage;

import com.github.project3.entity.camp.CampEntity;
import com.github.project3.entity.camp.CampImageEntity;
import com.github.project3.entity.wishlist.WishlistEntity;
import com.github.project3.entity.wishlist.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MypageCampResponse {
    private Integer id;
    private Integer wishId;
    private Status wishStatus;
    private String name;
    private String addr;
    private Integer price;
    private List<String> campImages;

    // 스태틱 팩토리 메서드
    public static MypageCampResponse from(WishlistEntity wishlistEntity) {
        MypageCampResponse response = new MypageCampResponse();
        CampEntity camp = wishlistEntity.getCamp();

        response.id = camp.getId();
        response.wishId = wishlistEntity.getId();
        response.wishStatus = wishlistEntity.getStatus();
        response.name = camp.getName();
        response.addr = camp.getAddr();
        response.price = camp.getPrice();
        response.campImages = camp.getImages().stream().map(CampImageEntity::getImageUrl).collect(Collectors.toList());

        return response;
    }
}
