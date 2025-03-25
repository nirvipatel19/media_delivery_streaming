package nirvi.project.media_delivery_streaming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@AllArgsConstructor
@Data
@Builder
public class FileResponsedto implements Serializable {
    private String fileName;
    private String fileURl;
    private String contentType;
    private String fileSize;
    private Date uploadedAt;
    private String message;

    public static String formatFileSize(long bytes) {
        if (bytes >= 1024 * 1024 * 1024) {
            return (bytes / (1024 * 1024 * 1024)) + "GB";
        } else if (bytes >= 1024 * 1024) {
            return (bytes / (1024 * 1024)) + "MB";
        } else if (bytes >= 1024) {
            return (bytes / 1024) + "KB";
        } else {
            return bytes + "B";
        }
    }
}