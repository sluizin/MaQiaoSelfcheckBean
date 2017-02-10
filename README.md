# MaQiaoSelfcheckBean
* 对pojo对象进行自检，不使用注解
* 接口 maQiao.maQiaoSelfcheckBean.InterfaceSC
* 因为使用了Lambda，所以只支持jdk1.8<br/>
* 允许重写接口方法:public boolean Selfcheck(T value){}