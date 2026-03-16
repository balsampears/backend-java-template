package com.balsam.system.common.core.page;

import com.balsam.system.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * <p>
 * 分页参数
 * </p>
 *
 * @author ouchangpeng
 * @since 2024-06-25
 */
@Data
public class PageRequest extends BaseEntity {

    public int pageNum = 1;
    public int pageSize = 10;
    public String keyword;
}
