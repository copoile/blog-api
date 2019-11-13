package cn.poile.blog.common.validator.annotation;

import cn.poile.blog.common.validator.IsImageValidator;

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
@Constraint(validatedBy = IsImageValidator.class)
public @interface IsImage {
    boolean required() default true;
    String message() default "文件格式不正确，只限bmp,gif,jpeg,jpeg,png,webp格式";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
