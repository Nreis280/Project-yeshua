package br.com.yeshua.projeto.config;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.yeshua.projeto.enums.UserRole;
import br.com.yeshua.projeto.model.User;
import br.com.yeshua.projeto.repositoriy.UserRepository;
import jakarta.annotation.PostConstruct;

@Configuration
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        // Verifica se o usuário ADMIN já existe
        if (userRepository.findByEmail("admin@example.com") == null) {
            // Cria um novo usuário ADMIN
            User admin = new User();
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setUserRole(UserRole.ADMIN);
            admin.setCreatedAt(new Date());
            userRepository.save(admin);
        }
    }
}
