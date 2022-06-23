package authen_author_hrm.account.config.jwt.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditAuthor {
    @NotBlank(message = "Chưa chọn quyền")
    private String role;

    @NotBlank(message = "Chưa chọn tài khoản")
    private Long userId;
}
