package com.balsam.system.common.service.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@ApiModel("文档")
@Data
public class DocumentDTO {

    @ApiModelProperty(name = "url", dataType = "String", value = "文档url地址", notes = "文档url地址", required = true)
    @NotEmpty(message = "文档上传的url不能为空")
    private String url;

    @ApiModelProperty(name = "name", dataType = "String", value = "文档名称", notes = "文档名称", required = true)
    @NotEmpty(message = "文档名称不能为空")
    private String name;

}
