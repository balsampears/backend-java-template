package com.balsam.system.common.service.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author unknown
 * @version 1.0
 * @since 2018/10/9 11:05
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVO {

    private String key;

    private String name;

    // 业务id
    // private Long attachmentId;

    // 文档类型
    // private Integer typeId;

}
