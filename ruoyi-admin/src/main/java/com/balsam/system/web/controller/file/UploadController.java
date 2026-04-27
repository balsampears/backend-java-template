package com.balsam.system.web.controller.file;


import com.balsam.system.common.annotation.Anonymous;
import com.balsam.system.common.core.controller.BaseController;
import com.balsam.system.common.core.domain.AjaxResult;
import com.balsam.system.common.service.domain.DocumentDTO;
import com.balsam.system.common.service.domain.DocumentVO;
import com.balsam.system.common.service.impl.OssServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件上传接口
 *
 * @author Hua
 * @date 2018/10/22  17:37
 */
@RestController
@RequestMapping("upload")
@Api(tags = "文件上传管理")
public class UploadController extends BaseController {

    @Resource
    private OssServiceImpl ossService;


    @Anonymous
    @ApiOperation(value = "上传文件-返回路径", response = AjaxResult.class)
    @PostMapping("file-upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile file) throws IOException {
        DocumentVO upload = ossService.upload(file);
        return AjaxResult.success(upload);
    }

    @Anonymous
    @ApiOperation(value = "上传文件-通过下载链接上传")
    @PostMapping("/uploadByUrl")
    public AjaxResult uploadByUrl(@RequestBody @Validated DocumentDTO file) {
        DocumentVO upload = ossService.uploadByUrl(file);
        return AjaxResult.success(upload);
    }

    @Anonymous
    @ApiOperation(value = "删除文件", response = AjaxResult.class)
    @PostMapping("file-del/{path}")
    public AjaxResult delUpload(@ApiParam("上传成功之后返回的路径,不要'/'之前的值") @PathVariable("path") String path) {
        boolean flag = ossService.deleteFile(path);
        return AjaxResult.success(flag);
    }


    @Anonymous
    @ApiOperation(value = "批量上传文件-返回路径")
    @PostMapping("file-batch-upload")
    public AjaxResult batchUpload(@RequestParam("files") MultipartFile[] files) throws IOException {
        List<DocumentVO> ossPathList = new ArrayList<>();
        for (MultipartFile multipartFile : files) {
            DocumentVO uploadVO = ossService.upload(multipartFile);
            ossPathList.add(uploadVO);
        }
        return AjaxResult.success(ossPathList);
    }

    @Anonymous
    @ApiOperation(value = "查看文件,返回临时公网访问地址、1小时有效期", response = AjaxResult.class)
    @GetMapping(value = "get")
    public ModelAndView get(@RequestParam("key") String key) {
        String url = ossService.getUrl(key);
        return new ModelAndView(new RedirectView(url));
    }

    @Anonymous
    @ApiOperation(value = "文件预览", response = AjaxResult.class)
    @GetMapping(value = "preview")
    public void preview(@RequestParam("key") String key, HttpServletResponse response) {
         ossService.preview(key,response);
    }
}
