package br.com.yeshua.projeto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TipoSimplesNacionalValidator implements ConstraintValidator<TipoSimplesNacional, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.equals("OPTANTE") || value.equals("NÃO OPTANTE");
    }

}
