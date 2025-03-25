
package nirvi.project.media_delivery_streaming.cdn;

import java.io.IOException;
public interface EdgeService {
    byte[] getFile(String fileName, int width, int height) throws IOException;

    void storeFile(String fileName, byte[] data);        // Create

    void updateFile(String fileName, byte[] data);       // Update

    void deleteFile(String fileName);                    // Delete
}