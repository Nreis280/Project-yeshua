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
import br.com.yeshua.projeto.model.Mei;
import br.com.yeshua.projeto.model.User;
import br.com.yeshua.projeto.repositoriy.HistoricoRepository;
import br.com.yeshua.projeto.repositoriy.MeiRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("mei")
@Slf4j
public class MeiController {

    @Autowired
    MeiRepository repository;

    @Autowired
    HistoricoRepository historicoRepository;

    @Autowired
    PagedResourcesAssembler<Mei> pageAssembler;

    @GetMapping
    public PagedModel<EntityModel<Mei>> index(
            @RequestParam(required = false) String representante,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String declaracoes,
            @RequestParam(required = false) String simplesNacional,
            @RequestParam(required = false) String apelido,
            @RequestParam(required = false) Long cdEmpresa,
            @RequestParam(required = false) String razaoSocial,
            @ParameterObject @PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable) {

        Page<Mei> page = null;

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

        return pageAssembler.toModel(page, Mei::toEntityModel);

    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<Mei> create(@RequestBody @Valid Mei mei) {
        Mei savedMei = repository.save(mei);
        registrarHistorico(savedMei, "Criou", null, savedMei.getApelido());
        return ResponseEntity.created(
                mei.toEntityModel().getLink("self").get().toUri()).body(savedMei);
    }

    @GetMapping("{id}")
    public EntityModel<Mei> get(@PathVariable Long id) {
        var mei = repository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Mei não encontrado"));

        return mei.toEntityModel();
    }

    @DeleteMapping("{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> destroy(@PathVariable Long id) {
        Mei mei = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mei não encontrado"));
        repository.deleteById(id);
        registrarHistorico(mei, "Apagou", mei, null);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @CacheEvict(allEntries = true)
    public Mei update(@PathVariable Long id, @RequestBody Mei meiAtualizada) {
        Mei meiExistente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mei não encontrado"));

        registrarAlteracoes(meiExistente, meiAtualizada);

        meiAtualizada.setId(id);
        Mei meiAtualizadaSalva = repository.save(meiAtualizada);

        return meiAtualizadaSalva;
    }

    private void registrarHistorico(Mei mei, String acao, Mei valorAntigo, String string) {
        User usuario = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Historico historico = Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao(acao)
                .entidade("Mei")
                .entidadeId(mei.getId())
                .alteradoEm(mei.getApelido())
                .campoAlterado(acao)
                .valorAntigo(valorAntigo != null ? valorAntigo.toString() : null)
                .valorNovo(string != null ? string.toString() : null)
                .build();

        historicoRepository.save(historico);
    }

    private void registrarAlteracoes(Mei existente, Mei atualizada) {
        User usuario = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!existente.getRazaoSocial().equals(atualizada.getRazaoSocial())) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("razaoSocial")
                    .alteradoEm(existente.getApelido())
                    .valorAntigo(existente.getRazaoSocial())
                    .valorNovo(atualizada.getRazaoSocial())
                    .build());
        }

