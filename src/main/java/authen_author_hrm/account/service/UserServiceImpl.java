package authen_author_hrm.account.service;

import authen_author_hrm.account.config.jwt.JwtUtils;
import authen_author_hrm.account.config.jwt.payload.request.EditAuthor;
import authen_author_hrm.account.config.jwt.payload.request.LoginRequest;
import authen_author_hrm.account.config.jwt.payload.request.RegisterRequest;
import authen_author_hrm.account.config.jwt.payload.response.JwtResponse;
import authen_author_hrm.account.config.jwt.payload.response.ResponseAuthor;
import authen_author_hrm.account.config.jwt.payload.response.ResponseData;
import authen_author_hrm.account.model.ERole;
import authen_author_hrm.account.model.Role;
import authen_author_hrm.account.model.User;
import authen_author_hrm.account.repository.RoleRepository;
import authen_author_hrm.account.repository.UserRepository;
import authen_author_hrm.account.service.userDetail.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public ResponseData register(RegisterRequest registerRequest) {
        Boolean findEmail = userRepository.existsByEmail(registerRequest.getEmail());
        if (findEmail) {
            return new ResponseData("false", registerRequest.getEmail() + " đã tồn tại! Nhập email mới.");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Set < Role > role = new HashSet <>();
        Role findRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("message: Role không tồn tại!"));
        role.add(findRole);
        user.setRoles(role);
        userRepository.save(user);
        return new ResponseData("true", "Đăng kí tài khoản " + registerRequest.getUsername() + " thành công!");
    }

    @Override
    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
//UsernamePasswordAuthenticationToken là class chứa các thông tin cần thiết để xác thực và phân quyền cho một user.
//Sau khi kiểm tra username, password thành công để Security xác thực người dùng đó đã đăng nhập vào hệ thống thì ta làm như sau:
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);
//Principal đơn giản chỉ là một đối tượng và sẽ được ép kiểu sang UserDetails.
// UserDetails là một interface cốt lõi của Spring Security. Nó đại diện cho một principal nhưng theo một cách mở rộng và cụ thể hơn.
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List < String > roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        return new JwtResponse(jwt,
                // userDetails.getId(),
                userDetails.getUsername(),
                // userDetails.getEmail(),
                roles
        );
    }

    @Override
    public ResponseData editAuthor(EditAuthor editAuthor) {
        String strRole = editAuthor.getRole();
        Set < Role > roles = new HashSet <>();
        switch (strRole) {
            case "ROLE_HR":
                Role roleHr = roleRepository.findByName(ERole.ROLE_HR)
                        .orElseThrow(() -> new RuntimeException("message: Khong tim thay role!"));
                roles.add(roleHr);
                break;
            case "ROLE_ADMIN":
                Role roleAdmin = roleRepository.findByName(ERole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("message: Khong tim thay role!"));
                roles.add(roleAdmin);
                break;
            default:
                Role roleUser = roleRepository.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("message: Khong tim thay role!"));
                roles.add(roleUser);
        }
        userRepository.findById(editAuthor.getUserId()).map(user -> {
            user.setRoles(roles);
            return userRepository.save(user);
        });
        ResponseAuthor responseAuthor = new ResponseAuthor();
        responseAuthor.setUsername(userRepository.findUsername(editAuthor.getUserId()));
        responseAuthor.setRoles(strRole);
        return new ResponseData("true", "Đã cập nhật quyền cho tài khoản thành công!",responseAuthor);
    }

}
