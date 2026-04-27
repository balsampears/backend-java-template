package com.balsam.system.common.service.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 批量下载
 * @author unknown
 * @version 1.0
 * @since 2018/10/9 11:05
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BatchDocumentDTO implements Serializable {
    /**
     * 附件信息
     */
   private List<DocumentDTO> documentDTOs;
    /**
     * 压缩文件名称
     */
    private String zipName;

}
