SQLLOAD思路。

1. 拿到原始日志，首先做预处理。
2. 预处理后的文件，做批量入库。

可定制的位置:
1. 文件预处理方法
2. 文件入库方法
3. 定制: INSERT SQL

公用的位置:
1. Job定时机制，废除crontab机制。
2. 获取连接的位置。 

预处理接口:
IPreparedProcess.java
    public void setTemp();
	public void execute();
	public 

入库接口:
ILoad.java
