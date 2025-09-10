package com.soumen.kalpavriksha.Utills;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import org.springframework.web.multipart.MultipartFile;

public class PlantUtil
{
    private String convertIntoBase64Image(MultipartFile image)
    {
        String base64Image = null;
        try {
            base64Image = Base64.getEncoder().encodeToString(image.getBytes());
            return base64Image;
        } catch (IOException e) {
            System.out.println("Inside identifyPlant method exception inside controller");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
