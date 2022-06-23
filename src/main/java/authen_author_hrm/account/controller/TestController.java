package authen_author_hrm.account.controller;

import authen_author_hrm.account.config.jwt.payload.request.EditAuthor;
import authen_author_hrm.account.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    UserService userService;

    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('HR') or hasRole('ADMIN')")
    public String userAccess() {

        return "User Content.";
    }

    @GetMapping("/hr")
    @PreAuthorize("hasRole('HR')")
    public String hrAccess() {
        return "Hr Board.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    //hasRole(“ADMIN”): Cho phép những user đã xác thực và có GrantedAuthority là ROLE_ADMIN mới được truy cập.
    public String adminAccess() {
        return "Admin Board.";
    }

    @PutMapping("/edit")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('HR')")
    public ResponseEntity < ? > editAuthor(@RequestBody EditAuthor editAuthor) {
        return ResponseEntity.ok(userService.editAuthor(editAuthor));
    }

}
