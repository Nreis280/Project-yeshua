package br.com.yeshua.projeto.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.math.BigDecimal;
import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
import br.com.yeshua.projeto.repositoriy.RepresentanteRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("representante")
@Slf4j
@CacheConfig(cacheNames = "representantes")
@Tag(name = "representantes", description = "Endpoint relacionados com representantes de empresas")
public class RepresentanteController {

    @Autowired
    RepresentanteRepository representanteRepository;

    @Autowired
    PagedResourcesAssembler<Representante> pageAssembler;


    @GetMapping("cpf")
    public PagedModel<EntityModel<Representante>> index(
        @RequestParam(required = false) String cpf,
        @ParameterObject @PageableDefault(sort = "id", direction = Direction.DESC) Pageable pageable
    ) {
        Page<Representante> page = null;

        if (cpf != null) {
            page = representanteRepository.findByCpf(cpf, pageable);
        }
        return pageAssembler.toModel(page, Representante::toEntityModel);
    }

    @GetMapping
    @Cacheable
    @Operation(summary = "Lista todas as representantes cadastradas no sistema.", description = "Endpoint que retorna um array de objetos do tipo representante com todas as representantes do usuário atual")
    public List<Representante> index() {
        return representanteRepository.findAll();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    @CacheEvict(allEntries = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Erro de validação da representante"),
            @ApiResponse(responseCode = "201", description = "representante cadastrada com sucesso")
    })
    public Representante create(@RequestBody @Valid Representante representante) {
        log.info("cadastrando representante: {}", representante);
        return representanteRepository.save(representante);
    }
    

    @GetMapping("{id}")
    public ResponseEntity<Representante> get(@PathVariable Long id) {
        log.info("Buscar por id: {}", id);

        return representanteRepository
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> destroy(@PathVariable Long id){
        representanteRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("MEI não encontrado")
        );

        verificarSeExisteRepresentante(id);

        representanteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @CacheEvict(allEntries = true)
    public Representante update(@PathVariable Long id, @RequestBody Representante representante) {
        log.info("atualizando representante id {} para {}", id, representante);

        verificarSeExisteRepresentante(id);

        representante.setId(id);
        return representanteRepository.save(representante);

    }

    private void verificarSeExisteRepresentante(Long id) {
        representanteRepository
                .findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Representante não encontrado"));
    }

}


