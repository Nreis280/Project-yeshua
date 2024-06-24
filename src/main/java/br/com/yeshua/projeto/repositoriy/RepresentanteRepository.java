package br.com.yeshua.projeto.repositoriy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import br.com.yeshua.projeto.model.Representante;


public interface RepresentanteRepository extends JpaRepository<Representante, Long> {

    Page<Representante> findByCpf (String cpf, Pageable pageable);

}
