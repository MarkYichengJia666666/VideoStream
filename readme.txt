1、 Eclipse  Import Test_DPSDK_Java工程文件夹后，直接运行会报错：java.lang.UnsatisfiedLinkError: no dslalien in java.library.path

	解决办法：请参见manual中《常见问题解答》文档中关于Test_DPSDK_Java问题汇总。

2、 此java demo依赖的C++dll库是32位的。某些64位操作系统的电脑上无法加载32位的库。
	解决办法：链接：http://pan.baidu.com/s/1c5h5Ki 密码：i6r5  此链接的云盘中有64位的C++封装的dll库。java demo中提供了C++库封装成jni的工程（32位dll库封装工程），客户可以仿照此工程封装64位的dll。
