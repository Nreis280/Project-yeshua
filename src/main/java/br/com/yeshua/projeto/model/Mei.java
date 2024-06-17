package br.com.yeshua.projeto.model;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.hateoas.EntityModel;

import br.com.yeshua.projeto.controller.MeiController;
import br.com.yeshua.projeto.validation.TipoContabilista;
import br.com.yeshua.projeto.validation.TipoDeclaracoes;
import br.com.yeshua.projeto.validation.TipoLicenciamento;
import br.com.yeshua.projeto.validation.TipoProcuracao;
import br.com.yeshua.projeto.validation.TipoStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@Builder
@Table(name = "mei",uniqueConstraints = {@UniqueConstraint(columnNames = {"razaoSocial","cnpj", "cdEmpresa","apelido"})})
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Mei {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String razaoSocial;

    @NotBlank
    private String cnpj;

    private BigDecimal cdEmpresa;

    @NotBlank
    @Size(max = 100)
    private String apelido;

    @NotBlank
    @TipoStatus(message = "{mei.tipo.tipostatus}")
    private String status;

    @NotBlank
    @Size(max = 100)
    private String cidade;

    private String porte = "MEI";

    @Size(max = 100)
    private String cdAcessoSN;

    @Size(max = 10)
    private String simplesNacional;

    @NotBlank
    @TipoDeclaracoes(message = "{mei.tipo.tipodeclaracoes}")
    private String declaracoes;

    private String ie;

    @Size(max = 100)
    private String postoFiscal;

    @Size(max = 100)
    private String senhaPostoFiscal;

    @Size(max = 20)
    private String ccm;

    @Size(max = 100)
    private String senhaGiss;

    @Size(max = 100)
    private String userNFE;

    @Size(max = 100)
    private String senhaNFE;

    @NotBlank
    @TipoProcuracao(message = "{mei.procuracao.tipoprocuracao}")
    private String procuracao;

    @NotBlank
    @TipoContabilista(message = "{mei.contabilista.tipocontabilista}")
    private String contabilista;

    @NotBlank
    @TipoLicenciamento(message = "{mei.licenciamento.tipolicenciamento}")
    private String licenciamento;

    private String govBr;

    @ManyToOne
    private Representante representante;

    public EntityModel<Mei> toEntityModel() {
        EntityModel<Mei> model = EntityModel.of(
            this,
            linkTo(methodOn(MeiController.class).get(id)).withSelfRel(),
            linkTo(methodOn(MeiController.class).destroy(id)).withRel("delete"),
            linkTo(methodOn(MeiController.class).index("", "", "", "", "", cdEmpresa, "", null)).withRel("contents")
        );
        return model;
    }
}
