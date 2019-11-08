package cn.poile.blog.controller.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * @author: yaohw
 * @create: 2019-11-08 16:04
 **/
@ApiModel(value = "用户更新json",description = "用户更新")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateUserRequest {

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "性别，1：男，0：女，默认为1")
    private Integer gender;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @ApiModelProperty(value = "生日")
    private LocalDate birthday;

    @ApiModelProperty(value = "简介|个性签名")
    private String brief;
}
