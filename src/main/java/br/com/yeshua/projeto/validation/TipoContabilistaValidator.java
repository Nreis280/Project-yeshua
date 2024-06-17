package br.com.yeshua.projeto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TipoContabilistaValidator implements ConstraintValidator<TipoContabilista, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.equals("OK") || value.equals("NÃO");
    }

}
