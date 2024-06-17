package br.com.yeshua.projeto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TipoProcuracaoValidator implements ConstraintValidator<TipoProcuracao, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.equals("Não tem") || value.equals("Anderson") || value.equals("Yeshua Empresarial") || value.equals("Yeshua Contabilidade") || value.equals("Anderson CPF Socios");
    }

}
