package com.github.project3.controller.mypage;

import com.github.project3.service.mypage.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ImageController {

    private ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService){
        this.imageService = imageService;
    }

    @PostMapping("/image/upload")
    @ResponseBody
    public Map<String, Object> imageUpload(MultipartRequest request) throws Exception{

        Map<String, Object> responseData = new HashMap<>();
        try {
            String s3Url = imageService.imageUpload(request);

            responseData.put("uploaded", true);
            responseData.put("url", s3Url);

            return responseData;
        } catch (IOException e) {

            responseData.put("uploaded", false);

            return responseData;
        }
    }
}
