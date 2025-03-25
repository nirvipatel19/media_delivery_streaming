package nirvi.project.media_delivery_streaming.dto;


import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ErrorInfo {
    private int errorCode;
    private String message;
    private Date timeStamp;
}
