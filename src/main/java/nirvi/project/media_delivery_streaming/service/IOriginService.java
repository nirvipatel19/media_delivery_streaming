package nirvi.project.media_delivery_streaming.service;

import java.io.File;

public interface IOriginService {
    File getFile(String fileName);
    void putFile(String fileName, File file);
}
