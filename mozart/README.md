owl-s api to service composition

1. 如果出现CastException
这个一般是组合服务时，fromProcess这个元素的源找不到，检查一下源process的文件，看看namespace和name有没有配置正确
