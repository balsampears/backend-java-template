package com.balsam.system.common.service.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author unknown
 * @version 1.0
 * @since 2018/10/9 11:05
 */
@ApiModel("文档")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVO {

    @ApiModelProperty(name = "key", required = true)
    private String key;

    @ApiModelProperty(name = "name", required = true)
    private String name;

    // @ApiModelProperty(name = "attachmentId", dataType = "Long", value = "业务id", notes = "业务id")
    // private Long attachmentId;

    // @ApiModelProperty(name = "typeId", dataType = "Integer", value = "文档类型", notes = "文档类型")
    // private Integer typeId;

}
