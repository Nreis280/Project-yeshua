package br.com.yeshua.projeto.repositoriy;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.yeshua.projeto.model.User;



public interface UserRepository extends JpaRepository<User, Long>{
    UserDetails findByEmail(String email);
}
