package com.balsam.system.common.service.domain;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;

@Data
public class DocumentDTO {

    @NotEmpty(message = "文档上传的url不能为空")
    private String url;

    @NotEmpty(message = "文档名称不能为空")
    private String name;

}
