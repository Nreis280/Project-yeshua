package br.com.yeshua.projeto.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(FIELD)
@Constraint(validatedBy = TipoStatusValidator.class)
@Retention(RUNTIME)
public @interface TipoStatus {

    String message() default "Tipo inválido. Tipo STATUS deve ser ATIVO, INATIVO ou SAIDA.";

    Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };

}
