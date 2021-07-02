[INFO] Setting default groupId: com.dobbinarchetype
[INFO] Setting default artifactId: demo
[INFO] Setting default version: 0.0.1-SNAPSHOT
[INFO] Setting default package: com

现在还没解决的问题是，package只有com

临时解决方式是，archetype:create-from-project 时不要clean。然后第二次构建时，会选择

[INFO] Setting default groupId: com.dobbinarchetype
[INFO] Setting default artifactId: demo
[INFO] Setting default version: 0.0.1-SNAPSHOT
[INFO] Setting default package: com.dobbinarchetype

然后手动将generated-source不要的包删掉，还有Test

然后提交maven就行了


// 上诉问题已经解决，原来Test里面的也要算，框架会自动取所有源码前面公共的部分。


1. 第一步在根目录执行，创建原型代码


    mvn archetype:create-from-project
    

2. 根目录会出现target文件夹，找到里面的 generated-sources 这是原型文件，讲其推到远程仓库

    
    cd target/generated-sources/archetype/
    # 可以本地从原型构建
    mvn install
    # 可以从远程仓库构建 
    mvn deploy
    # 但是如果是用的阿里云 云效的制品仓库的话，可能需要这样
    mvn clean install org.apache.maven.plugins:maven-deploy-plugin:2.8:deploy -DskipTests
    
