package br.com.yeshua.projeto.repositoriy;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.yeshua.projeto.model.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Page<Empresa> findByRepresentanteNomeIgnoreCase(String representante, Pageable pageable);

    @Query("SELECT e FROM Empresa e WHERE(e.status) = :status")
    Page<Empresa> findByStatus(@Param("status") String status, Pageable pageable);

    @Query("SELECT e FROM Empresa e WHERE(e.declaracoes) = :declaracoes")
    Page<Empresa> findByDeclaracoes(@Param("declaracoes") String declaracoes, Pageable pageable);

    @Query("SELECT e FROM Empresa e WHERE(e.simplesNacional) = :simplesNacional")
    Page<Empresa> findBySimplesNacional(@Param("simplesNacional") String simplesNacional, Pageable pageable);

    Page<Empresa> findByApelidoContainingIgnoreCase (String apelido, Pageable pageable);

    @Query("SELECT e FROM Empresa e WHERE str(e.cdEmpresa) LIKE %:cdEmpresa%")
    Page<Empresa> findByCdEmpresaContaining(@Param("cdEmpresa") String cdEmpresa, Pageable pageable);

    Page<Empresa> findByRazaoSocialContainingIgnoreCase(String razaoSocial, Pageable pageable);

}