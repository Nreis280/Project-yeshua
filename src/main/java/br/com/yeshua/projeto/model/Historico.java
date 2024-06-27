package br.com.yeshua.projeto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Date;

import org.springframework.hateoas.EntityModel;

import br.com.yeshua.projeto.controller.HistoricoController;
import br.com.yeshua.projeto.controller.RepresentanteController;

@Data
@Builder
@Entity
@Table(name = "historico")
@NoArgsConstructor
@AllArgsConstructor
public class Historico {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private User user;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date data;

    @Column(nullable = false)
    private String acao;

    @Column(nullable = false)
    private String entidade;

    @Column(nullable = false)
    private Long entidadeId;

    
    @Column(nullable = false)
    private String alteradoEm;

    @Column(nullable = false)
    private String campoAlterado;

    private String valorAntigo;

    private String valorNovo;

    public EntityModel<Historico> toEntityModel() {
        EntityModel<Historico> model = EntityModel.of(
            this,
            linkTo(methodOn(HistoricoController.class).index(null)).withRel("contents")
        );
        return model;
    }

}
