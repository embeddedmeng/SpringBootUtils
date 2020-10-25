package com.embeddedmeng.easywork.service.impl;

import com.embeddedmeng.easywork.service.MinioService;
import com.embeddedmeng.easywork.utils.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class MinioServiceImpl implements MinioService {

    @Autowired
    private MinioUtil minioUtil;

    /**
     * 判断 bucket是否存在
     *
     * @param bucketName
     * @return
     */
    @Override
    public boolean bucketExists(String bucketName) {
        return minioUtil.bucketExists(bucketName);
    }

    /**
     * 创建 bucket
     *
     * @param bucketName
     */
    @Override
    public void makeBucket(String bucketName) {
        minioUtil.makeBucket(bucketName);
    }

    /**
     * 文件上传
     *
     * @param bucketName
     * @param objectName
     * @param filename
     */
    @Override
    public void putObject(String bucketName, String objectName, String filename) {
        minioUtil.putObject(bucketName, objectName, filename);
    }


    @Override
    public void putObject(String bucketName, String objectName, InputStream stream) {
        minioUtil.putObject(bucketName, objectName, stream);
    }

    /**
     * 文件上传
     *
     * @param bucketName
     * @param multipartFile
     */
    @Override
    public void putObject(String bucketName, MultipartFile multipartFile, String filename) {
        minioUtil.putObject(bucketName, multipartFile, filename);
    }

    /**
     * 删除文件
     * @param bucketName
     * @param objectName
     */
    @Override
    public boolean removeObject(String bucketName,String objectName) {
        return minioUtil.removeObject(bucketName,objectName);
    }

    /**
     * 下载文件
     *
     * @param fileName
     * @param originalName
     * @param response
     */
    @Override
    public void downloadFile(String bucketName, String fileName, String originalName, HttpServletResponse response) {
        minioUtil.downloadFile(bucketName,fileName, originalName, response);
    }

    /**
     * 获取文件路径
     * @param bucketName
     * @param objectName
     * @return
     */
    @Override
    public String getObjectUrl(String bucketName,String objectName) {
        return minioUtil.getObjectUrl(bucketName,objectName);
    }

    /**
     * 批量下载图片
     * @param strings
     * @return
     * @throws Exception
     */
    public List<String> getUriList(List<String> strings) throws Exception {

        List<String> list=new ArrayList<>();
        String uri;
        for (String s:strings){
            uri="";
            uri = minioUtil.presignedGetObject("hope-bucket", s, 0);
            list.add(uri);
        }

        return list;
    }

}
