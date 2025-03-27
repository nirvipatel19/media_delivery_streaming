package nirvi.project.media_delivery_streaming.service;

import java.io.InputStream;
import java.util.Optional;

public interface IOriginService {

    Optional<InputStream> getFile(String fileName); // ✅ Return Optional to avoid null

    void putFile(String fileName, InputStream inputStream, long contentLength); // ✅ Store file in S3

    void removeFile(String fileName); // ✅ Remove file from S3
}
