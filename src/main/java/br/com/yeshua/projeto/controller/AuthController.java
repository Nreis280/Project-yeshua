package br.com.yeshua.projeto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.yeshua.projeto.dtos.AuthetinticationDto;
import br.com.yeshua.projeto.dtos.RegisterDto;
import br.com.yeshua.projeto.services.AuthorizationService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    AuthorizationService authorizationService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid AuthetinticationDto authetinticationDto){
        return authorizationService.login(authetinticationDto);
    }


    @PostMapping("/register")
    public ResponseEntity<Object> register (@RequestBody RegisterDto registerDto){
        return authorizationService.register(registerDto);
    }
}
