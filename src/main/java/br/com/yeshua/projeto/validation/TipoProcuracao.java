package br.com.yeshua.projeto.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(FIELD)
@Constraint(validatedBy = TipoProcuracaoValidator.class)
@Retention(RUNTIME)
public @interface TipoProcuracao {

    String message() default "Tipo inválido. Tipo de procuracao deve ser Não tem, Anderson, Anderson CPF Socios, Yeshua Contabilidade ou Yeshua Empresarial.";

    Class<?>[] groups() default { };

	Class<? extends Payload>[] payload() default { };

}
