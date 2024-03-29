# 对高风险操作的图灵测试需求

登录页或其他涉及较高风险的操作页都会有要求检查来访者是否是一个真实的人以及基于他的ip或其它一些浏览器能够获知的信息综合判断风控的需求，这时源站就会弹出多种多样的验证码来验证来访者是否是个活体，即`captcha`

captcha要求来访者按照指引机型一系列操作，比如算一道题、比如找图片中的东西等等，这种检测对方是人还是机器的方法被称为`图灵测试`

```
将人与机器隔开，前者通过一些装置（如键盘）向后者随意提问
多次问答后，如果有超过30%的人不能确定出被测试者是人还是机器，那么这台机器就通过了测试，并被认为具有人类智能
by 艾伦·麦席森·图灵
```

这种测试是非常有必要的

# 验证码

在远古时代，认证服务器都会提供一种常见的图灵测试手段，即要求输入验证码，它由认证服务器的后台生成一张包含了数字和字母(
或中文)的图片，要求登录者进行输入

随着时代的发展，验证码的方式也在进化，从图片变成了短信，人脸识别等等，其提供服务的范畴也不再是仅仅登录页或修改页，而在你支付、转账等等各种操作时都会出现

# 验证码服务

在今天，验证码已经规模化的形成独立的服务群体，由服务提供方(SP)提供全套的前端介入和后台验证组件

比如google提供recaptcha服务，其流程就是

* 前端引入google的验证js插件
* 在提交按钮或其它需要进行验证的地方按照插件的编程要求首先激活验证服务的代码，如下所示

```html

<button class="g-recaptcha"
        data-sitekey="google应用密钥"
        data-callback='onSubmit'
        data-action='submit'>Submit
</button>
<script>
    function onSubmit(token) {
        //这里其实是google的插件回调的
        document.getElementById("demo-form").submit();
    }
</script>
```

* 点击提交前，google先会和自己的后台交互给出一个token作为一个额外参数
* 浏览器向后台提交时需要带着这个额外的参数，后台再用这个参数和google进行校验从而得知当前的用户访问的风险如何

# SaaS化服务

一般的云厂商都会提供在线的验证码服务，比如网易盾易，腾讯云验证码，京东云验证码，流程都差不多，这些验证码服务的问题是

* 必须支持公网访问
* 要钱

# 私有化服务(<font color=orange>计划内</font>)

大部分客户实际上要求在自己的机房部署本地化的验证码服务，不过这样也有问题

* 有关风控的数据需要重新建设
* 面对爆发流量难以支撑

# 总结

AAA服务不自带验证码服务，研发在计划内

