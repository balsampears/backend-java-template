package com.balsam.system.web.controller.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.balsam.system.common.core.controller.BaseController;
import com.balsam.system.common.core.domain.AjaxResult;
import com.balsam.system.common.core.domain.model.RegisterBody;
import com.balsam.system.common.utils.StringUtils;
import com.balsam.system.framework.web.service.SysRegisterService;
import com.balsam.system.system.service.ISysConfigService;

import javax.annotation.Resource;

/**
 * 注册验证
 * 
 * @author ruoyi
 */
@RestController
public class SysRegisterController extends BaseController
{
    @Resource
    private SysRegisterService registerService;

    @Resource
    private ISysConfigService configService;

    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterBody user)
    {
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser"))))
        {
            return error("当前系统没有开启注册功能！");
        }
        String msg = registerService.register(user);
        return StringUtils.isEmpty(msg) ? success() : error(msg);
    }
}
