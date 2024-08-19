package com.github.project3.dto.mypage;

import com.github.project3.entity.camp.CampEntity;
import com.github.project3.entity.camp.CampImageEntity;
import com.github.project3.entity.user.UserImageEntity;
import com.github.project3.entity.wishlist.WishlistEntity;
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
    private List<Integer> wishId;
    private String name;
    private String addr;
    private Integer price;
    private List<String> campImages;

    // 스태틱 팩토리 메서드
    public static MypageCampResponse from(CampEntity camp) {
        MypageCampResponse response = new MypageCampResponse();
        response.id = camp.getId();
        response.wishId = camp.getWishlist().stream().map(WishlistEntity::getId).collect(Collectors.toList());
        response.name = camp.getName();
        response.addr = camp.getAddr();
        response.price = camp.getPrice();
        response.campImages = camp.getImages().stream().map(CampImageEntity::getImageUrl).collect(Collectors.toList());

        return response;
    }
}
