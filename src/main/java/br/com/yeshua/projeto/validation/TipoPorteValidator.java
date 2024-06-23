package br.com.yeshua.projeto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TipoPorteValidator implements ConstraintValidator<TipoPorte, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.equals("ME") || value.equals("EPP") || value.equals("DEMAIS");
    }

}