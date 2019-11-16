package cn.poile.blog.common.validator.annotation;

/**
 * @author: yaohw
 * @create: 2019-11-15 15:23
 **/

import cn.poile.blog.common.validator.ListSizeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 是否类型文件类型为图片
 * @author: yaohw
 * @create: 2019-11-13 11:02
 **/
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ListSizeValidator.class)
public @interface ListSize {
    boolean required() default true;
    int min() default -1;
    int max() default -1;
    String message();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
