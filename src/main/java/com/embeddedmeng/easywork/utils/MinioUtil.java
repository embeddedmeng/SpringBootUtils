package com.embeddedmeng.easywork.utils;

import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.PutObjectOptions;
import io.minio.Result;
import io.minio.errors.InvalidExpiresRangeException;
import io.minio.messages.Bucket;
import io.minio.messages.DeleteError;
import io.minio.messages.Item;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class MinioUtil {

    @Resource
    private MinioClient minioClient;

    private static final int DEFAULT_EXPIRY_TIME = 7 * 24 * 3600;

    /**
     * 检查存储桶是否存在
     *
     * @param bucketName 存储桶名称
     * @return
     */
    public boolean bucketExists(String bucketName) {
        try {
            boolean flag = false;
            flag = minioClient.bucketExists(bucketName);
            if (flag) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 创建存储桶
     *
     * @param bucketName 存储桶名称
     */
    public boolean makeBucket(String bucketName) {
        boolean flag = bucketExists(bucketName);
        if (!flag) {
            try {
                minioClient.makeBucket(bucketName);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 列出所有存储桶名称
     *
     * @return
     */
    public List<String> listBucketNames() {
        List<Bucket> bucketList = listBuckets();
        List<String> bucketListName = new ArrayList<>();
        for (Bucket bucket : bucketList) {
            bucketListName.add(bucket.name());
        }
        return bucketListName;
    }

    /**
     * 列出所有存储桶
     *
     * @return
     */
    public List<Bucket> listBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 删除存储桶
     *
     * @param bucketName 存储桶名称
     * @return
     */
    public boolean removeBucket(String bucketName) {
        boolean flag = bucketExists(bucketName);
        if (flag) {
            try {
                Iterable<Result<Item>> myObjects = listObjects(bucketName);
                for (Result<Item> result : myObjects) {
                    Item item = result.get();
                    // 有对象文件，则删除失败
                    if (item.size() > 0) {
                        return false;
                    }
                }
                // 删除存储桶，注意，只有存储桶为空时才能删除成功。
                minioClient.removeBucket(bucketName);
                flag = bucketExists(bucketName);
                if (!flag) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * 列出存储桶中的所有对象名称
     *
     * @param bucketName 存储桶名称
     * @return
     */
    public List<String> listObjectNames(String bucketName) {
        try {
            List<String> listObjectNames = new ArrayList<>();
            boolean flag = bucketExists(bucketName);
            if (flag) {
                Iterable<Result<Item>> myObjects = listObjects(bucketName);
                for (Result<Item> result : myObjects) {
                    Item item = result.get();
                    listObjectNames.add(item.objectName());
                }
            }
            return listObjectNames;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    /**
     * 列出存储桶中的所有对象
     *
     * @param bucketName 存储桶名称
     * @return
     */
    public Iterable<Result<Item>> listObjects(String bucketName) {
        try {
            boolean flag = bucketExists(bucketName);
            if (flag) {
                return minioClient.listObjects(bucketName);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 通过文件上传到对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param fileName   File name
     * @return
     */
    public boolean putObject(String bucketName, String objectName, String fileName) {
        try {
            boolean flag = bucketExists(bucketName);
            if (flag) {
                minioClient.putObject(bucketName, objectName, fileName, null);
                ObjectStat statObject = statObject(bucketName, objectName);
                if (statObject != null && statObject.length() > 0) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 文件上传
     *
     * @param bucketName
     * @param multipartFile
     */
    public void putObject(String bucketName, MultipartFile multipartFile, String filename) {
        try {
            PutObjectOptions putObjectOptions = new PutObjectOptions(multipartFile.getSize(), PutObjectOptions.MIN_MULTIPART_SIZE);
            putObjectOptions.setContentType(multipartFile.getContentType());
            minioClient.putObject(bucketName, filename, multipartFile.getInputStream(), putObjectOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过InputStream上传对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param stream     要上传的流
     * @return
     */
    public boolean putObject(String bucketName, String objectName, InputStream stream) {
        try {
            boolean flag = bucketExists(bucketName);
            if (flag) {
                minioClient.putObject(bucketName, objectName, stream, new PutObjectOptions(stream.available(), -1));
                ObjectStat statObject = statObject(bucketName, objectName);
                if (statObject != null && statObject.length() > 0) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 以流的形式获取一个文件对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return
     */
    public InputStream getObject(String bucketName, String objectName) {
        try {
            boolean flag = bucketExists(bucketName);
            if (flag) {
                ObjectStat statObject = statObject(bucketName, objectName);
                if (statObject != null && statObject.length() > 0) {
                    InputStream stream = minioClient.getObject(bucketName, objectName);
                    return stream;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 以流的形式获取一个文件对象（断点下载）
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param offset     起始字节的位置
     * @param length     要读取的长度 (可选，如果无值则代表读到文件结尾)
     * @return
     */
    public InputStream getObject(String bucketName, String objectName, long offset, Long length) {
        try {
            boolean flag = bucketExists(bucketName);
            if (flag) {
                ObjectStat statObject = statObject(bucketName, objectName);
                if (statObject != null && statObject.length() > 0) {
                    InputStream stream = minioClient.getObject(bucketName, objectName, offset, length);
                    return stream;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 下载并将文件保存到本地
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param fileName   File name
     * @return
     */
    public boolean getObject(String bucketName, String objectName, String fileName) {
        try {
            boolean flag = bucketExists(bucketName);
            if (flag) {
                ObjectStat statObject = statObject(bucketName, objectName);
                if (statObject != null && statObject.length() > 0) {
                    minioClient.getObject(bucketName, objectName, fileName);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除一个对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     */
    public boolean removeObject(String bucketName, String objectName) {
        try {
            boolean flag = bucketExists(bucketName);
            if (flag) {
                minioClient.removeObject(bucketName, objectName);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除指定桶的多个文件对象,返回删除错误的对象列表，全部删除成功，返回空列表
     *
     * @param bucketName  存储桶名称
     * @param objectNames 含有要删除的多个object名称的迭代器对象
     * @return
     */
    public List<String> removeObject(String bucketName, List<String> objectNames) {
        try {
            List<String> deleteErrorNames = new ArrayList<>();
            boolean flag = bucketExists(bucketName);
            if (flag) {
                Iterable<Result<DeleteError>> results = minioClient.removeObjects(bucketName, objectNames);
                for (Result<DeleteError> result : results) {
                    DeleteError error = result.get();
                    deleteErrorNames.add(error.objectName());
                }
            }
            return deleteErrorNames;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 生成一个给HTTP GET请求用的presigned URL。
     * 浏览器/移动端的客户端可以用这个URL进行下载，即使其所在的存储桶是私有的。这个presigned URL可以设置一个失效时间，默认值是7天。
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param expires    失效时间（以秒为单位），默认是7天，不得大于七天
     * @return
     */
    public String presignedGetObject(String bucketName, String objectName, Integer expires) {
        try {
            boolean flag = bucketExists(bucketName);
            String url = "";
            if (flag) {
                if (expires < 1 || expires > DEFAULT_EXPIRY_TIME) {
                    throw new InvalidExpiresRangeException(expires,
                            "expires must be in range of 1 to " + DEFAULT_EXPIRY_TIME);
                }
                url = minioClient.presignedGetObject(bucketName, objectName, expires);
            }
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成一个给HTTP PUT请求用的presigned URL。
     * 浏览器/移动端的客户端可以用这个URL进行上传，即使其所在的存储桶是私有的。这个presigned URL可以设置一个失效时间，默认值是7天。
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @param expires    失效时间（以秒为单位），默认是7天，不得大于七天
     * @return
     */
    public String presignedPutObject(String bucketName, String objectName, Integer expires) {
        try {
            boolean flag = bucketExists(bucketName);
            String url = "";
            if (flag) {
                if (expires < 1 || expires > DEFAULT_EXPIRY_TIME) {
                    throw new InvalidExpiresRangeException(expires,
                            "expires must be in range of 1 to " + DEFAULT_EXPIRY_TIME);
                }
                url = minioClient.presignedPutObject(bucketName, objectName, expires);
            }
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取对象的元数据
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return
     */
    public ObjectStat statObject(String bucketName, String objectName) {
        try {
            boolean flag = bucketExists(bucketName);
            if (flag) {
                ObjectStat statObject = minioClient.statObject(bucketName, objectName);
                return statObject;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 文件访问路径
     *
     * @param bucketName 存储桶名称
     * @param objectName 存储桶里的对象名称
     * @return
     */
    public String getObjectUrl(String bucketName, String objectName) {
        try {
            boolean flag = bucketExists(bucketName);
            String url = "";
            if (flag) {
                url = minioClient.getObjectUrl(bucketName, objectName);
            }
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    public void downloadFile(String bucketName, String fileName, String originalName, HttpServletResponse response) {
        try {

            InputStream file = minioClient.getObject(bucketName, fileName);
            String filename = new String(fileName.getBytes("ISO8859-1"), StandardCharsets.UTF_8);
            if (StringUtils.isNotEmpty(originalName)) {
                fileName = originalName;
            }
            response.setHeader("Content-Disposition", "attachment;filename=" + filename);
            ServletOutputStream servletOutputStream = response.getOutputStream();
            int len;
            byte[] buffer = new byte[1024];
            while ((len = file.read(buffer)) > 0) {
                servletOutputStream.write(buffer, 0, len);
            }
            servletOutputStream.flush();
            file.close();
            servletOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
