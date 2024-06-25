package br.com.yeshua.projeto.controller;

import static org.springframework.http.HttpStatus.CREATED;

import java.math.BigDecimal;
import java.util.Date;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.yeshua.projeto.model.Empresa;
import br.com.yeshua.projeto.model.Historico;
import br.com.yeshua.projeto.model.Representante;
import br.com.yeshua.projeto.model.User;
import br.com.yeshua.projeto.repositoriy.EmpresaRepository;
import br.com.yeshua.projeto.repositoriy.HistoricoRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("empresa")
@Slf4j
public class EmpresaController {

    record TotalPorRepresentante(String representante, BigDecimal cdEmpresa) {
    }

    @Autowired
    EmpresaRepository repository;

    @Autowired
    HistoricoRepository historicoRepository;

    @Autowired
    PagedResourcesAssembler<Empresa> pageAssembler;

    @GetMapping
    public PagedModel<EntityModel<Empresa>> index(
            @RequestParam(required = false) String representante,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String declaracoes,
            @RequestParam(required = false) String simplesNacional,
            @RequestParam(required = false) String apelido,
            @RequestParam(required = false) Long cdEmpresa,
            @RequestParam(required = false) String razaoSocial,
            @ParameterObject @PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable) {

        Page<Empresa> page = null;

        if (status != null) {
            page = repository.findByStatus(status, pageable);
        }

        if (declaracoes != null) {
            page = repository.findByDeclaracoes(declaracoes, pageable);
        }
        if (simplesNacional != null) {
            page = repository.findBySimplesNacional(simplesNacional, pageable);
        }
        if (apelido != null) {
            page = repository.findByApelidoContainingIgnoreCase(apelido, pageable);
        }

        if (cdEmpresa != null) {
            page = repository.findByCdEmpresaContaining(cdEmpresa.toString(), pageable);
        }

        if (razaoSocial != null) {
            page = repository.findByRazaoSocialContainingIgnoreCase(razaoSocial, pageable);
        }

        if (representante != null) {
            page = repository.findByRepresentanteNomeIgnoreCase(representante, pageable);
        }

        if (page == null) {
            page = repository.findAll(pageable);
        }

        return pageAssembler.toModel(page, Empresa::toEntityModel);

    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<Empresa> create(@RequestBody @Valid Empresa empresa) {
        Empresa savedEmpresa = repository.save(empresa);
        registrarHistorico(savedEmpresa, "Criou", null,savedEmpresa.getApelido());
        return ResponseEntity.created(
                empresa.toEntityModel().getLink("self").get().toUri()).body(savedEmpresa);
    }

    @GetMapping("{id}")
    public EntityModel<Empresa> get(@PathVariable Long id) {
        var empresa = repository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("empresa não encontrada"));

        return empresa.toEntityModel();
    }

    @DeleteMapping("{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> destroy(@PathVariable Long id) {
        Empresa empresa = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada"));
        repository.deleteById(id);
        registrarHistorico(empresa, "Apagou", empresa, null);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @CacheEvict(allEntries = true)
    public Empresa update(@PathVariable Long id, @RequestBody Empresa empresaAtualizada) {
        Empresa empresaExistente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada"));

        registrarAlteracoes(empresaExistente, empresaAtualizada);

        empresaAtualizada.setId(id);
        Empresa empresaAtualizadaSalva = repository.save(empresaAtualizada);

        return empresaAtualizadaSalva;
    }

    private void registrarHistorico(Empresa empresa, String acao, Empresa valorAntigo, String string) {
        User usuario = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Historico historico = Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao(acao)
                .entidade("Empresa")
                .entidadeId(empresa.getId())
                .campoAlterado(acao)
                .valorAntigo(valorAntigo != null ? valorAntigo.toString() : null)
                .valorNovo(string != null ? string.toString() : null)
                .build();

        historicoRepository.save(historico);
    }

    private void registrarAlteracoes(Empresa existente, Empresa atualizada) {
        User usuario = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    
        if (!existente.getRazaoSocial().equals(atualizada.getRazaoSocial())) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("razaoSocial")
                .valorAntigo(existente.getRazaoSocial())
                .valorNovo(atualizada.getRazaoSocial())
                .build());
        }
    
