Heading OAth2 Server
---------------------------------------

### 授权服务器

　　系统启动后会根据配置文件初始化admin用户，使用admin用户登录系统，在后台维护产品信息即需要授权访问的应用，新建产品信息后需要将应用所有对外暴露的接口地址及接口名称添加到对应的产品中，接口地址将作为后续权限校验的凭证。没有被添加的接口一般情况下不会被授权调用，可在资源提供方即提供接口调用的应用通过配置filter来配置。

　　关于接口地址，统一使用相对于应用contextPath的uri，即不包含contextPath。如接口调用地址为`http://127.0.0.1:8080/contextPath/api/foo`，则接口地址为/api/foo。

　　对于具有PathVariable的地址，如/api/foo/{id}/update，推荐尽量避免或改写成/api/foo/update/{id}，改写后接口地址填/api/foo/update即可，如不改写按原来的方式来写，则接口地址填/api/foo可授权该接口的调用，但是同时也将授权如/api/foo/{id}/delete或/api/foo/list等接口的调用。

　　对于具有QueryParam的地址，则接口地址填入?前的uri即可,即不包含QueryString字符串。

### 资源提供方

　　即提供接口调用的应用，需做以下依赖
> 
```xml
<dependency>
  <groupId>com.hd123.oauth2</groupId>
  <artifactId>oauth2-provider</artifactId>
  <version>0.3.0-SNAPSHOT</version>
</dependency>
```

　　该jar提供了一个OAuth2ValidateFilter，应用方配置该filter即可实现资源的保护，原理即拦截请求的uri地址并校验是否携带access_token，将uri和access_token作为参数传到授权服务器去做校验。该filter提供了三个init参数，其中oauth2ServerUrl是必须的参数，即授权服务器地址，只到端口号即可或是域名，支持http和https。realm为可选参数，用来标识当前应用所在域，如HDCard。excludeUris也为可选参数，表示不需要授权也能调用的接口的uri，多个以英文逗号分割，如： /api/login,/api/index,/api/register 。

　　当然，也可以不依赖此jar，resource server方可按照类似逻辑实现并添加自己的filter。java之外的其他语言可自行添加http中间件。

　　Spring4.0或以上版本可用java config方式添加该filter，servlet3.0以上版本可继承该filter并使用javax.servlet.annotation.WebFilter注解添加filter，安全性较将filter暴露在web.xml要高。

### 申请接口调用方

#### 1、注册用户

　　到授权系统注册新用户。

#### 2、注册应用

　　使用注册的用户登录系统，并注册应用，注册应用时需选择调用接口的产品和所需调用的接口。应用注册后，需要管理员用户根据请求授权的接口判断是否审核此应用，审核通过后会生成appid和appSecret，用来作为换取accessToken的凭证。

#### 3、获取accessToken (GET操作)

　　像授权服务器发送get请求，获取accessToken，必须参数为appid，app_secret，grant_type，response_type。其中appid，app_secret为注册应用后获得的凭证，grant_type参数必须为client_credentials，response_type参数必须为token。state参数为可选参数，此参数将原样返回。请求如下：
> 
```shell
　curl -XGET -i "https://headingoauth2.com/api/token?　＼
　    grant_type=client_credentials&response_type=token　＼
　    &app_id=hd9972729ca69743c09fd188042d3df5dd　＼
　    &app_secret=2a13I0jQ40cYHiKFmBvpIQ4MCHLbhZXQUNymCMNOY895UWUFBHgh98W　＼
　    &state=state　　　　　　　　　　　　　　　　　　　　　　　　　　　　　
```
> 返回json格式如下：
```json
  {
    "state" : "state",
    "access_token" :"accessToken",
    "expires_in" : "7200",
    "token_type" : "Bearer"
  }
```
　　其中accessToken是个很长的字符串，若要持久化存储请确保字段设为Lob类型。expires_in为有效时间，单位为秒。

#### 4、使用accessToken调用需授权的接口

　　可将accessToken参数以queryString方式拼接到接口地址的url中（使用?或&拼接url）,由于accessToken较长所以不推荐此方式。推荐将accessToken添加到请求的header中，header要按如下格式添加：

　　Authorization: Bearer access_token

