package vn.edu.fpt.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(MultipartFile file, String folder) {
        try {
            //Chuyển MultipartFile -> byte[]
            byte[] fileBytes = file.getBytes();

            //Upload lên Cloudinary
            Map result = this.cloudinary.uploader().upload(fileBytes,
                    ObjectUtils.asMap(
                            "folder", folder,
                            "resource_type", "auto"
                    ));
            // Trả về URL public
            return result.get("secure_url").toString();
            // → "https://res.cloudinary.com/xxx/image/upload/avatars/abc.jpg"
        } catch (IOException e) {
            throw new RuntimeException("Upload ảnh thất bại: " + e.getMessage());
        }
    }

    public void deleteFile(String imageUrl) {
        try {
            // Lấy publicId từ URL
            // URL: https://res.cloudinary.com/xxx/image/upload/avatars/abc.jpg
            // publicId: avatars/abc
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Xóa ảnh thất bại: " + e.getMessage());
        }
    }

    private String extractPublicId(String imageUrl) {
        //cắt lấy phần sau "upload/"
        String[] parts = imageUrl.split("upload/");
        String afterUpload = parts[1];  //"avartar/abc.jpg
        //Bỏ đuôi file
        return afterUpload.substring(0, afterUpload.lastIndexOf("."));
    }
}
