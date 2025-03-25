package nirvi.project.media_delivery_streaming.model;

import lombok.Builder;
import lombok.Data;
import java.io.InputStream;
import java.util.Date;

@Data
@Builder
public class FileResponse {
    private InputStream fileStream;
    private String fileName;
    private String contentType;
    private long fileSize; // Size in bytes
    private double fileSizeMB; // Size in MB
    private double uploadTimeSec; // Upload time in seconds
    private double bandwidthMBps; // Bandwidth in MB/sec
    private Date uploadedAt;
    private String message;
}
