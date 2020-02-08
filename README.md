# transaction-core 分布式事务框架

- 本框架适用于spring boot+RestTemplate

## 术语表
### GTM
GlobalTransactionManager（全局事务管理器）
### Master
主人，全局事务发起者，负责发起全局事务，管理直接下属slave，进行事务决策。
### Slave
随从，全局事务跟从者，响应上级slave或者master的决策。传达决策给直接下属slave。

## 框架介绍
本框架基于TCC模式，通过GTM进行事务管理。事务发起者作为master发起全局事务。事务中的远程调用服务端作为salve对事务做出响应。存在多级调用的情况下，TMG只管理直接下级，形成逐级管理的结构，如下图所示：
![组织图](https://img-blog.csdnimg.cn/20200209012200265.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80MTAyOTIzMw==,size_16,color_FFFFFF,t_70)
以上图存在两层调用为例进行说明，master将决策下达给一级slave，一级slave收到决策后执行决策，并将决策传给下属的的二级slave。二级slave执行决策并反馈响应给一级slave，一级slave将响应反馈给master，则整个决策执行完毕。


## 两阶段提交模型

- 一阶段prepare行为：
    - master：创建全局事务，全局事务管理器开启本地事务。如果存在远程调用，则全局事务管理器创建分支事务，记录远程调用地址。将分支事务id添加到请求header中发给slave。
    - slave：创建分支事务，全局事务管理器开启本地事务。如果存在远程调用，则全局事务管理器创建分支事务，记录远程调用地址。将分支事务id添加到请求header中发给slave。

- 二阶段commit 行为：
    - master：本地事务提交，调用slave提交接口。
    - slave：本地事务提交，调用下级slave提交接口。
  
- 二阶段rollback行为：
    - master：本地事务回滚，调用slave回滚接口。
    - slave：本地事务回滚，调用下级slave回滚接口。
    
## demo
[global-transaction-demo](https://github.com/xuyiyun0929/global-transaction-demo)