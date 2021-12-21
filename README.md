## Dobbin Framework Logo

#### 一、项目背景 

> > 为了快速落地项目、快速搭建脚手架，dobbinsoft开发一套基于SpringBoot MyBatis的框架，并手搓了如参数校验、文档生成、限流、鉴权等等常用功能。此项目是利用maven原型构建快速创建项目。

#### 二、快速开始

##### 2.1. 下载代码

您可以在国内开源社区Gitee下载（推荐）：https://gitee.com/iotechn/dobbinfw

您可以在国际开源社区Github下载：https://github.com/iotechn/dobbinfw

##### 2.2. maven原型构建

###### 2.2.1. 安装core、support、launcher包
请参考[core](../../../dobbinfw-core)、[support](../../../dobbinfw-support)、[launcher](../../../dobbinfw-launcher)

###### 2.2.2. 构建

请确定您已经将 JAVA_HOME 配置，并将mvn命令配置到PATH中，若出现找不到命令，或找不到JAVA_HOME，[请参考此文档](https://blog.csdn.net/weixin_44548718/article/details/108635409)


1. 第一步在根目录执行，创建原型代码


```shell
mvn archetype:create-from-project
```


2. 根目录会出现target文件夹，找到里面的 generated-sources 这是原型文件，讲其推到远程仓库
   
```shell
cd target/generated-sources/archetype/
mvn install
```

3. 发布到私库

```shell
mvn deploy
// 但是如果是用的阿里云 云效的制品仓库的话，可能需要这样
mvn clean install org.apache.maven.plugins:maven-deploy-plugin:2.8:deploy -DskipTests
```

##### 2.3. 使用IDEA原型构建

选择New Project，并输入以下坐标

```xml
<groupId>com.dobbinarchetype</groupId>
<artifactId>demo-archetype</artifactId>
<version>0.0.1-SNAPSHOT</version>
```

![](https://img.dobbinsoft.com/doc/archetype/idea.png)

#### 三、模块介绍

| 模块           | 介绍                                           |
| -------------- | ---------------------------------------------- |
| demo-admin-api | 为管理员后台提供的API                          |
| demo-app-api   | 为用户APP、小程序、PC等提供API                 |
| demo-biz       | 抽取admin、app所需要公用的服务方法             |
| demo-data      | 提供数据库访问层能力、定义领域模型、传输模型等 |
| demo-runner    | SpringBoot 启动，打包可执行jar                 |

#### 四、依赖介绍详细文档⭐

| 依赖 | 解释 |
| ---- | ---- |
|   [core](../../../dobbinfw-core)   | 核心包 |
|   [support](../../../dobbinfw-support)   | 支持包，非常重要的文档 |
|   [launcher](../../../dobbinfw-launcher)   | 启动器，非常重要的文档 |

#### 五、贡献 & 社区

若Launcher包不能满足您的业务需求，您可以直接在仓库中发布Pull Request。本项目欢迎所有开发者一起维护，并永久开源。

