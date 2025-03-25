package nirvi.project.media_delivery_streaming.controller;

import com.amazonaws.services.s3.model.S3ObjectInputStream;
import nirvi.project.media_delivery_streaming.service.S3EdgeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import java.io.IOException;

@RestController
@RequestMapping("/s3")
public class S3Controller {
    private final S3EdgeService s3EdgeService;

    public S3Controller(S3EdgeService s3EdgeService) {
        this.s3EdgeService = s3EdgeService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            byte[] fileData = file.getBytes();
            s3EdgeService.storeFile(file.getOriginalFilename(), fileData);
            return ResponseEntity.ok("File uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateFile(@RequestParam("file") MultipartFile file) {
        try {
            byte[] fileData = file.getBytes();
            s3EdgeService.updateFile(file.getOriginalFilename(), fileData);
            return ResponseEntity.ok("File updated successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File update failed: " + e.getMessage());
        }
    }


    @GetMapping("/stream/{fileName}")
    public ResponseEntity<byte[]> streamFile(@PathVariable String fileName) {
        try {
            byte[] fileData = s3EdgeService.getFile(fileName, 0, 0); // Fix: Pass width & height
            long contentLength = s3EdgeService.getFileSize(fileName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(getContentType(fileName)));
            headers.setContentLength(contentLength);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileData); // Returning file as byte[]
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }



    private String getContentType(String fileName) {
        if (fileName.endsWith(".mp4")) return "video/mp4";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".png")) return "image/png";
        return "application/octet-stream";
    }
}
