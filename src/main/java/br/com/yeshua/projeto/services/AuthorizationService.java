package br.com.yeshua.projeto.services;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.yeshua.projeto.config.security.TokenService;
import br.com.yeshua.projeto.dtos.AuthetinticationDto;
import br.com.yeshua.projeto.dtos.LoginResponseDto;
import br.com.yeshua.projeto.dtos.RegisterDto;
import br.com.yeshua.projeto.model.User;
import br.com.yeshua.projeto.repositoriy.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Service
public class AuthorizationService implements UserDetailsService {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    private AuthenticationManager authenticationManager;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    public ResponseEntity<Object> login(@RequestBody @Valid AuthetinticationDto data, HttpServletResponse response) {
        authenticationManager = context.getBean(AuthenticationManager.class);

        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var user = (User) auth.getPrincipal();
        var token = tokenService.generateToken(user);

        Cookie cookie = new Cookie("access_token", token);
        cookie.setMaxAge(2 *60 *60);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/"); 

        response.addCookie(cookie);

        return ResponseEntity.ok(new LoginResponseDto(token, user.getEmail(), user.getUserRole()));
    }

    public ResponseEntity<Object> register(@RequestBody RegisterDto registerDto) {
        if (this.userRepository.findByEmail(registerDto.email()) != null)
            return ResponseEntity.badRequest().build();
        String encryptedPassword = new BCryptPasswordEncoder().encode(registerDto.password());

        User newUser = new User(registerDto.email(), encryptedPassword, registerDto.role());
        newUser.setCreatedAt(new Date(System.currentTimeMillis()));
        this.userRepository.save(newUser);
        return ResponseEntity.ok().build();
    }
}