<template>
  <form ref="alipayForm" action="/login" method="POST">
    <input name="authenticationType" type="hidden" value="alipay"/>
    <input v-model="code" name="code" type="hidden"/>
    <input v-model="state" name="state" type="hidden"/>
    <sns-authentication-form-wrapper class="alipay-form-box"
                                     @click.n.native="redirectToAlipay"/>
  </form>
</template>

<script>
import basicAxiosProxy from "../../../../../xhr/axios-aop";
import LoadingVeil from "../../../../veil/LoadingVeil";
import SnsAuthenticationFormWrapper
  from "@/components/authentication/forms/sns/wrapper/SnsAuthenticationFormWrapper.vue";

export default {
  name: "AlipayAuthenticationForm",
  components: {SnsAuthenticationFormWrapper},
  props: {
    redirectUrl: null
  },
  data() {
    return {
      appId: null,
      code: "",
      state: "",
    }
  },
  methods: {
    redirectToAlipay: function () {
      let state = null;
      basicAxiosProxy.post("/api/authentication/state/oauth2/alipay").then(
          response => {
            state = response.data.state;
            window.location.href = "https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id="
                + this.appId
                + "&scope=auth_user&redirect_uri=" + encodeURIComponent(this.$props.redirectUrl) + "&state=" + encodeURIComponent(state);
          }
      )
    },
    login: function () {
      this.code = this.$route.query.auth_code
      this.state = this.$route.query.state
      setTimeout(() => {
        this.$loading(LoadingVeil)
        this.$refs.alipayForm.submit();
      }, 200)
    }
  },
  mounted() {
    basicAxiosProxy.get("/api/options/alipayUserAuthenticationServiceOption").then(
        response => {
          this.appId = response.data.option.appId;
        }
    )
    if (this.$route.query.source === "alipay_wallet") {
      //是回调
      this.login()
    }
  }
}
</script>

<style scoped>
.alipay-form-box {
  background-image: url("media/image/aplipay.webp");
  background-size: 24px;
  background-repeat: no-repeat;
  background-position: center;
  cursor: pointer;
}
</style>