　　header的key为Authorization，值为Bearer accessToke,其中Bearer后必须要有且只有一个英文空格。

开发&维护
-------------------------------------------------

### 1、MongoDB基础

#### 安装后的基础操作

* 配置文件在 /etc/mongodb.conf 基本配置如下：
>
> auth=true
> quiet = true
> dbpath = /var/lib/mongodb
> logpath = /var/log/mongodb/mongod.log
> logappend = true
> maxConns = 19999

* 注释掉授权，启动服务（systemctl start mongodb.service）

* 创建管理员用户:
> 进入mongodb
```shell
mongo
```
> 切换到管理员数据库
```shell
use admin
```
> 创建管理员
```shell
db.createUser(
　{
　　user:"admin",
　　pwd:"admin",
　　roles:[
　　　{
　　　　role:"readWrite", 
　　　　db:"admin"
　　　},
　　　{
　　　　role:"dbAdmin", 
　　　　db:"admin"
　　　},
　　　{
　　　　role:"userAdminAnyDatabase", 
　　　　db:"admin"
　　　}
　　]
　}
)
```

* 创建应用库的用户:

> 切换到应用数据库
```shell
use oauth2
```
> 创建应用数据库用户
```shell
db.createUser(
　{
　　user:"auth",
　　pwd:"auth",
　　roles:[
　　　{
　　　　role:"readWrite", 
　　　　db:"oauth2"
　　　},
　　　{
　　　　role:"dbOwner", 
　　　　db:"oauth2"
　　　},
　　　{
　　　　role:"userAdmin",
　　　　db:"oauth2"
　　　}
　　]
　}
)
```

* 打开授权，重启动服务（systemctl restart mongodb.service）

* 后续操作： 
> 进入mongodb
```shell
mongo
```
> 切换到应用数据库
```shell
use oauth2
```
> 授权用户
```shell
db.auth("weicard","weicard")
```
> 列出所有集合
```shell
show collections
```
> 创建集合
```shell
db.createCollection("token")
```
> 查询集合(接收json参数)
```shell
db.token.find()
```
> 删除集合
```shell
db.token.drop()
```
> 删除数据库
```shell
db.dropDatabase()
```
> 删除用户
```shell
db.system.users.remove({user:"weicard"})
```

#### 数据库角色
1. 数据库用户角色：read、readWrite;
2. 数据库管理角色：dbAdmin、dbOwner、userAdmin；
3. 集群管理角色：clusterAdmin、clusterManager、clusterMonitor、hostManager；
4. 备份恢复角色：backup、restore；
5. 所有数据库角色：readAnyDatabase、readWriteAnyDatabase、userAdminAnyDatabase、dbAdminAnyDatabase
6. 超级用户角色：root
7. 内部角色：__system
8. 这里还有几个角色间接或直接提供了系统超级用户的访问（dbOwner 、userAdmin、userAdminAnyDatabase）

### 2、前端依赖管理（bower）

需先安装NodeJS

* npm install -g bower
* npm install -g gulp
* bower help
* bower search
* bower install/uninstall

<p>
package.json定义构建时的依赖及版本，在同级目录执行npm install
.bowerrc文件定义bower_components目录位置
bower.json定义前端依赖及版本
在bower.json所在目录执行bower install会自动安装依赖到bower_components下 
gulpfile.js定义构建任务
</p>
## 3、生成https证书（keytool）

* 为服务器生成证书
> 
```shell
keytool -validity 1365 -genkey -v -alias jetty -keyalg RSA -keystore jetty.keystore \
    -dname "CN=172.17.2.71,OU=hd123.com,O=heading,L=sh,ST=sh,c=cn" \
    -storepass headingoauth2 -keypass headingoauth2
```

* 为客户端生成证书
> 
```shell
keytool -validity 1365 -genkeypair -v -alias client -keyalg RSA \
    -storetype PKCS12 -keystore  /home/madoka/cert/client.p12 \
    -dname "CN=client,OU=hd123.com,O=heading,L=sh,ST=sh,c=cn" \
    -storepass headingoauth2 -keypass headingoauth2
```

