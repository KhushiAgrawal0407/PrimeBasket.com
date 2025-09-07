package com.ecommerce.PrimeBasket.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImplementation implements FileService {
    public String uploadImage(String path, MultipartFile file) throws IOException {
        //get the file name of current/original file.
        String originalFileName= file.getOriginalFilename();

        //generate a unique file name (by using the concept of random uuid).
        String randomId = UUID.randomUUID().toString();
        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        String filePath = path + File.separator + fileName;

        //check if path exists or create one.
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        //upload to server
        Files.copy(file.getInputStream(), Paths.get(filePath));

        //return file name
        return fileName;
    }
}
