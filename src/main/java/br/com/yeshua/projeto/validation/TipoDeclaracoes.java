package br.com.yeshua.projeto.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(FIELD)
@Constraint(validatedBy = TipoDeclaracoesValidator.class)
@Retention(RUNTIME)
public @interface TipoDeclaracoes {

    String message() default "Tipo inválido. Tipo de Declaracoes deve ser DEFIS, DCTF ou DASN.";

    Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };

}