* 让服务器信任客户端证书
> 
```shell
keytool -export -v -alias client -keystore client.p12 \
    -storetype PKCS12 -storepass headingoauth2 -rfc -file client.cer 
```
```shell
keytool -import -v -alias client -file client.cer \
    -keystore jetty.keystore -storepass headingoauth2
```

* 让服务器信任服务端证书
> 
```shell
keytool -export -v -alias jetty -keystore jetty.keystore \
    -storepass headingoauth2 -rfc -file server.cer
```
```shell
keytool -import -v -alias jetty -file server.cer \
    -keystore client.truststore -storepass headingoauth2
```

* 查看
> 
```shell
keytool -list -keystore  jetty.keystore -storepass headingoauth2
```

docker 使用参考
-------------------------------------------------

#### build:  构建镜像
　docker build -t oauth2-server:0.6.0 .

#### run:  运行镜像
* 守护进程形式 <br/>
docker run -d -p 8180:8180 --name oa oauth2-server:0.6.0
* shell交互形式(本服务不支持，可运行其他镜像) <br/>
docker run -i -t --rm java:8 /bin/bash

#### 查看镜像
　docker images
#### 查看容器
　docker ps -a
#### 查看日志
　docker logs -f --tail=100 ContainerId

* -f, --follow=false        跟踪日志输出。 <br/>
* -t, --timestamps=false    显示时间戳。 <br/>
* --tail="all"              输出日志尾部特定行(默认是所有)。 <br/>
* --since=""                时间段

#### 查看磁盘挂载
　docker inspect -f {{.Volumes}} ContainerId

#### 导出日志目录
　docker cp ContainerId:/var/log/oauth2-server /local/path

#### 谨慎操作
* 删除一个镜像    	 <br/>
 docker rmi -f ImageId
* 删除所有镜像    	 <br/>
 docker rmi -f $(docker images -q)
* 删除一个容器    	 <br/>
 docker rm -v -f ContainerId
* 删除所有容器    	 <br/>
 docker rm -v -f $(docker ps -a -q)
* 删除旧的容器        <br/>
 docker ps -a | grep 'weeks ago' | awk '{print $1}' | xargs docker rm -v -f
* 删除停止状态的容器   <br/>
 docker rm -v -f $(docker ps -a -q -f status=exited)

#### 设置容器的磁盘空间（需要重启docker）
　docker -d --storage-opt dm.basesize=50G

#### 在容器中执行命令
　docker exec -i -t ContainerName myshell  <br/>

* -d, --detach=false         Detached mode: run command in the background
* -i, --interactive=false    Keep STDIN open even if not attached
* -t, --tty=false            Allocate a pseudo-TTY
* -u, --user=                Username or UID (format: <name|uid>[:<group|gid>])
> <font color=red>**如: docker exec -i -t ContainerName /bin/bash**</font> 

#### 提交容器当前状态到镜像
　docker commit [OPTIONS] CONTAINER [REPOSITORY[:TAG]]

* -a, --author=""     Author (e.g., "John Hannibal Smith `<hannibal@a-team.com>`")
* -c, --change=[]     Apply specified Dockerfile instructions while committing the image
* -m, --message=""    Commit message
* -p, --pause=true    Pause container during commit

#### 保存/备份镜像到本地
* docker save -o /local/path/fedora-latest.tar fedora:latest
* docker save fedora:latest > /local/path/fedora-latest.tar

#### 加载本地镜像文件到docker
* docker load --input fedora.tar
* docker load < fedora.tar

#### 容器与主机文件/目录传输
* docker cp [options] CONTAINER:PATH LOCALPATH
* docker cp [options] LOCALPATH CONTAINER:PATH

#### 连接到运行的容器中（谨慎使用，不推荐）
　docker attach [OPTIONS] CONTAINERID

* --no-stdin=false    Do not attach STDIN
* --sig-proxy=true    Proxy all received signals to the process

##### 退出attach
* 默认情况下，如果使用ctrl-c退出container,那么container也会stop
* 按ctrl-p ctrl-q可以退出到宿主机，而保持container仍然在运行

#### 查看容器或镜像的详细信息
　docker inspect [OPTIONS] CONTAINER|IMAGE [CONTAINER|IMAGE...]

* -f, --format=""    Format the output using the given go template
* --type=container|image  Return JSON for specified type, permissible values are "image" or "container"

