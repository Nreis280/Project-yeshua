package br.com.yeshua.projeto.model;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.hateoas.EntityModel;

import br.com.yeshua.projeto.controller.RepresentanteController;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "representantes")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Representante {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos")
    private String cpf;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "\\d{10,11}", message = "Telefone deve conter 10 ou 11 dígitos")
    private String telefone;


    public EntityModel<Representante> toEntityModel() {
    EntityModel<Representante> model = EntityModel.of(
        this,
        linkTo(methodOn(RepresentanteController.class).get(id)).withSelfRel(),
        linkTo(methodOn(RepresentanteController.class).destroy(id)).withRel("delete"),
        linkTo(methodOn(RepresentanteController.class).index("",null)).withRel("contents")
    );
    return model;
    }

}
