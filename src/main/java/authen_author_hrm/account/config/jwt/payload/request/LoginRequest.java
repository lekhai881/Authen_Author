package authen_author_hrm.account.config.jwt.payload.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {

    @NotBlank(message = "Chưa nhập email!")
    @Email(message = "Chưa nhập đúng định dạng email!")
    private String email;

  /*  @NotBlank(message = "Chưa nhập tên tài khoản!")
    private String username;*/

    @NotBlank(message = "Chưa nhập password!")
    private String password;
}
