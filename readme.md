#目标
DeviceConnect是手机控制ARM板子上的外设的最简单实例，目标是让使用者能更方便的扩张。功能列表如下：
+微信订阅号点亮ARM板子上的LED灯；
+Android App点亮ARM板子上的LED灯；
+iOS App点亮ARM板子上的LED灯。

##硬件环境
	云服务器：腾讯云服务器，它有免费1个月。
	ARM开发板：广州致远电子的EPC-6G2C，它有8路串口。
	手机：安卓、iPhone手机都可以。
##源码结构
下载源码后，它的目录结构如下所示：
DeviceInternet
├── android
├── apphtml
├── device
├── ios
└── readme.md
##编译
android目录，Android APP程序源码，用Android Studio打开和编译后在安卓手机上运行。
apphtml目录，云服务器的程序源码和微信公众号页面，用Intellij IDEA打开和编译后在云服务器上运行。
device目录，ARM工控板的程序源码，C语言程序，gcc交叉编译器编译后在ARM板子上运行。
ios目录，iPhone APP程序源码，用Xcode打开和编译后在iPhone手机上面运行即可。
##手机和云服务器的交互接口
手机和云服务器采取htpp协议api接口，返回的值是json数据。有两类接口：设置和查询。
###设置接口
下面是手机访问云服务器的接口，这里的active是put，代表设置。服务器端是一个字典，这里Key是led，value是off.
http://127.0.0.1:8080/api/a7/control?active=put&key=led&value=off
返回值是json数据，如下：
{“errorCode”:”0”,”errorMsg”:”配置”+key+成功}
###查询接口
手机访问云服务器：http://127.0.0.1:8080/api/a7/control?active=get&key=led
返回json数据：{“errorCode”:”0”,”value”:”off”,”key”:”led”,”errorMsg”:”查询+key+成功”}
##云服务器的申请和程序的部署
腾讯云服务器（需自己申请） ip:123.207.39.45 user:root 密码：常用密码
云服务器上面安装的centos操作系统，需安装java环境。
apphtml工程是基于gradle的项目，所以把apphtml部署到服务器参考gradle项目部署即可，很简单。
##微信订阅号的说明
微信订阅号其实仅仅能够返回word文档、音频、文字，很死板不能有逻辑。
为了实现订阅号控制灯泡，我们做了一个网页，通过订阅号的菜单进入网页，进而控制灯泡。
###域名的申请
从订阅号跳到网页，如果是IP地址那么会有警告弹出，域名则没有这个问题。
申请域名需要审核手续，时间和过程都比较麻烦，这里在花生壳申请一个二级域名。
https://buy.oray.com/domain/free.html?domain[]=zlgmcu.iask.in 域名：yfm1202.6655.la 用户名：yfm1202 密码：常用短密码
##其他
无

