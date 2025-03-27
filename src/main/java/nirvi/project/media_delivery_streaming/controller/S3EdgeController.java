package nirvi.project.media_delivery_streaming.controller;

import nirvi.project.media_delivery_streaming.service.S3EdgeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class S3EdgeController {

    private final S3EdgeService s3EdgeService;

    public S3EdgeController(S3EdgeService s3EdgeService) {
        this.s3EdgeService = s3EdgeService;
    }

    // ✅ Upload File
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            s3EdgeService.storeFile(file.getOriginalFilename(), file.getBytes());
            return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
        }
    }

    // ✅ Stream File (Supports Image Resizing)
    @GetMapping("/stream/{fileName}")
    public ResponseEntity<byte[]> streamFile(
            @PathVariable String fileName,
            @RequestParam(required = false, defaultValue = "0") int width,
            @RequestParam(required = false, defaultValue = "0") int height) {
        try {
            byte[] fileData = (width > 0 && height > 0) ?
                    s3EdgeService.getFile(fileName, width, height) :
                    s3EdgeService.streamFile(fileName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(fileData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // ✅ Update File
    @PutMapping("/update/{fileName}")
    public ResponseEntity<String> updateFile(@PathVariable String fileName, @RequestParam("file") MultipartFile file) {
        try {
            s3EdgeService.updateFile(fileName, file.getBytes());
            return ResponseEntity.ok("File updated successfully: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating file: " + e.getMessage());
        }
    }

    // ✅ Delete File
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        s3EdgeService.deleteFile(fileName);
        return ResponseEntity.ok("File deleted successfully: " + fileName);
    }

    // ✅ Get File Size
    @GetMapping("/size/{fileName}")
    public ResponseEntity<String> getFileSize(@PathVariable String fileName) {
        long size = s3EdgeService.getFileSize(fileName);
        return ResponseEntity.ok("File size: " + size + " bytes");
    }
}
