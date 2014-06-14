jtools java常用工具集
===================================

设计目标
-----------------------------------
设计目标是成为一个可扩展的不依赖现有系统，能直接使用的第三方工具集，并可配合系统命令，如dos/linux命令，执行相关命令或任务，如数据库查询，或数据库数据导出。




 支持本地模式，即直接在命令行运行
----------------------------------- 

### 参数模式，一个任务执行完就结束
### 命令行模式，会等待用户输入命令，并响应用户操作
### 打包后的运行包，见docs/bin/jtool.zip
### 运行包的使用，解压后配置环境变量，然后运行install.bat，就可以在任意地方执行jtool启动运行




 支持web模式，即成为web项目的一个库来用
----------------------------------- 

### 本地模式下的各项功能，web模式也都支持
### 链接
[jtoolweb](http://jtoolweb.jd-app.com/)<br />





tools功能
-----------------------------------
### md5, value,用MD5加密
### b64, value,用Base64加密
### b64d, value,用Base64解密
### lower, value,转小写
### upper, value,转大写
### zip, file
### unzip, file
### cat, cat [*.*]
### cd, cd directory
### cmd, cmd (dos command)
### constClass, [-f path] [-xml/-prop/-json] genFilePath packageName [className],生成constant静态常量类
### cp, cp source dest 文件复制
### date, [-f file] date1-date2[11-01/2011-00-00/now]日期计算
### dir, dir [*.*] [-time/-date/-datetime/-length] (.当前目录)
### export, [-code code] [-name name] [-lang cn/en] [-xlt xltFileName] [-out outFileName] select [column1,column2] from tableName
### fileModifyTime,  fileName [-datetime/-date/-time] [2011-01-01 00:00:00/2011-01-01/00:00:00]修改文件修改时间
### ftp, [-f] (path/file) login host user password port/dir path/download sourceFile destPath,模拟登入
### mv, mv source dest
### pwd, pwd,显示当前路径!
### rm, rm [*.*]
### sort, [-f] file/ [v1,v2,v3,v4],排序,支持字符排序，纯数字排序
### time, [-h/-hh/-m/-mm] time1-time2[00:00/00:00:00/now]时间计算
### trim, [-f] (path/file)/value,去空格或空行
### web, [login url 模拟登入] [-cookie] [-f file] url访问网站


dbs功能
-----------------------------------
### attr, tableName&sql将表字段转成java类属性
### col, tableName&sql显示表字段
### constClass, packageName [className] select key, value, [default_value] from tableName&sql 数据生成constant静态常量类
### data, tableName&sql显示表数据
### desc, tableName显示表结构
### enum, tableName&sql 数据生成enum对象
### fdata, tableName&sql显示表数据(form)
### insert, tableName成生insert sql语句
### json, tableName&sql 数据生成json对象
### select, tableName&sql生成查询语句(字段别名为类属性)
### show, 显示所有的表名
### sql, sql执行sql语句[select|update|delete]
### update, tableName&key,key1,!key2成生update sql语句
### xml, tableName&sql 数据生成xml格式数据

## 注dbs 支持结果查询,具体建单元测试代码




