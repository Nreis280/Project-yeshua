package br.com.yeshua.projeto.repositoriy;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import br.com.yeshua.projeto.model.Mei;

public interface MeiRepository extends JpaRepository<Mei, Long> {
    Page<Mei> findByRepresentanteNomeIgnoreCase(String representante, Pageable pageable);

    //JPQL - Java Persistence Query Language
    @Query("SELECT m FROM Mei m WHERE(m.status) = :status")
    Page<Mei> findByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT m FROM Mei m WHERE(m.declaracoes) = :declaracoes")
    Page<Mei> findByDeclaracoes(@Param("declaracoes") String declaracoes, Pageable pageable);

    @Query("SELECT m FROM Mei m WHERE(m.simplesNacional) = :simplesNacional")
    Page<Mei> findBySimplesNacional(@Param("simplesNacional") String simplesNacional, Pageable pageable);

    Page<Mei> findByApelidoContainingIgnoreCase (String apelido, Pageable pageable);

    @Query("SELECT m FROM Mei m WHERE str(m.cdEmpresa) LIKE %:cdEmpresa%")
    Page<Mei> findByCdEmpresaContaining(@Param("cdEmpresa") String cdEmpresa, Pageable pageable);

    Page<Mei> findByRazaoSocialContainingIgnoreCase(String razaoSocial, Pageable pageable);
}
