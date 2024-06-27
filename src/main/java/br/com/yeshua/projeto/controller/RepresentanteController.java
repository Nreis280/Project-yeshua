package br.com.yeshua.projeto.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.math.BigDecimal;
import java.util.Date;
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
import br.com.yeshua.projeto.model.Representante;
import br.com.yeshua.projeto.model.User;
import br.com.yeshua.projeto.repositoriy.HistoricoRepository;
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
    HistoricoRepository historicoRepository;


    @Autowired
    PagedResourcesAssembler<Representante> pageAssembler;


    @GetMapping("cpf/{cpf}")
    public ResponseEntity<Representante> index(@PathVariable String cpf) {
        Representante representante = representanteRepository.findByCpf(cpf);

        return representante != null? ResponseEntity.ok(representante) : ResponseEntity.notFound().build();
    }

    @GetMapping
    @Cacheable
    @Operation(summary = "Lista todas as representantes cadastradas no sistema.", description = "Endpoint que retorna um array de objetos do tipo representante com todas as representantes do usuário atual")
    public PagedModel<EntityModel<Representante>> index(Pageable pageable) {
        Page<Representante> page = null;

        page = representanteRepository.findAll(pageable);

        return pageAssembler.toModel(page, Representante::toEntityModel);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<Representante> create(@RequestBody @Valid Representante representante) {
        Representante savedRepresentante = representanteRepository.save(representante);
        registrarHistorico(savedRepresentante, "Criou", null, savedRepresentante.getNome());
        return ResponseEntity.created(
                representante.toEntityModel().getLink("self").get().toUri()).body(savedRepresentante);
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
    public ResponseEntity<Object> destroy(@PathVariable Long id) {
        Representante representante = representanteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("representante não encontrado"));
        representanteRepository.deleteById(id);
        registrarHistorico(representante, "Apagou", representante, null);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    @CacheEvict(allEntries = true)
    public Representante update(@PathVariable Long id, @RequestBody Representante representanteAtualizada) {
        Representante representanteExistente = representanteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mei não encontrado"));

        registrarAlteracoes(representanteExistente, representanteAtualizada);

        representanteAtualizada.setId(id);
        Representante representanteAtualizadaSalva = representanteRepository.save(representanteAtualizada);

        return representanteAtualizadaSalva;
    }

    private void registrarHistorico(Representante representante, String acao, Representante valorAntigo, String string) {
        User usuario = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Historico historico = Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao(acao)
                .entidade("Representante")
                .entidadeId(representante.getId())
                .campoAlterado(acao)
                .alteradoEm(representante.getNome())
                .valorAntigo(valorAntigo != null ? valorAntigo.toString() : null)
                .valorNovo(string != null ? string.toString() : null)
                .build();

        historicoRepository.save(historico);
    }

    private void registrarAlteracoes(Representante existente, Representante atualizada) {
        User usuario = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    
        if (!existente.getNome().equals(atualizada.getNome())) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Representante")
                .entidadeId(existente.getId())
                .campoAlterado("nome")
                .alteradoEm(atualizada.getNome())
                .valorAntigo(existente.getNome())
                .valorNovo(atualizada.getNome())
                .build());
        }

        if (!existente.getCpf().equals(atualizada.getCpf())) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Representante")
                .entidadeId(existente.getId())
                .campoAlterado("cpf")
                .alteradoEm(existente.getNome())
                .valorAntigo(existente.getCpf())
                .valorNovo(atualizada.getCpf())
                .build());
        }

        if (!existente.getEmail().equals(atualizada.getEmail())) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Representante")
                .entidadeId(existente.getId())
                .campoAlterado("email")
                .alteradoEm(existente.getNome())
                .valorAntigo(existente.getEmail())
                .valorNovo(atualizada.getEmail())
                .build());
        }

        if (!existente.getTelefone().equals(atualizada.getTelefone())) {
            historicoRepository.save(Historico.builder()
                .user(usuario)
                .data(new Date())
                .acao("Atualizou")
                .entidade("Representante")
                .entidadeId(existente.getId())
                .campoAlterado("telefone")
                .alteradoEm(existente.getNome())
                .valorAntigo(existente.getTelefone())
                .valorNovo(atualizada.getTelefone())
                .build());
        }

    }
}


