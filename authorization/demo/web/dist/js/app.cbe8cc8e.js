(function(e){function t(t){for(var o,s,i=t[0],c=t[1],l=t[2],d=0,p=[];d<i.length;d++)s=i[d],Object.prototype.hasOwnProperty.call(a,s)&&a[s]&&p.push(a[s][0]),a[s]=0;for(o in c)Object.prototype.hasOwnProperty.call(c,o)&&(e[o]=c[o]);u&&u(t);while(p.length)p.shift()();return r.push.apply(r,l||[]),n()}function n(){for(var e,t=0;t<r.length;t++){for(var n=r[t],o=!0,i=1;i<n.length;i++){var c=n[i];0!==a[c]&&(o=!1)}o&&(r.splice(t--,1),e=s(s.s=n[0]))}return e}var o={},a={app:0},r=[];function s(t){if(o[t])return o[t].exports;var n=o[t]={i:t,l:!1,exports:{}};return e[t].call(n.exports,n,n.exports,s),n.l=!0,n.exports}s.m=e,s.c=o,s.d=function(e,t,n){s.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:n})},s.r=function(e){"undefined"!==typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},s.t=function(e,t){if(1&t&&(e=s(e)),8&t)return e;if(4&t&&"object"===typeof e&&e&&e.__esModule)return e;var n=Object.create(null);if(s.r(n),Object.defineProperty(n,"default",{enumerable:!0,value:e}),2&t&&"string"!=typeof e)for(var o in e)s.d(n,o,function(t){return e[t]}.bind(null,o));return n},s.n=function(e){var t=e&&e.__esModule?function(){return e["default"]}:function(){return e};return s.d(t,"a",t),t},s.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},s.p="/";var i=window["webpackJsonp"]=window["webpackJsonp"]||[],c=i.push.bind(i);i.push=t,i=i.slice();for(var l=0;l<i.length;l++)t(i[l]);var u=c;r.push([0,"chunk-vendors"]),n()})({0:function(e,t,n){e.exports=n("56d7")},"034f":function(e,t,n){"use strict";n("85ec")},"03fd":function(e,t,n){},"0977":function(e,t,n){},"1b54":function(e,t,n){"use strict";n("0977")},"1e31":function(e,t,n){"use strict";n("b5d8")},"24cd":function(e,t,n){},2726:function(e,t,n){},2824:function(e,t,n){"use strict";n("e9fb")},"2d20":function(e,t,n){"use strict";n("03fd")},"3e06":function(e,t,n){"use strict";n("d6fd")},"40db":function(e,t,n){},"4a00":function(e,t,n){"use strict";n("40db")},"4ebc3":function(e,t,n){"use strict";n("5294")},5294:function(e,t,n){},"56d7":function(e,t,n){"use strict";n.r(t);n("e260"),n("e6cf"),n("cca6"),n("a79d");var o=n("2b0e"),a=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{attrs:{id:"app"}},[n("div",{staticClass:"view-container"},[n("router-view")],1),n("div",{staticClass:"background-container"},[n("AirplaneBackground")],1),n("footer",[e._v("Copyright © "),n("el-link",{attrs:{href:"mailto:Zhanghan30@jd.com"}},[e._v("Zhanghan30@jd.com")])],1)])},r=[],s=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"container"},[n("div",{staticClass:"waves"},[n("svg",{attrs:{preserveAspectRatio:"none","shape-rendering":"auto",viewBox:"0 24 150 28",xmlns:"http://www.w3.org/2000/svg","xmlns:xlink":"http://www.w3.org/1999/xlink"}},[n("defs",[n("path",{attrs:{id:"gentle-wave",d:"M-160 44c30 0 58-18 88-18s 58 18 88 18 58-18 88-18 58 18 88 18 v44h-352z"}})]),n("g",{staticClass:"parallax"},[n("use",{attrs:{fill:"rgba(255,255,255,0.7",x:"48","xlink:href":"#gentle-wave",y:"0"}}),n("use",{attrs:{fill:"rgba(255,255,255,0.5)",x:"48","xlink:href":"#gentle-wave",y:"3"}}),n("use",{attrs:{fill:"rgba(255,255,255,0.3)",x:"48","xlink:href":"#gentle-wave",y:"5"}}),n("use",{attrs:{fill:"#fff",x:"48","xlink:href":"#gentle-wave",y:"7"}})])])]),e._m(0),e._m(1)])},i=[function(){var e=this,t=e.$createElement,o=e._self._c||t;return o("div",{staticClass:"flight"},[o("img",{staticClass:"airplane",attrs:{src:n("875d")}}),o("img",{staticClass:"logo",attrs:{src:n("ba7a")}})])},function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticClass:"clouds"},[n("div",{staticClass:"cloud",staticStyle:{top:"228px"},attrs:{"data-speed":"1","data-type":"white_1"}}),n("div",{staticClass:"cloud",staticStyle:{top:"342px"},attrs:{"data-speed":"2","data-type":"white_2"}}),n("div",{staticClass:"cloud",staticStyle:{top:"451px"},attrs:{"data-speed":"3","data-type":"white_3"}}),n("div",{staticClass:"cloud",staticStyle:{top:"105px"},attrs:{"data-speed":"4","data-type":"white_4"}})])}],c={name:"AirplaneBackground"},l=c,u=(n("2d20"),n("2877")),d=Object(u["a"])(l,s,i,!1,null,"15faadb8",null),p=d.exports;document.title="认证服务器";var m={components:{AirplaneBackground:p}},f=m,h=(n("034f"),Object(u["a"])(f,a,r,!1,null,null,null)),v=h.exports,g=n("8c4f"),b=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{attrs:{id:"login-view-container"}},[n("login-form",{directives:[{name:"show",rawName:"v-show",value:null!==e.authenticated&&!e.authenticated,expression:"authenticated !== null && !authenticated"}]})],1)},w=[],_=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{attrs:{id:"login-forms"}},[n("div",{attrs:{id:"login-forms-container"}},[n("h1",{attrs:{id:"login-forms-header"}},[e._v("Camellia统一认证系统")]),n("div",{attrs:{id:"login-forms-body"}},[n("div",[n("el-row",{attrs:{gutter:20}},[e.authenticationTypes.includes("username")?n("el-tooltip",{staticClass:"item",attrs:{content:"用户名密码登录",effect:"dark",placement:"top"}},[n("el-col",{class:"username"===e.loginForm?"active-form":null,attrs:{span:24/e.authenticationTypes.length},nativeOn:{click:function(t){return e.selectUsernamePasswordForm.apply(null,arguments)}}},[n("i",{staticClass:"el-icon-lock login-form-selector"})])],1):e._e(),e.authenticationTypes.includes("sms")?n("el-tooltip",{staticClass:"item",attrs:{content:"短信验证码登录",effect:"dark",placement:"top"}},[n("el-col",{class:"sms"===e.loginForm?"active-form":null,attrs:{span:24/e.authenticationTypes.length},nativeOn:{click:function(t){return e.selectSmsForm.apply(null,arguments)}}},[n("i",{staticClass:"el-icon-message login-form-selector"})])],1):e._e(),e.authenticationTypes.includes("qrcode")?n("el-tooltip",{staticClass:"item",attrs:{content:"手机扫码登录",effect:"dark",placement:"top"}},[n("el-col",{class:"qrcode"===e.loginForm?"active-form":null,attrs:{span:24/e.authenticationTypes.length},nativeOn:{click:function(t){return e.selectQrcodeForm.apply(null,arguments)}}},[n("i",{staticClass:"el-icon-mobile-phone login-form-selector"})])],1):e._e()],1)],1),n("div",{attrs:{id:"login-forms-body-container"}},["username"===e.loginForm?n("UsernamePasswordForm"):e._e(),"sms"===e.loginForm?n("MobilePhoneSmsCodeForm"):e._e(),"qrcode"===e.loginForm?n("QrCodeForm"):e._e()],1)])])])},y=[],I=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{attrs:{id:"qrcode-line-block"}},[n("div",{attrs:{id:"qrcode-container"}},[n("div",{directives:[{name:"show",rawName:"v-show",value:e.qrCodeState===e.QrCodeState.WAIT_FOR_CONFIRMING||e.qrCodeState===e.QrCodeState.CONFIRMED,expression:"qrCodeState === QrCodeState.WAIT_FOR_CONFIRMING || qrCodeState === QrCodeState.CONFIRMED"}],attrs:{id:"qrcode-scanned-veil"}},[n("span",[e._v(e._s(e.qrCodeState===e.QrCodeState.WAIT_FOR_CONFIRMING?"已扫描":null))])]),n("div",{directives:[{name:"show",rawName:"v-show",value:e.loadState===e.LoadState.FAILED,expression:"loadState === LoadState.FAILED"}],attrs:{id:"qrcode-reload-veil"}},[n("el-tooltip",{staticClass:"item",attrs:{content:"刷新",effect:"dark",placement:"top"}},[n("i",{staticClass:"el-icon-refresh-left",on:{click:e.reloadQrCode}})])],1),n("form",{ref:"qrcodeLoginForm",attrs:{action:"/login",method:"post"}},[n("input",{attrs:{name:"type",type:"hidden",value:"qrcode"}}),n("input",{directives:[{name:"model",rawName:"v-model",value:e.token,expression:"token"}],attrs:{name:"token",type:"hidden"},domProps:{value:e.token},on:{input:function(t){t.target.composing||(e.token=t.target.value)}}})]),n("el-image",{directives:[{name:"loading",rawName:"v-loading",value:null===e.loadState,expression:"loadState === null"}],attrs:{src:e.image,alt:"二维码"}})],1)])},C=[],k=(n("d3b7"),n("bc3a")),x=n.n(k),S=n("5c96"),F=n.n(S),O=x.a.create();O.interceptors.response.use((function(e){return e}),(function(e){var t=e.response;return void 0!==t.data&&void 0!==t.data.error?401!==t.data.status&&S["Notification"].error({title:"出错啦",message:t.data.message}):(S["Notification"].error({title:"出错啦",message:e}),Ft.push({name:"error",query:{status:e.response.status,phrase:e.response.statusText}})),Promise.reject(e)}));var $=O,E=(n("d63d"),{lock:!0,text:"正在登录",spinner:"el-icon-loading",background:"rgba(0, 0, 0, 0.4)",customClass:"login-loading-veil"}),P=E,N={LOADING:"LOADING",FAILED:"FAILED",DONE:"DONE"},q={EXPIRED:"EXPIRED",WAIT_FOR_CONFIRMING:"WAIT_FOR_CONFIRMING",CONFIRMED:"CONFIRMED"},D={name:"QrCodeForm",data:function(){return{LoadState:N,QrCodeState:q,token:null,image:"",loadState:null,timerId:null,qrCodeState:null}},methods:{login:function(){this.$loading(P),this.$refs.qrcodeLoginForm.submit()},reloadQrCode:function(){var e=this;this.stopWatch(),this.image="",this.qrCodeState=null,this.loadState=N.LOADING,this.token=null,$.post("/api/qrcode").then((function(t){e.image="data:image/png;base64,"+t.data.image,e.loadState=N.DONE,e.token=t.data.token,e.watchQrCodeState(t.data.token)})).catch((function(t){501===t.response.data.status?e.$notify.error({title:"出错啦",message:t.response.data.message}):e.loadState=N.FAILED}))},watchQrCodeState:function(e){var t=this;this.timerId=setInterval((function(e){$.get("/api/qrcode/"+e).then((function(e){t.qrCodeState=e.data.state,t.qrCodeState===q.EXPIRED?t.reloadQrCode():t.qrCodeState===q.CONFIRMED&&t.login()}),(function(){t.image="",t.loadState=N.FAILED,t.qrCodeState=null,t.stopWatch()}))}),5e3,e)},stopWatch:function(){this.timerId&&(clearInterval(this.timerId),this.timerId=null)}},mounted:function(){this.reloadQrCode()}},R=D,T=(n("9558"),Object(u["a"])(R,I,C,!1,null,"73fd8553",null)),j=T.exports,L=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("el-form",{ref:"mobilePhoneForm",attrs:{action:"/login",method:"POST"}},[n("input",{attrs:{name:"authenticationType",type:"hidden",value:"sms"}}),n("el-row",[n("el-input",{attrs:{name:"mobilePhoneNumber",placeholder:"输入您的手机号","prefix-icon":"el-icon-mobile"},model:{value:e.mobilePhoneNumber,callback:function(t){e.mobilePhoneNumber=t},expression:"mobilePhoneNumber"}},[n("el-button",{attrs:{slot:"append",id:"acquire-sms-code-button",disabled:e.coolDownSeconds>0},slot:"append"},[e._v(" 获取验证码"+e._s(e.coolDownSeconds>0?"("+e.coolDownSeconds+")":null)+" ")])],1)],1),n("el-row",[n("el-input",{attrs:{name:"code",placeholder:"输入验证码"},model:{value:e.code,callback:function(t){e.code=t},expression:"code"}})],1),n("el-row",[n("el-button",{attrs:{id:"sms-longin-button",plain:"",type:"primary"},on:{click:e.login}},[e._v("登录")])],1)],1)},A=[],M={name:"MobilePhoneSmsCodeForm",data:function(){return{mobilePhoneNumber:"",code:"",coolDownSeconds:0,timerId:null}},methods:{coolDown:function(e){var t=this;this.coolDownSeconds=e,this.timerId=setInterval((function(){--t.coolDownSeconds,0===t.coolDownSeconds&&(clearInterval(t.timerId),t.timerId=null)}),1e3)},login:function(){this.$loading(P),this.$refs.mobilePhoneForm.$el.submit()}},mounted:function(){var e=this;new window["TencentCaptcha"](document.getElementById("acquire-sms-code-button"),2048219257,(function(t){console.log(t),$.post("/api/authentication/sms",{mobilePhoneNumber:e.mobilePhoneNumber,captchaToken:t.ticket}).then((function(t){e.coolDown(t.data.cooldown)})).catch((function(t){var n=t.response;console.error(n),void 0!==n.data&&void 0!==n.data.error&&429===n.data.status&&$.get("/api/authentication/sms?mobilePhoneNumber="+e.mobilePhoneNumber).then((function(t){e.coolDown(t.data.cooldown)}))}))}))}},Q=M,B=(n("2824"),Object(u["a"])(Q,L,A,!1,null,"11e5681c",null)),G=B.exports,W=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("el-form",{ref:"usernamePasswordForm",attrs:{model:e.formItems,rules:e.validation,action:"/login",method:"POST"}},[n("input",{attrs:{name:"authenticationType",type:"hidden",value:"username"}}),n("el-form-item",{attrs:{prop:"username"}},[n("el-input",{attrs:{name:"username",placeholder:"输入用户名/手机号/会员卡号","prefix-icon":"el-icon-user"},model:{value:e.formItems.username,callback:function(t){e.$set(e.formItems,"username",t)},expression:"formItems.username"}})],1),n("el-form-item",{attrs:{prop:"password"}},[n("el-input",{attrs:{name:"password",placeholder:"输入密码","prefix-icon":"el-icon-key","show-password":"",type:"password"},model:{value:e.formItems.password,callback:function(t){e.$set(e.formItems,"password",t)},expression:"formItems.password"}})],1),n("el-form-item",[n("el-button",{attrs:{id:"username-longin-button",plain:"",type:"primary"}},[e._v("登录")])],1),n("router-link",{attrs:{id:"forget-password",to:"/password/recovery"}},[e._v("忘记密码?")])],1)},V=[],H={name:"UsernamePasswordForm",data:function(){return{formItems:{username:"",password:""},validation:{username:[{required:!0,message:"请输入用户名",trigger:"blur"}],password:[{required:!0,message:"请输入密码",trigger:"blur"}]}}},methods:{login:function(){this.$loading(P),this.$refs.usernamePasswordForm.$el.submit()}},mounted:function(){var e=this;new window["TencentCaptcha"](document.getElementById("username-longin-button"),2048219257,(function(t){t.ret,e.$refs.usernamePasswordForm.validate((function(t){if(!t)return!1;e.login()}))}))}},U=H,X=(n("c631"),Object(u["a"])(U,W,V,!1,null,"426beb70",null)),z=X.exports,J={name:"LoginForm",components:{UsernamePasswordForm:z,MobilePhoneSmsCodeForm:G,QrCodeForm:j},data:function(){return{loginForm:null,authenticationTypes:[]}},methods:{selectUsernamePasswordForm:function(){this.loginForm="username"},selectSmsForm:function(){this.loginForm="sms"},selectQrcodeForm:function(){this.loginForm="qrcode"}},mounted:function(){var e=this;$.get("/api/options").then((function(t){e.authenticationTypes=t.data.options["authenticationTypeRegistry"].option.types,e.loginForm=e.authenticationTypes[0]}))}},Z=J,K=(n("b66c"),Object(u["a"])(Z,_,y,!1,null,"14b53d85",null)),Y=K.exports,ee={name:"LoginView",components:{LoginForm:Y},data:function(){return{authenticated:null}},mounted:function(){var e=this,t=this.$loading({lock:!0,text:"正在检查登录状态，清稍候...",spinner:"el-icon-loading",background:"rgba(0, 0, 0, 0.4)",customClass:"authentication-check"});$.get("/api/me").then((function(){e.authenticated=!0,t.close(),Ft.push("/welcome")})).catch((function(){e.authenticated=!1,t.close()}))}},te=ee,ne=(n("4ebc3"),Object(u["a"])(te,b,w,!1,null,"2facec45",null)),oe=ne.exports,ae=function(){var e=this,t=e.$createElement;e._self._c;return e._m(0)},re=[function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{attrs:{id:"authenticated-welcome-container"}},[n("h1",{attrs:{id:"authenticated-welcome-text"}},[e._v("您已成功登录，可直接访问业务系统")])])}],se={name:"WelcomeView",mounted:function(){$.get("/api/me").catch((function(){Ft.push("/")}))}},ie=se,ce=(n("1e31"),Object(u["a"])(ie,ae,re,!1,null,"36e78d24",null)),le=ce.exports,ue=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticStyle:{"text-align":"center"},attrs:{id:"error-view-container"}},[n("div",[n("h1",{attrs:{id:"auto-refresh-count-down"}},[e._v(e._s(e.countDown))]),n("h1",{attrs:{id:"error-status"}},[e._v("Http "+e._s(e.status)+" "+e._s(e.phrase))]),n("h2",{attrs:{id:"error-sub-title-text"}},[e._v(" "+e._s(void 0!==e.$route.params.pathMatch?"页面不存在":void 0===e.$route.query.message?"系统似乎遇到了一些问题":e.$route.query.message)+" "),n("router-link",{attrs:{to:"/"}},[n("el-tooltip",{staticClass:"item",attrs:{content:"返回首页",effect:"dark",placement:"top"}},[n("i",{staticClass:"el-icon-refresh-left"})])],1)],1)])])},de=[],pe={name:"ErrorView",data:function(){return{countDown:null,timerId:null,status:null,phrase:null,userSideError:!1}},methods:{refresh:function(){Ft.push("/")},start:function(){var e=this;this.countDown=10,this.timerId=setInterval((function(){0===--e.countDown&&e.refresh()}),1e3)},stop:function(){this.timerId&&(clearInterval(this.timerId),this.timerId=null)}},mounted:function(){void 0!==this.$route.params.pathMatch?(this.status=404,this.phrase="Not Found"):(this.status=this.$route.query.status,this.phrase=this.$route.query.phrase),this.stop(),this.start()}},me=pe,fe=(n("3e06"),Object(u["a"])(me,ue,de,!1,null,"daa8b61a",null)),he=fe.exports,ve=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{attrs:{id:"qrcode-landing-container"}},[n("div",[n("i",{staticClass:"el-icon-monitor"}),n("div",{attrs:{id:"qrcode-landing-text"}},[e._v("扫码成功，请确认是您本人操作")]),n("el-button",{directives:[{name:"show",rawName:"v-show",value:e.showConfirmButton&&!e.confirmed,expression:"showConfirmButton && !confirmed"}],attrs:{id:"qrcode-landing-confirm-button",type:"success"},on:{click:e.confirmLogin}},[e._v("确认登录 ")]),n("div",{directives:[{name:"show",rawName:"v-show",value:e.confirmed,expression:"confirmed"}],attrs:{id:"qrcode-landing-success-text"}},[e._v("确认完成")]),n("div",{directives:[{name:"show",rawName:"v-show",value:e.errorHappened,expression:"errorHappened"}],attrs:{id:"qrcode-landing-error-text"}},[e._v(" "+e._s(null===e.errorMessage?"后台出错了！":e.errorMessage)+" ")])],1)])},ge=[],be={name:"QrCodeAuthenticationLandingView",data:function(){return{showConfirmButton:!1,confirmed:!1,errorHappened:!1,errorMessage:null}},methods:{confirmLogin:function(){var e=this;x.a.post("/api/qrcode/"+this.$route.query.token+":confirm").then((function(){e.confirmed=!0}),(function(t){void 0!==t.response.data.error&&(e.errorMessage=t.response.data.message),e.errorHappened=!0}))}},mounted:function(){var e=this;x.a.post("/api/qrcode/"+this.$route.query.token+":scan").then((function(){e.showConfirmButton=!0}),(function(t){void 0!==t.response.data.error&&(e.errorMessage=t.response.data.message),e.errorHappened=!0}))}},we=be,_e=(n("f946"),Object(u["a"])(we,ve,ge,!1,null,"e14d994c",null)),ye=_e.exports,Ie=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{attrs:{id:"mfa-view-container"}},[n("div",[n("h1",[e._v("检测到您的登录环境似乎有些问题，需要进行mfa多因子验证")]),"sms"===e.$route.query.authenticator?n("sms-authenticator"):e._e()],1)])},Ce=[],ke=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",[n("el-row",[n("el-col",{attrs:{span:6}},[e._v(e._s(" 1 "))]),n("el-col",{attrs:{span:12}},[n("el-input",{attrs:{placeholder:"请输入发送给您的短信验证码"},model:{value:e.code,callback:function(t){e.code=t},expression:"code"}},[n("el-button",{attrs:{slot:"append",plain:"",type:"primary"},on:{click:e.authenticate},slot:"append"},[e._v("继续")])],1)],1),n("el-col",{attrs:{span:6}})],1),n("mfa-form",{ref:"mfaForm"})],1)},xe=[],Se=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("el-form",{ref:"mfaLoginForm",attrs:{action:"/login",method:"POST"}},[n("input",{attrs:{name:"authenticationType",type:"hidden",value:"mfa"}}),n("el-input",{attrs:{name:"challengeId",type:"hidden"},model:{value:e.challengeId,callback:function(t){e.challengeId=t},expression:"challengeId"}}),n("el-input",{attrs:{name:"response",type:"hidden"},model:{value:e.response,callback:function(t){e.response=t},expression:"response"}})],1)},Fe=[],Oe={name:"MfaForm",data:function(){return{challengeId:"",response:""}},methods:{authenticate:function(e,t){var n=this;this.challengeId=e,this.response=t,this.$loading(P),this.$nextTick((function(){n.$refs.mfaLoginForm.$el.submit()}))}}},$e=Oe,Ee=Object(u["a"])($e,Se,Fe,!1,null,"11800de5",null),Pe=Ee.exports,Ne={name:"SmsAuthenticator",components:{MfaForm:Pe},data:function(){return{code:"",challengeId:this.$route.query.challengeId}},methods:{authenticate:function(){this.$refs.mfaForm.authenticate(this.challengeId,this.code)}}},qe=Ne,De=(n("1b54"),Object(u["a"])(qe,ke,xe,!1,null,"00f7dd62",null)),Re=De.exports,Te={name:"MfaView",components:{SmsAuthenticator:Re}},je=Te,Le=(n("6c41"),Object(u["a"])(je,Ie,Ce,!1,null,"1fcd1c59",null)),Ae=Le.exports,Me=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{attrs:{id:"consent-view-container"}},[n("div",[n("h1",[e._v("应用程序"),n("span",{attrs:{id:"client-name-container"}},[e._v(e._s(e.clientId))]),e._v("需要访问您的以下信息，请确认是否授权")]),n("div",{attrs:{id:"scopes-container"}},e._l(e.safeGetScopes(),(function(t,o){return n("span",{key:o},[n("i",{staticClass:"el-icon-check"}),e._v(e._s(t))])})),0),n("div",{attrs:{id:"action-container"}},[n("el-form",{ref:"openIdConsentForm",attrs:{action:"/oauth2/authorize",method:"POST"}},[n("input",{attrs:{name:"client_id",readonly:"",type:"hidden"},domProps:{value:e.clientId}}),e._l(e.safeGetScopes(),(function(e,t){return n("input",{key:t,attrs:{name:"scope",readonly:"",type:"hidden"},domProps:{value:e}})})),n("input",{attrs:{name:"state",readonly:"",type:"hidden"},domProps:{value:e.state}}),n("el-button",{directives:[{name:"show",rawName:"v-show",value:!e.consentButtonClicked,expression:"!consentButtonClicked"}],attrs:{icon:"el-icon-check",type:"success"},on:{click:e.approve}},[e._v("授权")])],2)],1)])])},Qe=[],Be=(n("498a"),n("ac1f"),n("1276"),{name:"ConsentView",data:function(){return{clientId:"",state:"",scopes:"",consentButtonClicked:!1}},mounted:function(){this.clientId=this.$route.query.client_id,this.state=this.$route.query.state,this.scopes=this.$route.query.scope,void 0!==this.clientId&&""!==this.clientId.trim()&&0!==this.safeGetScopes().length&&void 0!==this.state&&""!==this.state.trim()||Ft.push({name:"error",query:{status:403,phrase:"Forbidden",message:"应用的权限申请不合法，请不要通过收藏夹或直接输入网址的方式打开当前页面"}})},methods:{safeGetScopes:function(){return void 0!==this.scopes?this.scopes.split(" "):[]},approve:function(){this.consentButtonClicked=!0,this.$refs.openIdConsentForm.$el.submit()}}}),Ge=Be,We=(n("4a00"),Object(u["a"])(Ge,Me,Qe,!1,null,"4d59158a",null)),Ve=We.exports,He=function(){var e=this,t=e.$createElement;e._self._c;return e._m(0)},Ue=[function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{staticStyle:{"text-align":"center"},attrs:{id:"goodbye-view-container"}},[n("div",[n("h1",{attrs:{id:"goodbye-text"}},[e._v("您已经成功登出")])])])}],Xe={name:"GoodByeView"},ze=Xe,Je=(n("8340"),Object(u["a"])(ze,He,Ue,!1,null,"2a756188",null)),Ze=Je.exports,Ke=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{attrs:{id:"password-recovery-container"}},[n("div",[n("span",{staticClass:"content"},[e.currentState===e.state.PRINCIPAL?n("principal-input",{ref:"principalInput"}):e._e()],1),n("span",{staticClass:"content"},[e.currentState===e.state.CODE?n("code-input",{ref:"codeInput"}):e._e()],1),n("span",{staticClass:"content"},[e.currentState===e.state.RESET?n("reset-password-input",{ref:"codeInput",attrs:{token:this.token}}):e._e()],1),n("span",{staticClass:"content as-button"},[this.currentState!==this.state.PRINCIPAL?n("el-link",{attrs:{underline:!1},on:{click:e.home}},[e._v("返回 ")]):e._e()],1),e.currentState!==this.state.PRINCIPAL&&e.currentState!==this.state.RESET?n("span",{staticClass:"slash"}):e._e(),n("span",{staticClass:"content as-button"},[this.currentState!==this.state.RESET?n("el-link",{attrs:{underline:!1},on:{click:e.next}},[e._v("下一步")]):e._e()],1)])])},Ye=[],et=(n("b0c0"),function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{attrs:{id:"principal-name-container"}},[n("span",[n("el-input",{attrs:{placeholder:"请输入您的手机号"},model:{value:e.name,callback:function(t){e.name=t},expression:"name"}})],1)])}),tt=[],nt={name:"PrincipalInput",data:function(){return{type:"",name:""}}},ot=nt,at=(n("8f52"),Object(u["a"])(ot,et,tt,!1,null,"6bdf3660",null)),rt=at.exports,st=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",[n("el-input",{attrs:{placeholder:"请输入发送给您的验证码"},model:{value:e.code,callback:function(t){e.code=t},expression:"code"}})],1)},it=[],ct={name:"CodeInput",data:function(){return{code:""}}},lt=ct,ut=(n("fa23"),Object(u["a"])(lt,st,it,!1,null,"0f43eaae",null)),dt=ut.exports,pt=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",{attrs:{id:"reset-password-container"}},[n("span",[n("el-input",{attrs:{placeholder:"请输入新的密码",type:"password"},model:{value:e.password,callback:function(t){e.password=t},expression:"password"}})],1),n("span",[n("el-input",{attrs:{placeholder:"请确认您的密码",type:"password"},model:{value:e.confirmed,callback:function(t){e.confirmed=t},expression:"confirmed"}})],1),n("span",[n("el-button",{attrs:{primary:""},on:{click:e.resetPassword}},[e._v("修改")])],1)])},mt=[],ft=(n("7914"),{lock:!0,text:"正在加载",spinner:"el-icon-loading",background:"rgba(0, 0, 0, 0.4)",customClass:"password-recovery-loading-veil"}),ht=ft,vt={name:"ResetPasswordInput",props:["token"],data:function(){return{password:"",confirmed:""}},methods:{resetPassword:function(){var e=this,t=this.$loading(Object.assign(ht,{text:"正在重置密码"}));$.put("/api/me/password",{challengeId:this.$props.token,password:this.password}).then((function(){e.$notify.success({message:"密码重置完成",title:"成功"}),e.$router.push("/")})).finally((function(){t.close()}))}}},gt=vt,bt=(n("6f9a"),Object(u["a"])(gt,pt,mt,!1,null,"b18241b2",null)),wt=bt.exports,_t={PRINCIPAL:"PRINCIPAL",CODE:"CODE",RESET:"RESET"},yt={name:"PasswordRecovery",components:{ResetPasswordInput:wt,CodeInput:dt,PrincipalInput:rt},data:function(){return{currentState:_t.PRINCIPAL,state:_t,token:""}},methods:{home:function(){this.currentState=_t.PRINCIPAL,this.token=""},next:function(){var e,t=this;switch(this.currentState){case this.state.PRINCIPAL:e=this.$loading(Object.assign(ht,{text:"正在发送验证码"})),$.post("/api/me/password:recover",{principalType:this.$refs.principalInput.type,username:this.$refs.principalInput.name}).then((function(e){t.token=e.data.challengeId,t.currentState=_t.CODE})).finally((function(){e.close()}));break;case this.state.CODE:e=this.$loading(Object.assign(ht,{text:"正在验证"})),$.post("/api/me/password:response",{challengeId:this.token,response:this.$refs.codeInput.code}).then((function(){t.currentState=_t.RESET})).finally((function(){e.close()}))}}}},It=yt,Ct=(n("c031"),Object(u["a"])(It,Ke,Ye,!1,null,"5e826a71",null)),kt=Ct.exports;o["default"].use(g["a"]);var xt=[{path:"/",component:oe},{path:"/welcome",component:le},{path:"/mfa",component:Ae},{path:"/consent",component:Ve},{path:"/error",component:he,props:!0,name:"error"},{path:"/qrcode/landing",component:ye},{path:"/password/recovery",component:kt},{path:"/goodbye",component:Ze},{path:"*",component:he}],St=new g["a"]({mode:"history",base:"/",routes:xt}),Ft=St,Ot=n("130e");n("0fae");o["default"].use(F.a),o["default"].use(Ot["a"],x.a),o["default"].config.productionTip=!1,new o["default"]({router:Ft,render:function(e){return e(v)}}).$mount("#app")},"67ed":function(e,t,n){},"6c41":function(e,t,n){"use strict";n("b9c2")},"6f9a":function(e,t,n){"use strict";n("2726")},7914:function(e,t,n){},8340:function(e,t,n){"use strict";n("daee")},"85ec":function(e,t,n){},"875d":function(e,t,n){e.exports=n.p+"img/airplane.3b20ccbc.png"},"8f52":function(e,t,n){"use strict";n("d39b")},9405:function(e,t,n){},9558:function(e,t,n){"use strict";n("eeee")},ae1a:function(e,t,n){},b5d8:function(e,t,n){},b66c:function(e,t,n){"use strict";n("24cd")},b9c2:function(e,t,n){},ba7a:function(e,t,n){e.exports=n.p+"img/hna.cbaf5e52.png"},c031:function(e,t,n){"use strict";n("ae1a")},c631:function(e,t,n){"use strict";n("9405")},d39b:function(e,t,n){},d63d:function(e,t,n){},d6fd:function(e,t,n){},daee:function(e,t,n){},e966:function(e,t,n){},e9fb:function(e,t,n){},eeee:function(e,t,n){},f946:function(e,t,n){"use strict";n("e966")},fa23:function(e,t,n){"use strict";n("67ed")}});
//# sourceMappingURL=app.cbe8cc8e.js.map