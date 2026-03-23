package com.balsam.system.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里oss配置文件
 *
 * @author
 * @date 2018年10月23日14:30:59
 */
@Configuration
@Data
public class AlibabaOssProperties {
    /**
     * 阿里云API的内或外网域名
     */
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    /**
     * 阿里云API的密钥Access Key ID
     */
    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;
    /**
     * 阿里云API的密钥Access Key Secret
     */
    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;
    /**
     * 阿里云bucket名称
     */
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;
    /**
     * 阿里云文件夹名称
     */
    @Value("${aliyun.oss.fileDir}")
    private String fileDir;
    /**
     * 预览接口
     */
    @Value("${aliyun.oss.endpointView}")
    private String endpointView;

    /**
     * 预览接口
     */
    @Value("${aliyun.oss.endpointImg}")
    private String endpointImg;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("AlibabProperties{");
        sb.append("endpoint='").append(endpoint).append('\'');
        sb.append(", accessKeyId='").append(accessKeyId).append('\'');
        sb.append(", accessKeySecret='").append(accessKeySecret).append('\'');
        sb.append(", bucketName='").append(bucketName).append('\'');
        sb.append(", fileDir='").append(fileDir).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
