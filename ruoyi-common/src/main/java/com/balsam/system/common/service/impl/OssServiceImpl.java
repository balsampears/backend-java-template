package com.balsam.system.common.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.internal.OSSUtils;
import com.aliyun.oss.model.*;
import com.balsam.system.common.config.AlibabaOssProperties;
import com.balsam.system.common.service.OssService;
import com.balsam.system.common.service.domain.BatchDocumentDTO;
import com.balsam.system.common.service.domain.DocumentDTO;
import com.balsam.system.common.service.domain.DocumentVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 链接oss服务器的相关资源信息
 *
 * @author Hua
 * @date 2018/10/17 21:05
 */
@Component
@Slf4j
public class OssServiceImpl implements OssService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OssServiceImpl.class);
    @Value("${spring.profiles.active}")
    public String active;
    @Resource
    private AlibabaOssProperties properties;

    /**
     * 判断文件是否存在
     *
     * @param ossKey 文件路径
     */
    public boolean isFileExists(String ossKey) {
        LOGGER.info("===================调用OSS下载文件开始=============================");
        ossKey = ossKeyWrap(ossKey);
        LOGGER.info("===================传入参数ossKey值为：" + ossKey);
        boolean fileExist = false;
        OSSClient ossClient = new OSSClient(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
        try {
            ossKey = checkOssKey(ossKey);
            fileExist = ossClient.doesObjectExist(properties.getBucketName(), ossKey);
        } catch (OSSException e) {
            LOGGER.error("oss发生异常，异常类型{OSSException}" + e.getMessage());
        } catch (ClientException e) {
            LOGGER.error("oss发生异常，异常类型{ClientException}" + e.getMessage());
        } catch (RuntimeException e) {
            LOGGER.error("oss发生异常，异常类型{RuntimeException}" + e.getMessage());
        } finally {
            ossClient.shutdown();
        }
        return fileExist;
    }

    /**
     * 上传文件(无大小限制的方法)
     *
     * @param filePath 上传文件绝对路径  例："C:/Users/Administrator/Desktop/2017_08_16_02_42_07_621.jpg"
     * @param ossKey   例："/file/2017_08_16_02_42_07_621.jpg"
     * @throws RuntimeException FileNotFoundException
     */
    public void uploadFile(String filePath, String ossKey) throws RuntimeException, FileNotFoundException {
        LOGGER.info("===================调用OSS下载文件开始=============================");
        LOGGER.info("===================传入参数ossKey值为：" + ossKey + "\tfilePath地址为" + filePath);
        OSSClient ossClient = new OSSClient(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
        File file = new File(filePath.trim());
        //picsize(file);
        String fileName = file.getName();
        ossKey = checkFile(fileName, ossKey);
        InputStream inputStream = new FileInputStream(filePath);
        ossClient.putObject(properties.getBucketName(), ossKey, inputStream);
        ossClient.shutdown();
        LOGGER.info("上传文件【" + ossKey + "】成功");
    }

    /**
     * 上传文件（限制文件最大为1M）
     *
     * @param file   上传的文件
     * @param ossKey oss存放路径 key  例："/file/2017_08_16_02_42_07_621.jpg"
     * @throws RuntimeException FileNotFoundException
     */
    public void uploadFile(File file, String ossKey) throws RuntimeException, FileNotFoundException {
        LOGGER.info("===================调用OSS下载文件开始=============================");
        LOGGER.info("===================传入参数ossKey值为：" + ossKey);
        OSSClient ossClient = new OSSClient(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
        String fileName = file.getName();
        ossKey = checkFile(fileName, ossKey);
        InputStream inputStream = new FileInputStream(file.getPath());
        ossClient.putObject(properties.getBucketName(), ossKey, inputStream);
        ossClient.shutdown();
        LOGGER.info("上传文件【" + ossKey + "】成功");
    }

    /**
     * 上传文件流信息
     *
     * @param bytes
     * @param path
     * @return path
     */
    public String upload(byte[] bytes, String path) {
        return upload(new ByteArrayInputStream(bytes), path);
    }

    /**
     * 上传文件,传入输入流,使用默认地址
     *
     * @param inputStream 字节流
     * @return OSS 对象 key（与存储一致；路径中的反斜杠会写成 %5C 便于传输）
     * @throws RuntimeException
     */


    public String upload(InputStream inputStream, String path) throws RuntimeException {

        LOGGER.info("===================调用OSS下载文件开始=============================");
        LOGGER.info("===================传入参数path值为：" + path);
        OSSClient ossClient = new OSSClient(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
        path = checkOssKey(path);
        // 创建上传Object的Metadata
        ObjectMetadata meta = new ObjectMetadata();
        // 被下载时网页的缓存行为
        meta.setCacheControl("no-cache");
        //创建上传请求
        PutObjectRequest request = new PutObjectRequest(properties.getBucketName(), path, inputStream, meta);
        ossClient.putObject(request);
        ossClient.shutdown();

        LOGGER.info("===================  oss上传文件成功:{}", path);
        return toClientOssKey(path);
    }

    /**
     * 根据文件名删除OSS服务器上的文件
     *
     * @param ossKey oss存放路径 key  例："/file/2017_08_16_02_42_07_621.jpg"
     * @return boolean
     */
    public boolean deleteFile(String ossKey) throws RuntimeException {
        LOGGER.info("===================调用OSS删除文件开始=============================");
        ossKey = ossKeyWrap(ossKey);
        LOGGER.info("===================传入参数ossKey" + ossKey);
        OSSClient ossClient = new OSSClient(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
        ossKey = checkOssKey(ossKey);
        boolean fileExist = ossClient.doesObjectExist(properties.getBucketName(), ossKey);
        if (fileExist) {
            ossClient.deleteObject(properties.getBucketName(), ossKey);
            LOGGER.info("删除文件【" + ossKey + "】成功");
            ossClient.shutdown();
            return true;
        } else {
            LOGGER.error("删除文件【" + ossKey + "】失败");
        }

        return false;
    }

    /**
     * 根据文件名下载服务器上的文件(浏览器提示保存路径)
     *
     * @param ossKey oss存放路径 key  例："/file/2017_08_16_02_42_07_621.jpg"
     * @return
     */
    public void downloadFile(String ossKey, HttpServletResponse response) throws RuntimeException, IOException {
        LOGGER.info("===================调用OSS下载文件开始=============================");
//        ossKey = ossKeyWrap(ossKey);
        LOGGER.info("===================传入参数ossKey" + ossKey);
        InputStream inputStream = null;
        try {
            OSSClient ossClient = new OSSClient(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
            String datafilename = checkFileName(ossKey);
            OSSObject ossObject = ossClient.getObject(properties.getBucketName(), datafilename);
            response.setContentType("application/x-msdownload;");
            response.setHeader("Content-disposition", "attachment; filename=" + new String(ossKey.getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
            inputStream = ossObject.getObjectContent();
            IoUtil.copy(inputStream, response.getOutputStream());
            ossClient.shutdown();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            // 关闭流和连接
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据上传返回的key 直接下载文件
     * 不判断环境
     *
     * @param ossKey
     * @param response
     * @throws RuntimeException
     * @throws IOException
     */
    public void downloadByKey(String ossKey, HttpServletResponse response) throws RuntimeException, IOException {
        LOGGER.info("===================调用OSS下载文件开始=============================");
        ossKey = ossKeyWrap(ossKey);
        LOGGER.info("===================传入参数ossKey" + ossKey);
        InputStream inputStream = null;
        try {
            OSSClient ossClient = new OSSClient(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
            OSSObject ossObject = ossClient.getObject(properties.getBucketName(), ossKey);
            response.setContentType("application/x-msdownload;");
            response.setHeader("Content-disposition", "attachment; filename=" + new String(ossKey.getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
            inputStream = ossObject.getObjectContent();
            IoUtil.copy(inputStream, response.getOutputStream());
            ossClient.shutdown();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            // 关闭流和连接
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据文件名下载服务器上的文件到指定目录(浏览器不提示保存路径)
     *
     * @param ossKey oss存放路径 key
     * @param path   存放的绝对路径带文件名 例："C:/Users/Administrator/Desktop/2017_08_16_02_42_07_621.jpg"
     */
    public void downloadFile(String ossKey, String path) throws RuntimeException {
        LOGGER.info("===================调用OSS下载文件开始=============================");
        ossKey = ossKeyWrap(ossKey);
        LOGGER.info("===================传入参数path值为：" + path + "\t传入参数ossKey" + ossKey);
        OSSClient ossClient = new OSSClient(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
        ossKey = checkFileName(ossKey);
        // 下载object到文件
        ossClient.getObject(new GetObjectRequest(properties.getBucketName(), ossKey), new File(path));
        ossClient.shutdown();
        LOGGER.info("下载文件【" + ossKey + "】成功");
    }

    /**
     * 下载文件
     * byte数据了类型
     *
     * @param path 文件地址
     * @return 文件数据
     */
    public byte[] download(String path) throws RuntimeException, IOException {
        path = ossKeyWrap(path);
        LOGGER.info("===================调用OSS下载文件开始=============================");
        LOGGER.info("===================传入参数" + path);
        OSSClient ossClient = new OSSClient(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
        InputStream inputStream = ossClient.getObject(properties.getBucketName(), path.toLowerCase()).getObjectContent();
        byte[] bytes = IOUtils.toByteArray(inputStream);
        ossClient.shutdown();
        LOGGER.info("===================调用OSS下载文件结束=============================");
        LOGGER.info("下载文件【" + path + "】成功");
        return bytes;
    }

    /**
     * 文件下载
     *
     * @param path 文件路径
     * @return InputStream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public InputStream downloadToInputStream(String path) throws Exception {
        path = ossKeyWrap(path);
        LOGGER.info("===================调用OSS下载文件开始=============================");
        LOGGER.info("===================传入参数" + path);
        OSSClient ossClient = new OSSClient(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
        InputStream inputStream = ossClient.getObject(properties.getBucketName(), path).getObjectContent();
        ossClient.shutdown();
        LOGGER.info("下载文件【" + path + "】成功");
        return inputStream;
    }

    /**
     * 获取oss文件目录
     *
     * @return List
     */
    public List<OSSObjectSummary> getFileList() {
        LOGGER.info("===================调用OSS产看文件目录开始=============================");
        OSSClient ossClient = new OSSClient(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
        List<OSSObjectSummary> list = ossClient.listObjects(properties.getBucketName()).getObjectSummaries();
        LOGGER.info("oss目录文件是：" + JSONObject.toJSONString(list));
        ossClient.shutdown();
        return list;
    }

    /**
     * 将一段文字写入文件，然后上传到文件服务器
     *
     * @param content 字符串
     * @return OSS 对象 key
     * @throws RuntimeException
     */
    public String upload(String content, String path) throws RuntimeException {
        File file = null;
        PrintStream ps = null;
        String uploadPath = null;
        try {
            String sysPath = System.getProperty("user.dir");
            String tempFilePath = sysPath + "\\" + System.currentTimeMillis();
            file = File.createTempFile(tempFilePath, ".txt");
            ps = new PrintStream(new FileOutputStream(file));
            ps.println(content);
            uploadPath = upload(new FileInputStream(file), path);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            if (file != null && file.isFile()) {
                file.delete();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return uploadPath;
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    public DocumentVO upload(MultipartFile file) throws IOException {
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        String path = IdUtil.simpleUUID() + "-" + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN) + "." + suffix;
        if (StrUtil.isEmpty(file.getOriginalFilename())) {
            throw new RuntimeException("文件名称不能为空");
        }
        if (file.getOriginalFilename().length() > 256) {
            throw new RuntimeException("文件名称过长");
        }
        if (!OSSUtils.validateObjectKey(path)) {
            throw new RuntimeException("文件的命名不合法");
        }
        String ossPath = upload(file.getInputStream(), path, file.getOriginalFilename());
        DocumentVO vo = new DocumentVO();
        vo.setName(file.getOriginalFilename());
        vo.setKey(toClientOssKey(ossPath));
        return vo;
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    public DocumentVO upload(MultipartFile file, String fileSecondPath) throws IOException {
        String fileOriginName = file.getOriginalFilename();
        if (StrUtil.isEmpty(fileOriginName)) {
            fileOriginName = "006.zip";
        }
        //例如 234234/006.zip
        String path = fileSecondPath + File.separator + fileOriginName;
        log.info("非验签文件上传路径"+path);
        String ossPath = this.uploadByFileSecondPath(file.getInputStream(), path, fileOriginName);
        DocumentVO vo = new DocumentVO();
        vo.setName(fileOriginName);
        vo.setKey(toClientOssKey(ossPath));
        return vo;
    }

    /**
     * 非重构上传贷款文件
     * @param inputStream
     * @param fileSecondPath
     * @param fileOriginName
     * @return
     * @throws UnsupportedEncodingException
     */
    public DocumentVO uploadLoanFile(InputStream inputStream, String fileSecondPath, String fileOriginName) throws UnsupportedEncodingException {
        String path = fileSecondPath + File.separator + fileOriginName;
        String ossPath = this.upload(inputStream, path, fileOriginName);
        DocumentVO vo = new DocumentVO();
        vo.setName(fileOriginName);
        vo.setKey(toClientOssKey(ossPath));
        return vo;
    }

    /**
     * 根据指定文件名上传pdf文件
     *
     * @param file
     * @param fileSecondPath 文件二级目录
     * @param
     * @return
     * @throws IOException
     */
    public DocumentVO uploadPdfByFileName(MultipartFile file, String fileSecondPath, String fileName) throws IOException {
        String fileOriginName = fileName + ".pdf";
        //例如 234234/006.zip
        String path = fileSecondPath + File.separator + fileOriginName;
        String ossPath = this.uploadByFileSecondPath(file.getInputStream(), path, fileOriginName);
        DocumentVO vo = new DocumentVO();
        vo.setName(fileOriginName);
        vo.setKey(toClientOssKey(ossPath));
        return vo;
    }


    /**
     * <br>Created by Hua on 2019/3/1  11:18
     *
     * @param inputStream  文件
     * @param path         OSS存储文件名称
     * @param downFileName 下载文件名称
     * @return
     * @throws RuntimeException
     */
    public String uploadByFileSecondPath(InputStream inputStream, String path, String downFileName) throws RuntimeException, UnsupportedEncodingException {
        LOGGER.info("===================调用OSS下载文件开始=============================");
        LOGGER.info("===================传入参数path值为：" + path);
        OSSClient ossClient = new OSSClient(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
        path = getFileDir(path);
        // 创建上传Object的Metadata
        ObjectMetadata meta = new ObjectMetadata();
        downFileName = URLEncoder.encode(downFileName, "UTF-8");
        meta.setContentDisposition("attachment;filename=\"" + downFileName + "\"");
        // 被下载时网页的缓存行为
        meta.setCacheControl("no-cache");
        log.info("上传路径：" + path);
        log.info("下载文件名称：" + downFileName);
        //创建上传请求
        PutObjectRequest request = new PutObjectRequest(properties.getBucketName(), path, inputStream, meta);
        ossClient.putObject(request);
        ossClient.shutdown();
        LOGGER.info("===================  oss上传文件成功:{}", path);
        //上传成功返回的文件路径
        return path;
    }

    /**
     * <br>Created by Hua on 2019/3/1  11:18
     *
     * @param inputStream  文件
     * @param path         OSS存储文件名称
     * @param downFileName 下载文件名称
     * @return
     * @throws RuntimeException
     */
    public String upload(InputStream inputStream, String path, String downFileName) throws RuntimeException, UnsupportedEncodingException {
        LOGGER.info("===================调用OSS下载文件开始=============================");
        LOGGER.info("===================传入参数path值为：" + path);
        OSSClient ossClient = new OSSClient(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
        path = checkOssKey(path);
        // 创建上传Object的Metadata
        ObjectMetadata meta = new ObjectMetadata();
        downFileName = URLEncoder.encode(downFileName, "UTF-8");
        meta.setContentDisposition("inline;filename=\"" + downFileName + "\"");
        // 被下载时网页的缓存行为
        meta.setCacheControl("no-cache");
        //创建上传请求
        PutObjectRequest request = new PutObjectRequest(properties.getBucketName(), path, inputStream, meta);
        ossClient.putObject(request);
        ossClient.shutdown();
        LOGGER.info("===================  oss上传文件成功:{}", path);
        //上传成功返回的文件路径
        return path;
    }

    /**
     * 上传文件（根据下载链接）
     * @param file
     * @return
     */
    public DocumentVO uploadByUrl(DocumentDTO file) {
        DocumentVO vo = new DocumentVO();
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(file.getUrl());
            conn = (HttpURLConnection) url.openConnection();  //连接
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows NT; DigExt)");
            //得到输入流
            inputStream = conn.getInputStream();
            //获取路径
            String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            String path = IdUtil.simpleUUID() + "-" + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN) + "." + suffix;
            if (!OSSUtils.validateObjectKey(path)) {
                throw new RuntimeException("文件的命名不合法");
            }
            String ossPath = upload(inputStream, path, file.getName());
            vo.setName(file.getName());
            vo.setKey(toClientOssKey(ossPath));
        } catch (Exception e) {
            log.error("[oss] 转发第三方文件到oss平台异常", e);
            throw new RuntimeException("上传文件到oss异常");
        } finally {
            // 关闭流和连接
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return vo;
    }

    /**
     * 上传文件base64字符串
     *
     * @param base64 base64加密的文件内容
     * @return fileName 带有后缀的文件名称
     * @throws IOException
     */
    public DocumentVO uploadBase64Str(String base64, String fileName) throws UnsupportedEncodingException {
        String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String path = IdUtil.simpleUUID() + "-" + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN) + "." + prefix;
        if (!OSSUtils.validateObjectKey(path)) {
            throw new RuntimeException("文件的命名不合法");
        }
        byte[] decode = Base64.decode(base64);
        InputStream inputStream = new ByteArrayInputStream(decode);
        String ossPath = upload(inputStream, path, fileName);
        DocumentVO vo = new DocumentVO();
        vo.setName(fileName);
        vo.setKey(toClientOssKey(ossPath));
        return vo;
    }

    /**
     * 批量下载文件,直接输出到浏览器
     *
     * @param batchDocumentDTO 文件
     * @return byte[]
     */
    public void batchDownload(BatchDocumentDTO batchDocumentDTO, HttpServletResponse httpServletResponse) throws UnsupportedEncodingException {
        List<DocumentDTO> documentDTOs = batchDocumentDTO.getDocumentDTOs();
        if (CollUtil.isEmpty(documentDTOs)) {
            return;
        }
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setContentType("multipart/form-data");
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(batchDocumentDTO.getZipName(), "UTF-8") + ".zip");
        //创建zip文件输出流
        BufferedInputStream bis = null;
        OSSClient ossClient = new OSSClient(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
        try (ZipOutputStream zos = new ZipOutputStream(httpServletResponse.getOutputStream())) {
            int sortNum = 1;
            for (DocumentDTO dto : documentDTOs) {
                String doc = ossKeyWrap(dto.getUrl());
                OSSObject ossObject = ossClient.getObject(properties.getBucketName(), doc);
                if (ossObject != null) {
                    InputStream inputStream = ossObject.getObjectContent();
                    //将每一个文件写入zip文件包内，即进行打包
                    byte[] buffs = new byte[1024 * 10];
                    ZipEntry zipEntry = new ZipEntry(sortNum + "-" + dto.getName());
                    zos.putNextEntry(zipEntry);
                    bis = new BufferedInputStream(inputStream, 1024 * 10);
                    int read;
                    while ((read = bis.read(buffs, 0, 1024 * 10)) != -1) {
                        zos.write(buffs, 0, read);
                    }
                    ossObject.close();
                }
                sortNum++;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException("批量下载文件异常");
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                httpServletResponse.getOutputStream().flush();
                httpServletResponse.getOutputStream().close();
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
            try {
                if (ossClient != null) {
                    ossClient.shutdown();
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    /**
     * 从url获得可访问的链接
     * @param url
     * @return
     */
    public String getUrlByUrl(String url) {
        int urlIndex = url.indexOf("=");
        String ossServiceUrl = this.getUrl(url.substring(urlIndex + 1));
        return ossServiceUrl;
    }

    /**
     * 获得url链接
     *
     * @param key
     * @return
     */
    public String getUrl(String key) {
        // 设置URL过期时间为24小时
        Date expiration = new Date(System.currentTimeMillis() + 24 * 3600L * 1000);
        // 创建ClientBuilderConfiguration实例，您可以根据实际情况修改默认参数。
        ClientConfiguration config=new ClientConfiguration();
        // 设置是否支持CNAME。CNAME用于将自定义域名绑定到目标Bucket。
        config.setSupportCname(true);
        //初始化客户端
        OSSClient ossClient = new OSSClient(properties.getEndpointView(), properties.getAccessKeyId(), properties.getAccessKeySecret(),config);
        // 生成URL
        URL url = ossClient.generatePresignedUrl(properties.getBucketName(), key, expiration);
        if (url != null) {
            return url.toString();
        }
        ossClient.shutdown();
        return null;
    }

    private String checkFileName(String fileName) throws RuntimeException {
        if (fileName == null || fileName.length() < 1) {
            LOGGER.error("fileName：" + fileName);
            throw new RuntimeException("fileName 不能为空");
        }
        return getFileDir(fileName);
    }

    private String checkOssKey(String ossKey) throws RuntimeException {
        if (ossKey == null || ossKey.length() < 1) {
            LOGGER.error("osskey值为：" + ossKey);
            throw new RuntimeException("ossKey 不能为空");
        }
        return getFileDir(ossKey);
    }

    private String checkFile(String fileName, String ossKey) throws RuntimeException {
        if (fileName == null || fileName.length() < 1) {
            LOGGER.error("fileName值：" + fileName);
            throw new RuntimeException("fileName 不能为空");
        }
        if (ossKey == null || ossKey.length() < 1) {
            LOGGER.error("ossKey值：" + ossKey);
            throw new RuntimeException("ossKey 不能为空");
        }
        return getFileDir(ossKey);
    }

    private String getFileDir(String fileName) {
        String ossKey = properties.getFileDir() + File.separator + DateUtil.format(new Date(), "yyyyMMdd") + File.separator + fileName;
        return ossKey;
    }

    private String getFileDir(String fileName, String secondFilePath) {
        String ossKey = properties.getFileDir() + File.separator + secondFilePath + File.separator + fileName;
        return ossKey;
    }


    /** 返回给前端的 OSS key：与桶内对象名一致，反斜杠写成 %5C（与历史 query 传参约定一致）。 */
    private static String toClientOssKey(String ossPath) {
        return ossPath.replace("\\", "%5C");
    }

    /**
     * 下载路径出来
     *
     * @param path
     * @return
     */
    private String ossKeyWrap(String path) {
        //去除会显示加的前缀
        String doc = path.replaceFirst("/upload/get\\?key=", "");
        //windows系统
        doc = doc.replace(active + "%5C", active + "\\");
        return doc;
    }

    /**
     * 根据Url上传文件
     *
     * @param fileUrl
     * @param fileName
     * @return
     * @throws IOException
     */
    public DocumentVO uploadByUrl(String fileUrl, String fileName) throws IOException {
        DocumentVO vo = new DocumentVO();
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(fileUrl);
            conn = (HttpURLConnection) url.openConnection();  //连接
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows NT; DigExt)");
            //得到输入流
            inputStream = conn.getInputStream();
            //获取路径
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
            String path = IdUtil.simpleUUID() + "-" + DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN) + "." + suffix;
            if (!OSSUtils.validateObjectKey(path)) {
                throw new IOException("文件的命名不合法");
            }
            String ossPath = upload(inputStream, path, fileName);
            vo.setName(fileName);
            vo.setKey(toClientOssKey(ossPath));

        } finally {
            // 关闭流和连接
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return vo;

    }

    /**
     * 根据URL，指定文件名上传pdf文件
     *
     * @param fileUrl        文件下载地址
     * @param fileSecondPath 文件二级目录
     * @param
     * @return
     * @throws IOException
     */
    public DocumentVO uploadByUrlAndFileName(String fileUrl, String fileSecondPath, String fileName)  {
        DocumentVO vo = new DocumentVO();
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(fileUrl);
            conn = (HttpURLConnection) url.openConnection();  //连接
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows NT; DigExt)");
            //得到输入流
            inputStream = conn.getInputStream();

            String fileOriginName = fileName + ".pdf";
            String path = fileSecondPath + File.separator + fileOriginName;
            String ossPath = this.uploadByFileSecondPath(inputStream, path, fileOriginName);
            vo.setName(fileOriginName);
            vo.setKey(toClientOssKey(ossPath));

        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            // 关闭流和连接
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return vo;
    }

    /**
     * 根据上传返回的key 返回
     * 不判断环境
     *
     * @param ossKey
     */
    public MultipartFile getMultipartFileByKey(String ossKey){
        LOGGER.info("===================调用OSS下载文件开始=============================");
        ossKey = ossKeyWrap(ossKey);
        LOGGER.info("===================传入参数ossKey" + ossKey);
        MultipartFile multipartFile = null;
        InputStream in = null;
        try {
            OSSClient ossClient = new OSSClient(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
            OSSObject ossObject = ossClient.getObject(properties.getBucketName(), ossKey);
            String filename = new String(ossKey.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
            in = ossObject.getObjectContent();
            multipartFile = getMultipartFile(in, filename);
            ossClient.shutdown();
        } catch (IOException e) {
            LOGGER.error("Stream copy exception", e);
            throw new IllegalArgumentException("文件上传失败");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.error("Stream close exception", e);
                }
            }
        }
        return multipartFile;
    }

    /**
     * 获取封装得MultipartFile
     *
     * @param inputStream inputStream
     * @param fileName    fileName
     * @return MultipartFile
     */
    private MultipartFile getMultipartFile(InputStream inputStream, String fileName) {
        FileItem fileItem = createFileItem(inputStream, fileName);
        //CommonsMultipartFile是feign对multipartFile的封装，但是要FileItem类对象
        return new CommonsMultipartFile(fileItem);
    }


    /**
     * FileItem类对象创建
     *
     * @param inputStream inputStream
     * @param fileName    fileName
     * @return FileItem
     */
    private FileItem createFileItem(InputStream inputStream, String fileName) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        String textFieldName = "file";
        FileItem item = factory.createItem(textFieldName, MediaType.MULTIPART_FORM_DATA_VALUE, true, fileName);
        int bytesRead = 0;
        byte[] buffer = new byte[10 * 1024 * 1024];
        OutputStream os = null;
        //使用输出流输出输入流的字节
        try {
            os = item.getOutputStream();
            while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            inputStream.close();
        } catch (IOException e) {
            LOGGER.error("Stream copy exception", e);
            throw new IllegalArgumentException("文件上传失败");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    LOGGER.error("Stream close exception", e);

                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error("Stream close exception", e);
                }
            }
        }

        return item;
    }

    public void preview(String key, HttpServletResponse response) {
        InputStream in = null;
        OSSClient ossClient = new OSSClient(properties.getEndpoint(), properties.getAccessKeyId(), properties.getAccessKeySecret());
        ServletOutputStream outputStream=null;
        try {
            GetObjectRequest getObjectRequest = new GetObjectRequest(properties.getBucketName(), key);
            getObjectRequest.setProcess("doc/preview,export_1,print_1/watermark,text_5YaF6YOo6LWE5paZ,size_30,t_60");
            // 使用getObject方法，并通过process参数传入处理指令。
            OSSObject ossObject = ossClient.getObject(getObjectRequest);
            // 读取文档信息。
            outputStream= response.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = ossObject.getObjectContent().read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            LOGGER.error("Stream copy exception", e);
            throw new IllegalArgumentException("文件预览失败");
        } finally {
            ossClient.shutdown();
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    LOGGER.error("Stream close exception", e);
                }
            }
        }
    }
}
