# UIKit
架构中的UIKit。用于快速搭建App架构。

## 核心组件
### CommonActivity
CommonActivity是UIKit的通用页面容器，因为UIKit的原则是所有页面尽量使用Fragment实现，而CommonActivity就是众多Fragment的容器。

首先需要在AndroidManifest.xml文件中添加：
```xml
<application...>
    <activity android:name="com.kelin.uikit.common.CommonActivity" />
</application>
```
#### 启动一个Fragment页面，需要调用launch方法。
```kotlin
CommonActivity.launch<PlaceholderFragment>(this, "新的页面")
```
启动一个页面并传参：
```kotlin
CommonActivity.launch<PlaceholderFragment>(this, "新的页面") {
    PlaceholderFragment.setName(this, "新的页面")  //为Fragment传参。
}
```
启动一个页面并对页面进行配置：
```kotlin
CommonActivity.launch<PlaceholderFragment>(this, "新的页面") {
    //immersion()  //设置沉浸式页面并隐藏Toolbar。
    //options(bundle) //为CommonActivity设置启动参数。
    //options { //使用闭包的方式为CommonActivity设置启动参数。
    //    putString("testKey", "testValue")
    //}
    immersionToolbar(R.drawable.ic_navigation_back)  //设置沉浸式Toolbar并未Toolbar设置navigationIcon。
}
```
启动一个页面并希望得到返回结果：
```kotlin
CommonActivity.launchByOption<PlaceholderFragment>(this) {
    start<String>{ code, data ->
        if (data != null) {
            Toast.makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()
        }
    }
}
```
#### 启动一个TabLayout+ViewPager的页面需要调用launchTab方法。
```kotlin
CommonActivity.launchTabOnly(this) {
    "生活" to PlaceholderFragment::class
    "汽车" to PlaceholderFragment.createInstance("汽车")
    "圈子" to PlaceholderFragment.createInstance("圈子")
}
```
![TabLayout+ViewPager](ReadmeRes/Tab_ViewPager1.png)

启动一个沉浸式状态栏的TabLayout+ViewPager页面。
```kotlin
CommonActivity.launchTabOnly(this, immersion = true) {
    "汽车" to PlaceholderFragment.createInstance("汽车")
    "生活" to PlaceholderFragment::class
    "圈子" to PlaceholderFragment.createInstance("圈子")
}
```
禁用ViewPager的滑动翻页
```kotlin
CommonActivity.launchTabOnly(this, scrollEnable = false) {
    "生活" to PlaceholderFragment::class
    "汽车" to PlaceholderFragment.createInstance("汽车")
    "圈子" to PlaceholderFragment.createInstance("圈子")
}
```
也可以在启动页面时对CommonActivity进行配置。
```kotlin
CommonActivity.launchTabByOption(this){
    //immersion()  //设置沉浸式页面并隐藏Toolbar。
    //options(bundle) //为CommonActivity设置启动参数。
    //options { //使用闭包的方式为CommonActivity设置启动参数。
    //    putString("testKey", "testValue")
    //}
    immersionToolbar(R.drawable.ic_navigation_back)  //设置沉浸式Toolbar并未Toolbar设置navigationIcon。
    configurePage {
        "汽车" to PlaceholderFragment.createInstance("汽车")
        "生活" to PlaceholderFragment::class
        "圈子" to PlaceholderFragment.createInstance("圈子")
    }
    start { resultCode ->
        Toast.makeText(this@MainActivity, "操作成功", Toast.LENGTH_SHORT).show()
    }
}
```
### 注意：
**启动页面后如果需要得到页面的返回数据都需要调用`launch***ByOption`方法，并在调用start方法是传入`onResult`回调。还需要在需要返回结果的Fragment中调用`OkActivityResult.setResultData(requireActivity())`方法设置返回结果。**