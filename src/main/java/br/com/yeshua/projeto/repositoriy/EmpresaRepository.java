package br.com.yeshua.projeto.repositoriy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.yeshua.projeto.model.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Page<Empresa> findByRepresentanteNomeIgnoreCase(String representante, Pageable pageable);

    //JPQL - Java Persistence Query Language
    @Query("SELECT m FROM Empresa m WHERE(m.status) = :status")
    Page<Empresa> findByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT m FROM Empresa m WHERE(m.declaracoes) = :declaracoes")
    Page<Empresa> findByDeclaracoes(@Param("declaracoes") String declaracoes, Pageable pageable);

    @Query("SELECT m FROM Empresa m WHERE(m.simplesNacional) = :simplesNacional")
    Page<Empresa> findBySimplesNacional(@Param("simplesNacional") String simplesNacional, Pageable pageable);

    @Query("SELECT m FROM Empresa m WHERE(m.apelido) = :apelido")
    Page<Empresa> findByApelido(@Param("apelido") String apelido, Pageable pageable);

    @Query("SELECT m FROM Empresa m WHERE(m.razaoSocial) = :razaoSocial")
    Page<Empresa> findByRazaoSocial(@Param("razaoSocial") String razaoSocial, Pageable pageable);

/*     Empresa findAllByOrderByNome();
 */
}