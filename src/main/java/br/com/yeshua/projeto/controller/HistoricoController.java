package br.com.yeshua.projeto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.yeshua.projeto.model.Historico;
import br.com.yeshua.projeto.model.Representante;
import br.com.yeshua.projeto.repositoriy.HistoricoRepository;
import br.com.yeshua.projeto.repositoriy.RepresentanteRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("historico")
@Slf4j
public class HistoricoController {

    @Autowired
    HistoricoRepository historicoRepository;

    @Autowired
    PagedResourcesAssembler<Historico> pageAssembler;

    @GetMapping
    public PagedModel<EntityModel<Historico>> index(@PageableDefault(sort = "data", direction = Direction.DESC) Pageable pageable) {
        Page<Historico> page = null;

        page = historicoRepository.findAll(pageable);

        return pageAssembler.toModel(page, Historico::toEntityModel);
    }

    @GetMapping("{id}")
    public ResponseEntity<Historico> historicoById(@PathVariable Long id, Pageable pageable) {
        Historico historico = historicoRepository.findById(id).get();

        return ResponseEntity.ok(historico);
    }
}
