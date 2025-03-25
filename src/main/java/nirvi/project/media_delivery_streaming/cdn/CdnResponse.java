package nirvi.project.media_delivery_streaming.cdn;

import lombok.Builder;
import lombok.Data;
import java.util.Base64;

@Builder
@Data
public class CdnResponse {
    private String contentId;
    private String bucketName;
    private long fileSize;
    private String fileType;
    private byte[] fileData; // Store raw bytes instead of String

    /**
     * Encodes the byte array to a Base64 string for JSON responses.
     */
    public String getFileDataAsBase64() {
        return Base64.getEncoder().encodeToString(fileData);
    }

    /**
     * Converts from JSON Base64 encoded string back to byte array.
     */
    public static CdnResponse fromBase64(String contentId, String bucketName, long fileSize, String fileType, String base64Data) {
        return CdnResponse.builder()
                .contentId(contentId)
                .bucketName(bucketName)
                .fileSize(fileSize)
                .fileType(fileType)
                .fileData(Base64.getDecoder().decode(base64Data))
                .build();
    }
}
