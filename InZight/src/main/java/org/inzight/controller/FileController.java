package org.inzight.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inzight.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    /**
     * Endpoint để serve file ảnh
     */
    @GetMapping("/{subfolder}/{filename:.+}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String subfolder,
            @PathVariable String filename) {
        try {
            log.debug("Requesting file: {}/{}", subfolder, filename);
            Resource resource = fileStorageService.loadFileAsResource(subfolder, filename);
            String contentType = "application/octet-stream";
            
            // Xác định content type dựa trên extension
            String lowerFilename = filename.toLowerCase();
            if (lowerFilename.endsWith(".jpg") || lowerFilename.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            } else if (lowerFilename.endsWith(".png")) {
                contentType = "image/png";
            } else if (lowerFilename.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (lowerFilename.endsWith(".webp")) {
                contentType = "image/webp";
            }

            log.debug("Serving file: {}/{} with content-type: {}", subfolder, filename, contentType);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "inline; filename=\"" + URLEncoder.encode(filename, StandardCharsets.UTF_8) + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600") // Cache 1 giờ
                    .body(resource);
        } catch (Exception e) {
            log.error("Error serving file: {}/{}", subfolder, filename, e);
            return ResponseEntity.notFound().build();
        }
    }
}

