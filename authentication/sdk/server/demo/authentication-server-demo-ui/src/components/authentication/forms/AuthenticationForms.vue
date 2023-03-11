<template>
  <div class="authentication-forms-box">
    <div>
      <brand class="brand-location"></brand>
      <div class="input-qrcode-box">
        <div class="input-box">
          <el-tabs v-model="activeInputForm">
            <el-tab-pane v-if="this.authenticationTypes.includes('username')" :label="$t('components.authentication.forms.username.title')"
                         name="username">
              <username-authentication-form/>
            </el-tab-pane>
            <el-tab-pane v-if="this.authenticationTypes.includes('sms')" :label="$t('components.authentication.forms.sms.title')"
                         name="sms">
              <sms-authentication-form/>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
      <div class="sns-box">
        <div class="sns-title">{{ $t("components.authentication.authenticationForms.title") }}</div>
        <div class="sns-forms-location">
          <alipay-authentication-form v-if="this.authenticationTypes.includes('alipay')"
                                      :redirect-url="this.snsRedirectUrl"></alipay-authentication-form>
          <we-chat-authentication-form v-if="this.authenticationTypes.includes('wechat')"
                                       :redirect-url="this.snsRedirectUrl"></we-chat-authentication-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Brand from "@/components/brand/Brand.vue";
import basicAxiosProxy from "@/xhr/axios-aop";
import UsernameAuthenticationForm from "@/components/authentication/forms/username/UsernameAuthenticationForm.vue";
import SmsAuthenticationForm from "@/components/authentication/forms/sms/SmsAuthenticationForm.vue";
import AlipayAuthenticationForm from "@/components/authentication/forms/sns/alipay/AlipayAuthenticationForm.vue";
import WeChatAuthenticationForm from "@/components/authentication/forms/sns/wechat/WeChatAuthenticationForm.vue";

export default {
  name: "AuthenticationForms",
  mounted() {
    basicAxiosProxy.get("/api/authentication/service").then(
        response => {
          this.authenticationTypes = response.data.types
          this.activeInputForm = this.authenticationTypes.includes("username") ? "username" :
              this.authenticationTypes.includes("sms") ? "sms" : ""
        }
    )
  },
  methods: {},
  data() {
    return {
      //激活的输入表单
      activeInputForm: null,
      authenticationTypes: [],
      snsRedirectUrl: window.location.protocol + '//' + window.location.host
    }
  },
  components: {
    WeChatAuthenticationForm,
    AlipayAuthenticationForm, SmsAuthenticationForm, UsernameAuthenticationForm, Brand
  }
}
</script>

<style scoped>
/deep/ .brand-name {
  color: black;
}

.authentication-forms-box {
  width: 456px;
  background: white;
  border-radius: 5px;
  box-shadow: 1px 2px 8px 0 rgba(0, 0, 0, 0.5);
  padding: 91px 56px 60px;
  min-height: 660px;
  box-sizing: border-box;
}

.input-qrcode-box {
  min-height: 337px;
  margin-top: 16px;
}

.sns-title {
  text-align: center;
  color: #CDCDCD;
}

.sns-forms-location {
  margin-top: 12px;
  display: flex;
  justify-content: center;
}

/deep/ .el-tabs__item.is-active {
  color: #f94f3f;
}

/deep/ .el-tabs__active-bar {
  background: #f94f3f;
}

/deep/ .el-tabs__item:hover {
  color: #f94f3f;
}

/deep/ .el-input__inner:focus {
  border-color: #f94f3f;
}
</style>