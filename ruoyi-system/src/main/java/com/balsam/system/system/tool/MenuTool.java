package com.balsam.system.system.tool;

import com.alibaba.fastjson2.JSON;
import com.balsam.system.common.core.domain.entity.SysMenu;
import com.balsam.system.system.service.ISysMenuService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
public class MenuTool {
    @Autowired
    ISysMenuService sysMenuService;

    @Tool(description = "查询菜单列表")
    public String queryMenuList(
        @ToolParam(description = "用户id", required = true) Long userId
    ){
        List<SysMenu> sysMenus = sysMenuService.selectMenuList(userId);
        return JSON.toJSONString(sysMenus);
    }
}
