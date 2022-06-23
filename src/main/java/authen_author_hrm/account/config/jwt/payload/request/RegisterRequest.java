package authen_author_hrm.account.config.jwt.payload.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class RegisterRequest {
    @NotEmpty(message = "Chưa nhập username!")
    @Size(min = 3, max = 20, message = "tài khoản phải từ 3 đến 20")
    private String username;

    @NotEmpty(message = "Chưa nhập email!")
    @Email(message = "Email không hợp lệ")
    @Size(max = 50)
    private String email;

    @NotEmpty(message = "Chưa nhập password!")
    @Size(min = 8,message = "password phải từ 8 kí tự trở lên!")
    private String password;

    private Set < String > role;

}
