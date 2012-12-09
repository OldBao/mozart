owl-s api to service composition

1. 如果出现CastException
这个一般是组合服务时，fromProcess这个元素的源找不到，检查一下源process的文件，看看namespace和name有没有配置正确
2. 如果是使用owl-s editor早期版本生成的描述文件，是基于1.1版本的，在组合服务时候目前主要发现以下两个问题
    theParam替换成theVar
    TheParentPerform替换成ThisPerform
