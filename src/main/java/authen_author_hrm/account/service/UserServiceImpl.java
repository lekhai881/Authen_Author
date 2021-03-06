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
            return new ResponseData("false", registerRequest.getEmail() + " ???? t???n t???i! Nh???p email m???i.");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Set < Role > role = new HashSet <>();
        Role findRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("message: Role kh??ng t???n t???i!"));
        role.add(findRole);
        user.setRoles(role);
        userRepository.save(user);
        return new ResponseData("true", "????ng k?? t??i kho???n " + registerRequest.getUsername() + " th??nh c??ng!");
    }

    @Override
    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
//UsernamePasswordAuthenticationToken l?? class ch???a c??c th??ng tin c???n thi???t ????? x??c th???c v?? ph??n quy???n cho m???t user.
//Sau khi ki???m tra username, password th??nh c??ng ????? Security x??c th???c ng?????i d??ng ???? ???? ????ng nh???p v??o h??? th???ng th?? ta l??m nh?? sau:
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);
//Principal ????n gi???n ch??? l?? m???t ?????i t?????ng v?? s??? ???????c ??p ki???u sang UserDetails.
// UserDetails l?? m???t interface c???t l??i c???a Spring Security. N?? ?????i di???n cho m???t principal nh??ng theo m???t c??ch m??? r???ng v?? c??? th??? h??n.
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
        return new ResponseData("true", "???? c???p nh???t quy???n cho t??i kho???n th??nh c??ng!",responseAuthor);
    }

}
