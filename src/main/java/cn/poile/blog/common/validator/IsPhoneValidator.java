package cn.poile.blog.common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * @author: yaohw
 */
public class IsPhoneValidator implements ConstraintValidator<IsPhone, Object> {

    private boolean required;

    @Override
    public void initialize(IsPhone ca) {
        required = ca.required();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        String mobile ="" + o;
        if (!"".equals(mobile) && required) {
            String regexp = "^[1][3,4,5,6,7,8,9]\\d{9}$";
            return Pattern.matches(regexp, mobile);
        }
        return true;
    }
}
