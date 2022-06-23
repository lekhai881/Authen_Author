package authen_author_hrm.account.service;

import authen_author_hrm.account.config.jwt.payload.request.EditAuthor;
import authen_author_hrm.account.config.jwt.payload.request.LoginRequest;
import authen_author_hrm.account.config.jwt.payload.request.RegisterRequest;
import authen_author_hrm.account.config.jwt.payload.response.JwtResponse;
import authen_author_hrm.account.config.jwt.payload.response.ResponseData;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    ResponseData register(RegisterRequest registerRequest);

    JwtResponse login(LoginRequest loginRequest);

    ResponseData editAuthor(EditAuthor editAuthor);
}
