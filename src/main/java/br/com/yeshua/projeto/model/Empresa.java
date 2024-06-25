package br.com.yeshua.projeto.model;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.math.BigDecimal;

import org.springframework.hateoas.EntityModel;

import br.com.yeshua.projeto.controller.EmpresaController;
import br.com.yeshua.projeto.validation.TipoContabilista;
import br.com.yeshua.projeto.validation.TipoDeclaracoes;
import br.com.yeshua.projeto.validation.TipoLicenciamento;
import br.com.yeshua.projeto.validation.TipoPorte;
import br.com.yeshua.projeto.validation.TipoProcuracao;
import br.com.yeshua.projeto.validation.TipoSimplesNacional;
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
@Table(name = "empresas",uniqueConstraints = {@UniqueConstraint(columnNames = {"razaoSocial","cnpj", "cdEmpresa","apelido"})})
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Empresa extends EntityModel<Empresa>{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String razaoSocial;

    // @CNPJ
    @NotBlank
    private String cnpj;

    private Long cdEmpresa;

    @NotBlank
    @Size(max = 100)
    private String apelido;

    @NotBlank
    @TipoStatus(message = "{empresa.tipo.tipostatus}")
    private String status;

    @NotBlank
    @Size(max = 100)
    private String cidade;

    @NotBlank
    @TipoPorte(message = "{empresa.tipo.tipoporte}")
    private String porte;

    @Size(max = 100)
    private String cdAcessoSN;

    @Size(max = 11)
    @TipoSimplesNacional(message = "{empresa.tipo.tiposimplesnacional}")
    private String simplesNacional;

    @NotBlank
    @TipoDeclaracoes(message = "{empresa.tipo.tipodeclaracoes}")
    private String declaracoes;

    @Size(max = 100)
    private String postoFiscal;

    @Size(max = 100)
    private String senhaPostoFiscal;

    private String ie;

    @NotBlank
    @TipoProcuracao(message = "{empresa.procuracao.tipoprocuracao}")
    private String procuracao;

    @NotBlank
    @TipoContabilista(message = "{empresa.contabilista.tipocontabilista}")
    private String contabilista;

    @Size(max = 20)
    private String ccm;

    @Size(max = 100)
    private String senhaGiss;

    @Size(max = 100)
    private String userNFE;

    @Size(max = 100)
    private String senhaNFE;

    @NotBlank
    @TipoLicenciamento(message = "{empresa.licenciamento.tipolicenciamento}")
    private String licenciamento;

    private String govBr;

    @ManyToOne
    private Representante representante;


    public EntityModel<Empresa> toEntityModel() {
        EntityModel<Empresa> model = EntityModel.of(
            this,
            linkTo(methodOn(EmpresaController.class).get(id)).withSelfRel(),
            linkTo(methodOn(EmpresaController.class).destroy(id)).withRel("delete"),
            linkTo(methodOn(EmpresaController.class).index("", "", "", "", "", cdEmpresa, "", null)).withRel("contents")
        );
        return model;
    }


}
