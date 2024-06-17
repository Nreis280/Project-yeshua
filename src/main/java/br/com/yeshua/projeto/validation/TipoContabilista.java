package br.com.yeshua.projeto.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(FIELD)
@Constraint(validatedBy = TipoContabilistaValidator.class)
@Retention(RUNTIME)
public @interface TipoContabilista {

    String message() default "Tipo inválido. Tipo de Contabilista deve ser OK ou NÃO.";

    Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };

}
