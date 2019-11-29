package cn.poile.blog.common.validator.annotation;

import cn.poile.blog.common.validator.YearMonthFormatValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * yyyy-mm,年月格式校验
 * @author: yaohw
 * @create: 2019-11-28 09:23
 **/
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = YearMonthFormatValidator.class)
public @interface YearMonthFormat {
    boolean required() default true;
    String message() default "年月格式不正确";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
