# UIKit [![](https://jitpack.io/v/kelinZhou/UIKit.svg)](https://jitpack.io/#kelinZhou/UIKit)

架构中的UIKit。用于快速搭建App架构。

## 核心组件
### Navigation
Navigation是UIKit的通用页面容器，因为UIKit的原则是所有页面尽量使用Fragment实现，而Navigation就是众多Fragment的容器。

首先需要在AndroidManifest.xml文件中添加：
```xml
<application...>
    <activity android:name="com.kelin.uikit.common.Navigation" />
</application>
```
#### 启动一个Fragment页面，需要调用launch方法。
```kotlin
Navigation.launch<PlaceholderFragment>(context, "新的页面")
```
启动一个页面并传参：
```kotlin
Navigation.launch<PlaceholderFragment>(context, "新的页面") {
    PlaceholderFragment.setName(intent, "新的页面")  //为Fragment传参。
}
```
启动一个页面并对页面进行配置：
```kotlin
Navigation.launch<PlaceholderFragment>(context, "新的页面") {
    //immersion()  //设置沉浸式页面并隐藏Toolbar。
    //options(bundle) //为Navigation设置启动参数。
    //options { //使用闭包的方式为Navigation设置启动参数。
    //    putString("testKey", "testValue")
    //}
    immersionToolbar()  //设置沉浸式Toolbar。
    navigationIcon(R.drawable.ic_navigation_back) //为Toolbar设置返回按钮图标

    //监听返回结果
    result<String>{ data ->
        if (data != null) {
            Toast.makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()
        }
    }
}
```

#### 启动一个TabLayout+ViewPager的页面需要调用launchTab方法。
```kotlin
Navigation.launchTabOnly(context) {
    "生活" to PlaceholderFragment::class
    "汽车" to PlaceholderFragment.createInstance("汽车")
    "圈子" to PlaceholderFragment.createInstance("圈子")
}
```
![TabLayout+ViewPager](ReadmeRes/Tab_ViewPager1.png)

启动一个沉浸式状态栏的TabLayout+ViewPager页面。
```kotlin
Navigation.launchTabOnly(context, immersion = true) {
    "汽车" to PlaceholderFragment.createInstance("汽车")
    "生活" to PlaceholderFragment::class
    "圈子" to PlaceholderFragment.createInstance("圈子")
}
```
禁用ViewPager的滑动翻页
```kotlin
Navigation.launchTabOnly(context, scrollEnable = false) {
    "生活" to PlaceholderFragment::class
    "汽车" to PlaceholderFragment.createInstance("汽车")
    "圈子" to PlaceholderFragment.createInstance("圈子")
}
```
也可以在启动页面时对Navigation进行配置。
```kotlin
Navigation.launchTab(context){
    //immersion()  //设置沉浸式页面并隐藏Toolbar。
    //options(bundle) //为Navigation设置启动参数。
    //options { //使用闭包的方式为Navigation设置启动参数。
    //    putString("testKey", "testValue")
    //}
    //defaultIndex(1) //设置默认打开的页面索引。
    immersionToolbar()  //设置沉浸式Toolbar。
    navigationIcon(R.drawable.ic_navigation_back) //为Toolbar设置返回按钮图标
    configurePage {
        "汽车" to PlaceholderFragment.createInstance("汽车")
        "生活" to PlaceholderFragment::class
        "圈子" to PlaceholderFragment.createInstance("圈子")
    }
    //监听返回结果。
    resultForCode { resultCode ->
        Toast.makeText(this@MainActivity, "操作成功", Toast.LENGTH_SHORT).show()
    }
}
```
#### 启动一个搜索样式的页面需要调用launchSearch方法。
启动一个搜索页面
```kotlin
Navigation.launchSearch<TestSearchFragment>(context)  //TestSearchFragment为搜索结果显示页面。
```
启动一个搜索页面并配置
```kotlin
Navigation.launchSearch<TestSearchFragment>(context){
    immersion() //开启沉浸式样式
    //initialSearch(true)  //页面拉起后直接执行一次搜索
    //instantSearch(true)  //用户输入字符后自动触发搜索
    historyPage(TestHistoryFragment::class.java)
}
```
启动一个搜索页面并希望得到返回结果
````kotlin
Navigation.launchSearch<TestSearchFragment>(context){
    results<Persion> { data -> 
        //do something with data
    }
}
````

#### 启动一个H5页面
```kotlin
Navigation.launchH5(context, "https://www.baidu.com", "百度")
```
启动一个H5页面并进行配置。
```kotlin
Navigation.launchH5(context, "https://www.baidu.com", "百度"){
    statusBarDark(false)  //设置状态栏颜色模式。
    closeStyle()  //设置仅支持关闭样式。按下返回键直接退出H5页面(无论已经进入多少级页面)
    byBrowser()  //通过浏览器打开网页
    cookie(cookie) //设置cookie
    setJavascriptInterface(MyJsInterface::class.java, "PaiDian") //设置Javascript接口
}
```
还可以启动H5页面运行网页代码
```kotlin
Navigation.launchH5ByData(context, htmlData)
```
### 注意：
**启动页面后如果需要得到页面的返回数据都需要在调用optional函数中调用`results<Data>{}`方法或`resultsForCode<Data>{}`方法。还需要在需要返回结果的Fragment中调用`OkActivityResult.setResultData(requireActivity(), data)`方法设置返回结果。**