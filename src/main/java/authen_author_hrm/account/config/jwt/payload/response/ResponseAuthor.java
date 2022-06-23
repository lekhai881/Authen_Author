package authen_author_hrm.account.config.jwt.payload.response;

import authen_author_hrm.account.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseAuthor {
    private String username;
    private String roles;

}
