package br.com.yeshua.projeto.repositoriy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.yeshua.projeto.model.Representante;


public interface RepresentanteRepository extends JpaRepository<Representante, Long> {

    @Query("SELECT r FROM representantes r WHERE(r.cpf) = :cpf")
    Page<Representante> findByCpf(@Param("cpf") String cpf, Pageable pageable);

}
