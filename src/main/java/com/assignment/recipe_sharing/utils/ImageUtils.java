package com.assignment.recipe_sharing.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ImageUtils {

    public static String uploadImage(String id, MultipartFile imageFile, String path) throws
            HttpMediaTypeNotSupportedException {
        try {
            InputStream inputStream = imageFile.getInputStream();
            String extension = FilenameUtils.getExtension(imageFile.getOriginalFilename());
            if (extension != null && (extension.contains("jpg") ||
                    extension.contains("jpeg") || extension.contains("png"))) {
                String imageFileName = id + "_" + imageFile.getOriginalFilename();
                File targetImageFile = new File(path + imageFileName);
                Files.copy(inputStream, targetImageFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } else {
                throw new HttpMediaTypeNotSupportedException(extension + " file type is not " +
                        "supported. Supported file types are jpeg, jpg or png");

            }
        } catch (IOException e) {
            System.out.println("Exception occurred :" + e.getMessage());
        }
        return id;
    }

    public static List<byte[]> getImage(String id, String path) {
        try {
            File f = new File(path);
            File[] matchingFiles = f.listFiles((dir, name) -> name.startsWith(id));
            List<byte[]> imageFiles = new ArrayList<>();
            if (matchingFiles != null && matchingFiles.length > 0) {
                for (File img : matchingFiles) {
                    FileInputStream fileInputStream = new FileInputStream(img);
                    byte[] bytes = IOUtils.toByteArray(fileInputStream);
                    imageFiles.add(bytes);
                }
                return imageFiles;
            } else {
                return null;
            }

        } catch (IOException e) {
            System.out.println("Exception occured :" + e.getMessage());
            return null;
        }
    }
}