#### docker run 可选参数
* -c/--cpu-shares 512 每个CPU的配额（1024允许占用全部CPU）
* --cpuset=0,1 只允许运行在前两个CPU上（--cpu 2）
* --cpu 4 使用四核（加载run命令的末尾）
* -m 128m 内存分配
[详细参考Docker容器资源管理](http://dockone.io/article/216)
* --dns 覆盖容器默认dns配置
* --mac-address 覆盖容器默认mac地址配置
* --add-host 添加hosts（如--add-host db-static:86.75.30.9）
* --security-opt 指定安全策略（主机配置）
* --privileged Docker将拥有访问主机所有设备的权限
* --device 指定容器可访问的设备（如--device=/dev/snd:/dev/snd:rwx rwx为权限）
* --rm 容器停止后删除容器(不与-d一起使用)  <br/>
* -e 设置环境变量（如-e "deep=purple"）
* -h 来设定hostname
* --link name:alias 连接其他容器
* -v $HOSTDIR:$DOCKERDIR 挂载主机目录到容器目录
* --read-only 设置容器只读

#### docker run --net 参数
* none。关闭容器内的网络连接
将网络模式设置为none时，这个容器将不允许访问任何外部router。
这个容器内部只会有一个loopback接口，而且不存在任何可以访问外部网络的router。

* bridge。通过veth接口来连接容器，默认配置。
Docker默认会将容器设置为bridge模式。此时在主机上面将会存在一个docker0的网络接口，
同时会针对容器创建一对veth接口。其中一个veth接口是在主机充当网卡桥接作用，另外一个veth接口存在于容器的命名空间中，
并且指向容器的loopback。Docker会自动给这个容器分配一个IP，并且将容器内的数据通过桥接转发到外部。

* host。允许容器使用host的网络堆栈信息。 注意：这种方式将允许容器访问host中类似D-BUS之类的系统服务，所以认为是不安全的。
当网络模式设置为host时，这个容器将完全共享host的网络堆栈。host所有的网络接口将完全对容器开放。
容器的主机名也会存在于主机的hostname中。这时，容器所有对外暴露的端口和对其它容器的连接，将完全失效。

* container。使用另外一个容器的网络堆栈信息。
当网络模式设置为Container时，这个容器将完全复用另外一个容器的网络堆栈。
同时使用时这个容器的名称必须要符合下面的格式：--net container:<name|id>.

#### 查看镜像历史
　docker history [OPTIONS] IMAGE

* -H, --human=true     Print sizes and dates in human readable format
* --no-trunc=false     Don't truncate output
* -q, --quiet=false    Only show numeric IDs

#### 导出镜像
　docker export [OPTIONS] CONTAINER

* docker export exampleimage > exampleimage.tar
* docker export --output="exampleimage.tar" exampleimage

#### 导入镜像
　docker import URL|- [REPOSITORY[:TAG]]

* docker import `http://example.com/exampleimage.tgz`
* cat exampleimage.tgz | docker import - exampleimagelocal:new

#### 其他
* docker tag IMAGE[:TAG] [REGISTRYHOST/][USERNAME/]NAME[:TAG]
* docker stop container     --stops it.
* docker start container    --will start it again.
* docker restart container  --restarts a container.
* docker kill container     --sends a SIGKILL to a container.
* docker wait container     --blocks until container stops.
* docker port container     --shows public facing port of container.
* docker top container      --shows running processes in container.
* docker stats container    --shows containers' resource usage statistics.
* docker diff container     --shows changed files in the container's FS.
* docker events             --gets events from container.
* docker login              --to login to a registry.
* docker search             --searches registry for image.
* docker pull pulls an image from registry to local machine.
* docker push               --pushes an image to the registry from local machine.

### Docker 1.9 Feature
##### Volume with data container

If you're more comfortable with mounting data to a data container, the commands you execute at the first time will look like as follows:

```shell
# Create data container
docker run --name=container-data --entrypoint /bin/true image
# Use `docker run` for the first time.
docker run --name=container-name --volumes-from container-data image
```
##### Using Docker 1.9 Volume command

```shell
# Create docker volume.
$ docker volume create --name container-data

# Use `docker run` for the first time.
$ docker run --name=container-name -v container-data:/data image
```
