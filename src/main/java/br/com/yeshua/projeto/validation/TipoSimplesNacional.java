package br.com.yeshua.projeto.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(FIELD)
@Constraint(validatedBy = TipoSimplesNacionalValidator.class)
@Retention(RUNTIME)
public @interface TipoSimplesNacional {

    String message() default "Tipo inválido. Tipo do Porte deve ser ME, EPP ou DEMAIS.";

    Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };

}
