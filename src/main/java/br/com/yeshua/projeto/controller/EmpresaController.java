package br.com.yeshua.projeto.controller;


import static org.springframework.http.HttpStatus.CREATED;

import java.math.BigDecimal;

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
import br.com.yeshua.projeto.model.Representante;
import br.com.yeshua.projeto.repositoriy.EmpresaRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("empresa")
@Slf4j
public class EmpresaController {

    record TotalPorRepresentante (String representante, BigDecimal valor){} 

    @Autowired
    EmpresaRepository repository;

    @Autowired
    PagedResourcesAssembler<Empresa> pageAssembler;

    @GetMapping
    public PagedModel<EntityModel<Empresa>> index(
        @RequestParam(required = false) String representante,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String declaracoes,
        @RequestParam(required = false) String simplesNacional,
        @RequestParam(required = false) String apelido,
        @RequestParam(required = false) BigDecimal cdEmpresa,
        @RequestParam(required = false) String razaoSocial,
        @ParameterObject @PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable
    ) {

        Page<Empresa> page = null;

        if (status != null){
            page = repository.findByStatus(status, pageable);
        }

        if (declaracoes != null){
            page = repository.findByDeclaracoes(declaracoes, pageable);
        }
        if (simplesNacional != null){
            page = repository.findBySimplesNacional(simplesNacional, pageable);
        }
        if (apelido != null){
            page = repository.findByApelido(apelido, pageable);
        }

        if (cdEmpresa != null){
            page = repository.findByCdEmpresa(cdEmpresa, pageable);
        }

        if (razaoSocial != null){
            page = repository.findByRazaoSocial(razaoSocial, pageable);
        }

        if (representante != null){
            page = repository.findByRepresentanteNomeIgnoreCase(representante, pageable);
        } 

        if(page == null){
            page = repository.findAll(pageable);
        }

        return pageAssembler.toModel(page, Empresa::toEntityModel);

    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<Empresa> create(@RequestBody @Valid Empresa empresa){
        repository.save(empresa);

        return ResponseEntity.created(
                    empresa.toEntityModel().getLink("self").get().toUri()
                ).body(empresa);
    }

    @GetMapping("{id}")
    public EntityModel<Empresa> get(@PathVariable Long id){
        var empresa = repository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("empresa não encontrada")
        );

        return empresa.toEntityModel();
    }

    @DeleteMapping("{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> destroy(@PathVariable Long id){
        repository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("empresa não encontrada")
        );

        verificarSeExisteEmpresa(id);

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    
    @PutMapping("{id}")
    @CacheEvict(allEntries = true)
    public Empresa update(@PathVariable Long id, @RequestBody Empresa empresa) {
        log.info("atualizando empresa id {} para {}", id, empresa);

        verificarSeExisteEmpresa(id);

        empresa.setId(id);
        return repository.save(empresa);

    }

        private void verificarSeExisteEmpresa(Long id) {
        repository
                .findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa não encontrada"));
    }
    
}