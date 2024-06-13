package br.com.yeshua.projeto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@SpringBootApplication
public class ProjetoApplication {

	@GetMapping
	public ResponseEntity<String> status(){
		return ResponseEntity.ok("Working!");
	}

	public static void main(String[] args) {
		SpringApplication.run(ProjetoApplication.class, args);
	}

}
