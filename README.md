#db-migration

##工具说明
db-migration是一个数据库表备份的工具。由于使用create table select备份的方式会导致表中原有的索引丢失，如果紧急情况下需要使用备份表，会造成索引无法使用的情况，可能带来灾难性的后果。db-migration采用多线程方式进行分段数据的拷贝插入，效率会更高。

##配置说明
配置项在`conf/config.properties`中，主要有以下配置项
* 数据库配置
    *  `jdbc.url`:数据库连接串配置
    *  `jdbc.username`:数据库用户名
    *  `jdbc.password`:数据库密码
    *  `jdbc.driver`:无特殊情况不需要修改,默认使用mariadb的驱动
* `primary.key.name`:表主键名称，大部分的表都定义为id,根据实际情况修改
* 线程配置
    *  `thread.count`:同时迁移的线程个数
    *  `thread.sleep.time`:线程每次执行完一个批次数据后休眠的时间
* `fetch.data.everytime`:线程每次要抓取同步的记录数
* `migration.tables`:要备份的表,格式为`源表名称:备份表名称,源表名称:备份表名称`,其中源表和备份表中间用冒号隔开,如果有多张要处理的表以逗号隔开

##运行说明
* 启动脚本:启动脚本`bin/start.sh`,运行时只需要`./bin/start.sh`即可
* 日志说明:日志位于logs目录下,名称为`run.log`
    *  线程每次开始迁移之前会打印当前的startId和endId,如`will execute at startId=14480, endId=14597`
    *  程序每隔10s就会打印迁移情况,如`src table count: 10548, dest table count: 10548`

##其他说明
如果需要备份不同库的表,需要拷贝一份程序,另起一个线程.