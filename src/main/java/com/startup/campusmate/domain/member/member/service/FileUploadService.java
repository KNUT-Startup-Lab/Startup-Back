package com.startup.campusmate.domain.member.member.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${file.image-upload-dir}")
    private String uploadDir;

    public String saveFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("파일이 비어있습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        // 중복 방지를 위해 UUID를 파일명에 추가
        String savedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path savedPath = Paths.get(uploadDir, savedFilename);

        try {
            Files.createDirectories(savedPath.getParent()); // 디렉토리가 없으면 생성
            Files.copy(file.getInputStream(), savedPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("파일을 저장할 수 없습니다.", e);
        }

        // 클라이언트가 접근할 수 있는 URL 경로를 반환 (예: /gen/savedFilename.jpg)
        return "/gen/" + savedFilename;
    }

    public void deleteFile(String fileUrl) {
        // fileUrl에서 파일명만 추출 (예: /gen/abc.jpg -> abc.jpg)
        String filename = Paths.get(fileUrl).getFileName().toString();
        try {
            Files.deleteIfExists(Paths.get(uploadDir, filename));
        } catch (IOException e) {
            // 로깅 처리
        }
    }
}