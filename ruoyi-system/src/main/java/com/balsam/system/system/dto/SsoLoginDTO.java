package com.balsam.system.system.dto;


import com.balsam.system.common.core.domain.AjaxResult;
import lombok.Data;

@Data
public class SsoLoginDTO {
    private String type;

    private AjaxResult ajaxResult;
}
