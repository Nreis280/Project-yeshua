package br.com.yeshua.projeto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TipoLicenciamentoValidator implements ConstraintValidator<TipoLicenciamento, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.equals("SIM") || value.equals("NÃO");
    }

}