        if (!existente.getCnpj().equals(atualizada.getCnpj())) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("cnpj")
                    .alteradoEm(existente.getApelido())
                    .valorAntigo(existente.getCnpj())
                    .valorNovo(atualizada.getCnpj())
                    .build());
        }

        if (existente.getCdEmpresa() != null ? !existente.getCdEmpresa().equals(atualizada.getCdEmpresa())
                : atualizada.getCdEmpresa() != null) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("cdEmpresa")
                    .alteradoEm(existente.getApelido())
                    .valorAntigo(existente.getCdEmpresa() != null ? existente.getCdEmpresa().toString() : null)
                    .valorNovo(atualizada.getCdEmpresa() != null ? atualizada.getCdEmpresa().toString() : null)
                    .build());
        }

        if (!existente.getApelido().equals(atualizada.getApelido())) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("apelido")
                    .alteradoEm(atualizada.getApelido())
                    .valorAntigo(existente.getApelido())
                    .valorNovo(atualizada.getApelido())
                    .build());
        }

        if (!existente.getStatus().equals(atualizada.getStatus())) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("status")
                    .alteradoEm(existente.getApelido())
                    .valorAntigo(existente.getStatus())
                    .valorNovo(atualizada.getStatus())
                    .build());
        }

        if (!existente.getCidade().equals(atualizada.getCidade())) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("cidade")
                    .alteradoEm(existente.getApelido())
                    .valorAntigo(existente.getCidade())
                    .valorNovo(atualizada.getCidade())
                    .build());
        }

        if (existente.getCdAcessoSN() != null ? !existente.getCdAcessoSN().equals(atualizada.getCdAcessoSN())
                : atualizada.getCdAcessoSN() != null) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("cdAcessoSN")
                    .alteradoEm(existente.getApelido())
                    .valorAntigo(existente.getCdAcessoSN() != null ? existente.getCdAcessoSN() : null)
                    .valorNovo(atualizada.getCdAcessoSN() != null ? atualizada.getCdAcessoSN() : null)
                    .build());
        }

        if (existente.getSimplesNacional() != null
                ? !existente.getSimplesNacional().equals(atualizada.getSimplesNacional())
                : atualizada.getSimplesNacional() != null) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("simplesNacional")
                    .alteradoEm(existente.getApelido())
                    .valorAntigo(existente.getSimplesNacional() != null ? existente.getSimplesNacional() : null)
                    .valorNovo(atualizada.getSimplesNacional() != null ? atualizada.getSimplesNacional() : null)
                    .build());
        }

        if (!existente.getDeclaracoes().equals(atualizada.getDeclaracoes())) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("declaracoes")
                    .alteradoEm(existente.getApelido())
                    .valorAntigo(existente.getDeclaracoes())
                    .valorNovo(atualizada.getDeclaracoes())
                    .build());
        }

        if (existente.getPostoFiscal() != null ? !existente.getPostoFiscal().equals(atualizada.getPostoFiscal())
                : atualizada.getPostoFiscal() != null) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("postoFiscal")
                    .alteradoEm(existente.getApelido())
                    .valorAntigo(existente.getPostoFiscal() != null ? existente.getPostoFiscal() : null)
                    .valorNovo(atualizada.getPostoFiscal() != null ? atualizada.getPostoFiscal() : null)
                    .build());
        }

        if (existente.getSenhaPostoFiscal() != null
                ? !existente.getSenhaPostoFiscal().equals(atualizada.getSenhaPostoFiscal())
                : atualizada.getSenhaPostoFiscal() != null) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("senhaPostoFiscal")
                    .alteradoEm(existente.getApelido())
                    .valorAntigo(existente.getSenhaPostoFiscal() != null ? existente.getSenhaPostoFiscal() : null)
                    .valorNovo(atualizada.getSenhaPostoFiscal() != null ? atualizada.getSenhaPostoFiscal() : null)
                    .build());
        }

        if (existente.getIe() != null ? !existente.getIe().equals(atualizada.getIe()) : atualizada.getIe() != null) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("ie")
                    .alteradoEm(existente.getApelido())
                    .valorAntigo(existente.getIe() != null ? existente.getIe() : null)
                    .valorNovo(atualizada.getIe() != null ? atualizada.getIe() : null)
                    .build());
        }

        if (!existente.getProcuracao().equals(atualizada.getProcuracao())) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("procuracao")
                    .alteradoEm(existente.getApelido())
                    .valorAntigo(existente.getProcuracao())
                    .valorNovo(atualizada.getProcuracao())
                    .build());
        }

        if (!existente.getContabilista().equals(atualizada.getContabilista())) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("contabilista")
                    .alteradoEm(existente.getApelido())
                    .valorAntigo(existente.getContabilista())
                    .valorNovo(atualizada.getContabilista())
                    .build());
        }

        if (existente.getCcm() != null ? !existente.getCcm().equals(atualizada.getCcm())
                : atualizada.getCcm() != null) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("ccm")
                    .alteradoEm(existente.getApelido())
                    .valorAntigo(existente.getCcm() != null ? existente.getCcm() : null)
                    .valorNovo(atualizada.getCcm() != null ? atualizada.getCcm() : null)
                    .build());
        }

        if (existente.getSenhaGiss() != null ? !existente.getSenhaGiss().equals(atualizada.getSenhaGiss())
                : atualizada.getSenhaGiss() != null) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("senhaGiss")
                    .alteradoEm(existente.getApelido())
                    .valorAntigo(existente.getSenhaGiss() != null ? existente.getSenhaGiss() : null)
                    .valorNovo(atualizada.getSenhaGiss() != null ? atualizada.getSenhaGiss() : null)
                    .build());
        }

        if (existente.getUserNFE() != null ? !existente.getUserNFE().equals(atualizada.getUserNFE())
                : atualizada.getUserNFE() != null) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("userNFE")
                    .alteradoEm(existente.getApelido())
                    .valorAntigo(existente.getUserNFE() != null ? existente.getUserNFE() : null)
                    .valorNovo(atualizada.getUserNFE() != null ? atualizada.getUserNFE() : null)
                    .build());
        }

        if (existente.getSenhaNFE() != null ? !existente.getSenhaNFE().equals(atualizada.getSenhaNFE())
                : atualizada.getSenhaNFE() != null) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("senhaNFE")
                    .alteradoEm(existente.getApelido())
                    .valorAntigo(existente.getSenhaNFE() != null ? existente.getSenhaNFE() : null)
                    .valorNovo(atualizada.getSenhaNFE() != null ? atualizada.getSenhaNFE() : null)
                    .build());
        }

        if (!existente.getLicenciamento().equals(atualizada.getLicenciamento())) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("licenciamento")
                    .alteradoEm(existente.getApelido())
                    .valorAntigo(existente.getLicenciamento())
                    .valorNovo(atualizada.getLicenciamento())
                    .build());
        }

        if (existente.getGovBr() != null ? !existente.getGovBr().equals(atualizada.getGovBr())
                : atualizada.getGovBr() != null) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("govBr")
                    .alteradoEm(existente.getApelido())
                    .valorAntigo(existente.getGovBr() != null ? existente.getGovBr() : null)
                    .valorNovo(atualizada.getGovBr() != null ? atualizada.getGovBr() : null)
                    .build());
        }

        if (!existente.getRepresentante().getId().equals(atualizada.getRepresentante().getId())) {
            historicoRepository.save(Historico.builder()
                    .user(usuario)
                    .data(new Date())
                    .acao("Atualizou")
                    .entidade("Mei")
                    .entidadeId(existente.getId())
                    .campoAlterado("representante")
                    .alteradoEm(existente.getApelido()) 
                    .valorAntigo(existente.getRepresentante().getNome())
                    .valorNovo(atualizada.getRepresentante().getNome())
                    .build());
        }
    }
}
