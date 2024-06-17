package br.com.yeshua.projeto.repositoriy;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.yeshua.projeto.model.Empresa;
import br.com.yeshua.projeto.model.Mei;

public interface MeiRepository extends JpaRepository<Mei, Long> {
    Page<Mei> findByRepresentanteNomeIgnoreCase(String representante, Pageable pageable);

    //JPQL - Java Persistence Query Language
    @Query("SELECT e FROM Mei e WHERE(e.status) = :status")
    Page<Mei> findByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT e FROM Mei e WHERE(e.declaracoes) = :declaracoes")
    Page<Mei> findByDeclaracoes(@Param("declaracoes") String declaracoes, Pageable pageable);

    @Query("SELECT e FROM Mei e WHERE(e.simplesNacional) = :simplesNacional")
    Page<Mei> findBySimplesNacional(@Param("simplesNacional") String simplesNacional, Pageable pageable);

    @Query("SELECT e FROM Mei e WHERE(e.apelido) = :apelido")
    Page<Mei> findByApelido(@Param("apelido") String apelido, Pageable pageable);

    @Query("SELECT e FROM Mei e WHERE(e.cdEmpresa) = :cdEmpresa")
    Page<Mei> findByCdEmpresa(@Param("cdEmpresa") BigDecimal cdEmpresa, Pageable pageable);

    @Query("SELECT e FROM Mei e WHERE(e.razaoSocial) = :razaoSocial")
    Page<Mei> findByRazaoSocial(@Param("razaoSocial") String razaoSocial, Pageable pageable);
}
