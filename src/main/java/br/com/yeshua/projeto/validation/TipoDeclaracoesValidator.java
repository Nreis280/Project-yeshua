package br.com.yeshua.projeto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TipoDeclaracoesValidator implements ConstraintValidator<TipoDeclaracoes, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.equals("DEFIS") || value.equals("DCTF") || value.equals("DASN");
    }

}
