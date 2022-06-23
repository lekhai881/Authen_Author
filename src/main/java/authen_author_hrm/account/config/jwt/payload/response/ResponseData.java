package authen_author_hrm.account.config.jwt.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseData {
    private String status;
    private String message;
    private Object data;

    public ResponseData(String status, String message) {
        this.status = status;
        this.message = message;
    }
}