        if (!existente.getCnpj().equals(atualizada.getCnpj())) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("cnpj")
                .valorAntigo(existente.getCnpj())
                .valorNovo(atualizada.getCnpj())
                .build());
        }
    
        if (existente.getCdEmpresa() != null ? !existente.getCdEmpresa().equals(atualizada.getCdEmpresa()) : atualizada.getCdEmpresa() != null) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("cdEmpresa")
                .valorAntigo(existente.getCdEmpresa() != null ? existente.getCdEmpresa().toString() : null)
                .valorNovo(atualizada.getCdEmpresa() != null ? atualizada.getCdEmpresa().toString() : null)
                .build());
        }
    
        if (!existente.getApelido().equals(atualizada.getApelido())) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("apelido")
                .valorAntigo(existente.getApelido())
                .valorNovo(atualizada.getApelido())
                .build());
        }
    
        if (!existente.getStatus().equals(atualizada.getStatus())) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("status")
                .valorAntigo(existente.getStatus())
                .valorNovo(atualizada.getStatus())
                .build());
        }
    
        if (!existente.getCidade().equals(atualizada.getCidade())) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("cidade")
                .valorAntigo(existente.getCidade())
                .valorNovo(atualizada.getCidade())
                .build());
        }
    
        if (!existente.getPorte().equals(atualizada.getPorte())) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("porte")
                .valorAntigo(existente.getPorte())
                .valorNovo(atualizada.getPorte())
                .build());
        }
    
        if (existente.getCdAcessoSN() != null ? !existente.getCdAcessoSN().equals(atualizada.getCdAcessoSN()) : atualizada.getCdAcessoSN() != null) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("cdAcessoSN")
                .valorAntigo(existente.getCdAcessoSN() != null ? existente.getCdAcessoSN() : null)
                .valorNovo(atualizada.getCdAcessoSN() != null ? atualizada.getCdAcessoSN() : null)
                .build());
        }
    
        if (existente.getSimplesNacional() != null ? !existente.getSimplesNacional().equals(atualizada.getSimplesNacional()) : atualizada.getSimplesNacional() != null) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("simplesNacional")
                .valorAntigo(existente.getSimplesNacional() != null ? existente.getSimplesNacional() : null)
                .valorNovo(atualizada.getSimplesNacional() != null ? atualizada.getSimplesNacional() : null)
                .build());
        }
    
        if (!existente.getDeclaracoes().equals(atualizada.getDeclaracoes())) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("declaracoes")
                .valorAntigo(existente.getDeclaracoes())
                .valorNovo(atualizada.getDeclaracoes())
                .build());
        }
    
        if (existente.getPostoFiscal() != null ? !existente.getPostoFiscal().equals(atualizada.getPostoFiscal()) : atualizada.getPostoFiscal() != null) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("postoFiscal")
                .valorAntigo(existente.getPostoFiscal() != null ? existente.getPostoFiscal() : null)
                .valorNovo(atualizada.getPostoFiscal() != null ? atualizada.getPostoFiscal() : null)
                .build());
        }
    
        if (existente.getSenhaPostoFiscal() != null ? !existente.getSenhaPostoFiscal().equals(atualizada.getSenhaPostoFiscal()) : atualizada.getSenhaPostoFiscal() != null) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("senhaPostoFiscal")
                .valorAntigo(existente.getSenhaPostoFiscal() != null ? existente.getSenhaPostoFiscal() : null)
                .valorNovo(atualizada.getSenhaPostoFiscal() != null ? atualizada.getSenhaPostoFiscal() : null)
                .build());
        }
    
        if (existente.getIe() != null ? !existente.getIe().equals(atualizada.getIe()) : atualizada.getIe() != null) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("ie")
                .valorAntigo(existente.getIe() != null ? existente.getIe() : null)
                .valorNovo(atualizada.getIe() != null ? atualizada.getIe() : null)
                .build());
        }
    
        if (!existente.getProcuracao().equals(atualizada.getProcuracao())) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("procuracao")
                .valorAntigo(existente.getProcuracao())
                .valorNovo(atualizada.getProcuracao())
                .build());
        }
    
        if (!existente.getContabilista().equals(atualizada.getContabilista())) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("contabilista")
                .valorAntigo(existente.getContabilista())
                .valorNovo(atualizada.getContabilista())
                .build());
        }
    
        if (existente.getCcm() != null ? !existente.getCcm().equals(atualizada.getCcm()) : atualizada.getCcm() != null) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("ccm")
                .valorAntigo(existente.getCcm() != null ? existente.getCcm() : null)
                .valorNovo(atualizada.getCcm() != null ? atualizada.getCcm() : null)
                .build());
        }
    
        if (existente.getSenhaGiss() != null ? !existente.getSenhaGiss().equals(atualizada.getSenhaGiss()) : atualizada.getSenhaGiss() != null) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("senhaGiss")
                .valorAntigo(existente.getSenhaGiss() != null ? existente.getSenhaGiss() : null)
                .valorNovo(atualizada.getSenhaGiss() != null ? atualizada.getSenhaGiss() : null)
                .build());
        }
    
        if (existente.getUserNFE() != null ? !existente.getUserNFE().equals(atualizada.getUserNFE()) : atualizada.getUserNFE() != null) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("userNFE")
                .valorAntigo(existente.getUserNFE() != null ? existente.getUserNFE() : null)
                .valorNovo(atualizada.getUserNFE() != null ? atualizada.getUserNFE() : null)
                .build());
        }
    
        if (existente.getSenhaNFE() != null ? !existente.getSenhaNFE().equals(atualizada.getSenhaNFE()) : atualizada.getSenhaNFE() != null) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("senhaNFE")
                .valorAntigo(existente.getSenhaNFE() != null ? existente.getSenhaNFE() : null)
                .valorNovo(atualizada.getSenhaNFE() != null ? atualizada.getSenhaNFE() : null)
                .build());
        }
    
        if (!existente.getLicenciamento().equals(atualizada.getLicenciamento())) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("licenciamento")
                .valorAntigo(existente.getLicenciamento())
                .valorNovo(atualizada.getLicenciamento())
                .build());
        }
    
        if (existente.getGovBr() != null ? !existente.getGovBr().equals(atualizada.getGovBr()) : atualizada.getGovBr() != null) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("govBr")
                .valorAntigo(existente.getGovBr() != null ? existente.getGovBr() : null)
                .valorNovo(atualizada.getGovBr() != null ? atualizada.getGovBr() : null)
                .build());
        }
    
        if (!existente.getRepresentante().getId().equals(atualizada.getRepresentante().getId())) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Empresa")
                .entidadeId(existente.getId())
                .campoAlterado("representante")
                .valorAntigo(existente.getRepresentante().getNome())
                .valorNovo(atualizada.getRepresentante().getNome())
                .build());
        }
    }
    

}