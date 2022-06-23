package authen_author_hrm.account.config;

import authen_author_hrm.account.config.jwt.AuthEntryPointJwt;
import authen_author_hrm.account.config.jwt.AuthTokenFilter;
import authen_author_hrm.account.service.userDetail.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter { //Để kích hoạt Spring Security trước tiên ta cần phải có một class kế thừa Abstract class  WebSecurityConfigurerAdapter

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    //AuthenticationManager thực ra là một vùng chứa cho các AuthenticationProvider,  tạo ra một interface nhất quán cho tất cả chúng. Trong hầu hết các trường hợp thì AuthenticationManager được config mặc định là quá đủ.Nhìn chung, AuthenticationManager truyền một số AuthenticationToken tới mỗi AuthenticationProvider  và kiểm tra nó. Nếu AuthenticationProvider có thể dùng nó để xác thực thì nó sẽ trả về 1 chỉ dẫn “Authenticated” hay “Unauthenticated” hay “Could not aunthenticate”.
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //Trong Spring Security, việc mã hóa mật khẩu sẽ do interface PasswordEncoder đảm nhận. PasswordEncoder có implementation là BCryptPasswordEncoder sẽ giúp chúng ta mã hóa mật khẩu bằng thuật toán BCrypt. Nhưng để sử dụng được PasswordEncoder, ta phải cấu hình để PasswordEncoder trở thành một Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Nếu bạn không Override phương thức này thì Spring Security sẽ sử dụng config mặc định như khi bạn vừa khởi tạo Spring boot vs Security và run, thì ở mặc định tất cả các link của bạn thiết lập đều phải Authenticated mới được truy cập vào.
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()//dòng này có nghĩa bạn sẽ không dùng cơ chế chống (Cross-site Request Forgery) cho project vì JavaWebToken đã thực hiện được.
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler) //dòng này ta sẽ bắt lỗi nếu người dùng chưa xác thực tài khoản mà truy cập vào những đường link yêu cầu phải xác thực tài khoản trước và xử lý lỗi rồi trả về mã lỗi “unauthorized” tại url
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)//mặc định spring security sẽ sử dụng IF_REQUIRED nghĩa là Session sẽ được tạo chỉ khi được yêu cầu. Ví dụ: nếu bạn dùng formLogin() config mặc định của Spring Security, thì sau khi login thì Security sẽ tự động tạo 1 session lưu token vừa tạo ra, để mỗi lần bạn request đến các url yêu cầu xác thực, thì tự động Spring Security sẽ lấy token từ session và đưa vào xác thực để truy cập vào các url đó. Còn STATELESS nghĩa là không tạo hay sử dụng Session trong Spring security
                .and()
                .authorizeRequests()//dòng này sẽ thiết lập quyền truy cập cho các url, nếu theo sau là:
                .antMatchers("/auth/**").permitAll() //Cho phép tất cả user truy cập không cần xác thực.
                .antMatchers("/test/**").permitAll()
                .anyRequest().authenticated();
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class); //dòng này sẽ giúp cấu hình các filter để xử lý các yêu cầu đăng nhập và xác thực.
    }
}

