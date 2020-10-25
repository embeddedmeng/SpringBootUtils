package com.embeddedmeng.easywork.controller;

import com.embeddedmeng.easywork.config.MinioConfig;
import com.embeddedmeng.easywork.service.MinioService;
import io.minio.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Api(tags = "文件上传接口")
@RestController
@RequestMapping(value = "/minio")
public class MinioController {

    @Resource
    private MinioService minioService;

    @Resource
    private MinioConfig minioConfig;

    @ApiOperation(value = "使用minio文件上传")
    @PostMapping("/uploadFile")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "MultipartFile", name = "file", value = "上传的文件", required = true),
            @ApiImplicitParam(dataType = "String", name = "bucketName", value = "对象存储桶名称", required = false)
    })
    public String uploadFile(@RequestParam MultipartFile file, @RequestParam(required = false) String bucketName) {
        try {
            bucketName = StringUtils.isNotBlank(bucketName) ? bucketName : minioConfig.getBucketName();
            if (!minioService.bucketExists(bucketName)) {
                minioService.makeBucket(bucketName);
            }
            String fileName = file.getOriginalFilename();
            String objectName = new SimpleDateFormat("yyyy/MM/dd/").format(new Date()) + UUID.randomUUID().toString().replaceAll("-", "")
                    + fileName.substring(fileName.lastIndexOf("."));

            InputStream inputStream = file.getInputStream();
            minioService.putObject(bucketName, objectName, inputStream);
            inputStream.close();
            return minioService.getObjectUrl(bucketName, objectName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @ApiOperation(value = "使用minio下载文件")
    @GetMapping("/downloadFile")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "MultipartFile", name = "file", value = "上传的文件", required = true),
            @ApiImplicitParam(dataType = "String", name = "bucketName", value = "对象存储桶名称", required = false)
    })
    public void downloadFile(String file, String buckedName) {
        try {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletResponse response = servletRequestAttributes.getResponse();
            minioService.downloadFile(buckedName, file, "", response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
