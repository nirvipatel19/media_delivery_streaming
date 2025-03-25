package nirvi.project.media_delivery_streaming.cdn;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CdnRequest {
    private String contentId;  // Unique ID for the file
    private String bucketName; // S3 Bucket Name
}
