# SpringBootUtils
基于SpringBoot 的 工具集

### 各部分功能说明
#### 一、SpringBoot异步任务的基本使用
##### 1）ExecutorConfig 自定义的Executor:
```
- @EnableAsync 开启Spring异步支持
- @Bean("taskExecutor") 指定默认任务执行器
```
##### 2）AsyncTaskService 异步任务方法接口:

##### 3）AsyncTaskServiceImpl 异步任务方法实现:
```
- @Async("myAsync") 使用自定义myAsync任务执行器
- @Async 使用默认taskExecutor任务执行器
```
##### 4）AsyncTaskController 异步任务接口
##### *** 需要注意的问题
```
需要注意的问题:
需要注意的问题一：异步方法的定义位置问题
最好将异步调用的方法单独放在一个@Component类中，或者说不要将异步调用方法写在@Controller中，否则将无法进行调用，因为SpringBoot使用@Transaction需要经过事务拦截器，只有通过了该事务拦截器的方法才能被加入Spring的事务管理器中，而在同一个类中的一个方法调用另一个方法只会经过一次事务拦截器，所以如果是后面的方法使用了事务注解将不会生效，在这里异步调用也是同样的道理
需要注意的问题二：异步方法的事务调用问题
在@Async注解的方法上再使用@Transaction注解是无效的，在@Async注解的方法中调用Service层的事务方法是有效的
需要注意的问题三：异步方法必须是实例的
因为静态方法不能被override重写，因为@Async异步方法的实现原理是通过注入一个代理类到Bean中，该代理类集成这个Bean并且需要重写这个异步方法，所以需要是实例方法
```
#### 二、Swagger3 的基本使用
##### 1）pom.xml
```$xslt
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```
##### 2）Swagger3Config Swagger3配置文件
##### *** 其他说明
```$xslt
- Swagger3的访问路径为port/swagger-ui/ 或port/swagger-ui/index.html
```
##### 3）Swagger3注解说明
```$xslt
@Api：用在请求的类上，表示对类的说明
    tags="说明该类的作用，可以在UI界面上看到的注解"
    value="该参数没什么意义，在UI界面上也看到，所以不需要配置"

@ApiOperation：用在请求的方法上，说明方法的用途、作用
    value="说明方法的用途、作用"
    notes="方法的备注说明"

@ApiImplicitParams：用在请求的方法上，表示一组参数说明
    @ApiImplicitParam：用在@ApiImplicitParams注解中，指定一个请求参数的各个方面
        name：参数名
        value：参数的汉字说明、解释
        required：参数是否必须传
        paramType：参数放在哪个地方
            · header --> 请求参数的获取：@RequestHeader
            · query --> 请求参数的获取：@RequestParam
            · path（用于restful接口）--> 请求参数的获取：@PathVariable
            · body（不常用）
            · form（不常用）    
        dataType：参数类型，默认String，其它值dataType="Integer"       
        defaultValue：参数的默认值

@ApiResponses：用在请求的方法上，表示一组响应
    @ApiResponse：用在@ApiResponses中，一般用于表达一个错误的响应信息
        code：数字，例如400
        message：信息，例如"请求参数没填好"
        response：抛出异常的类

@ApiModel：用于响应类上，表示一个返回响应数据的信息
            （这种一般用在post创建的时候，使用@RequestBody这样的场景，
            请求参数无法使用@ApiImplicitParam注解进行描述的时候）
    @ApiModelProperty：用在属性上，描述响应类的属性
```
#### 三、Minio 文件系统的基本使用
##### 1）pom.xml
```
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.7</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>

<!--Minio-->
<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>7.0.2</version>
</dependency>
```
##### 2）application.yml
```
minio:
  # minio服务地址(使用时替换成minio部署地址)
  endpoint: localhost
  # minio端口(使用时替换成minio部署端口) 
  port: 9000 
  # 配置登录key 
  accessKey: AKIAIOSFODNN7EXAMPLE
  secretKey: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
  # 默认为true => https形式
  secure: false 
  # 默认桶
  bucketName: "hope-bucket"
```
##### 3）MinioUtil minio的工具方法类
##### 4）MinioConfig minio配置文件
##### 5）MinioService minio基本操作接口
##### 6）MinioServiceImpl minio基本操作实现
##### 7）MinioController minio上传，下载测试接口
##### 8）Minio Docker部署
```$xslt
docker run -d -p 9000:9000 --name myminio --restart=always \
  -e "MINIO_ACCESS_KEY=AKIAIOSFODNN7EXAMPLE" \
  -e "MINIO_SECRET_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY" \
  minio/minio server /data

其他部署方式可参考：https://docs.min.io/cn/
```
#### 四、Kafka的基本使用
##### 1）pom.xml
```
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.42</version>
</dependency>
<!--Kafka-->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```
##### 2）application.yml
```
spring:
  # 配置kafka
  kafka:
    # kafka服务地址(使用时替换成kafka部署地址:端口)
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
    consumer:
      group-id: default_consumer_group #群组ID
      enable-auto-commit: true
      auto-commit-interval: 1000
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
```
##### 3）KafkaController 生产者，发送消息通知
##### 4）EasyworkApplication 注册消费者
```
@KafkaListener(topics = "demo") 订阅名称为demo的topic，接收消息并处理
```
#### 五、Redis的基本使用
#### 六、MongoDB的基本使用
#### 七、ElasticSearch的基本使用




