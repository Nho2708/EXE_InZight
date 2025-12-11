package org.inzight.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Value("${app.base-url:}")
    private String configuredBaseUrl;

    /**
     * Lấy base URL từ request hoặc cấu hình
     */
    private String getBaseUrl() {
        // Ưu tiên lấy từ request (động)
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                // Kiểm tra ngrok headers trước
                String forwardedHost = request.getHeader("X-Forwarded-Host");
                String forwardedProto = request.getHeader("X-Forwarded-Proto");
                
                if (forwardedHost != null && !forwardedHost.isEmpty()) {
                    // Đang chạy qua proxy (ngrok, reverse proxy, etc.)
                    String scheme = forwardedProto != null ? forwardedProto : "https";
                    String baseUrl = scheme + "://" + forwardedHost;
                    log.debug("Using base URL from X-Forwarded headers: {}", baseUrl);
                    return baseUrl;
                }
                
                // Không có forwarded headers, dùng request trực tiếp
                String scheme = request.getScheme(); // http hoặc https
                String serverName = request.getServerName(); // domain hoặc IP
                int serverPort = request.getServerPort();
                
                // Xây dựng base URL từ request
                StringBuilder baseUrl = new StringBuilder();
                baseUrl.append(scheme).append("://").append(serverName);
                
                // Chỉ thêm port nếu không phải port mặc định
                if ((scheme.equals("http") && serverPort != 80) || 
                    (scheme.equals("https") && serverPort != 443)) {
                    baseUrl.append(":").append(serverPort);
                }
                
                String dynamicUrl = baseUrl.toString();
                log.debug("Using dynamic base URL from request: {}", dynamicUrl);
                return dynamicUrl;
            }
        } catch (Exception e) {
            log.warn("Could not get base URL from request, using configured value", e);
        }
        
        // Fallback: dùng cấu hình
        if (configuredBaseUrl != null && !configuredBaseUrl.isEmpty()) {
            return configuredBaseUrl;
        }
        
        // Fallback cuối cùng: localhost
        log.warn("No base URL configured, using localhost:8080");
        return "http://localhost:8080";
    }

    /**
     * Upload file và trả về URL công khai
     */
    public String uploadFile(MultipartFile file, String subfolder) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("File is empty");
            }

            // Tạo tên file unique
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;

            // Tạo thư mục nếu chưa tồn tại
            Path uploadPath = Paths.get(uploadDir, subfolder);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Lưu file
            Path targetLocation = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Trả về URL công khai (sử dụng base URL động)
            String baseUrl = getBaseUrl();
            String fileUrl = baseUrl + "/api/files/" + subfolder + "/" + filename;
            log.info("File uploaded successfully: {}", fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error("Error uploading file", e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    /**
     * Load file từ storage
     */
    public Resource loadFileAsResource(String subfolder, String filename) {
        try {
            Path filePath = Paths.get(uploadDir, subfolder, filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + filename);
            }
        } catch (Exception e) {
            log.error("Error loading file", e);
            throw new RuntimeException("File not found: " + filename, e);
        }
    }

    /**
     * Xóa file
     */
    public void deleteFile(String subfolder, String filename) {
        try {
            Path filePath = Paths.get(uploadDir, subfolder, filename).normalize();
            Files.deleteIfExists(filePath);
            log.info("File deleted: {}", filePath);
        } catch (IOException e) {
            log.error("Error deleting file", e);
            throw new RuntimeException("Failed to delete file: " + filename, e);
        }
    }
}

