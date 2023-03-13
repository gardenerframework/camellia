<template>
  <form ref="jdForm" action="/login" method="POST">
    <input name="authenticationType" type="hidden" value="jd"/>
    <input v-model="code" name="code" type="hidden"/>
    <input v-model="state" name="state" type="hidden"/>
    <sns-authentication-form-wrapper class="jd-form-box"
                                     @click.n.native="redirectToJd"/>
  </form>
</template>

<script>
import SnsAuthenticationFormWrapper
  from "@/components/authentication/forms/sns/wrapper/SnsAuthenticationFormWrapper.vue";
import basicAxiosProxy from "@/xhr/axios-aop";
import LoadingVeil from "@/components/veil/LoadingVeil";
import {Base64} from "js-base64";

export default {
  name: "JdAuthenticationForm",
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
    redirectToJd: function () {
      let state = null;
      basicAxiosProxy.post("/api/authentication/state/oauth2/jd").then(
          response => {
            state = response.data.state;
            window.location.href = "https://open-oauth.jd.com/oauth2/to_login?app_key="
                + this.appId
                + "&redirect_uri=" + encodeURIComponent(this.$props.redirectUrl) + "&response_type=code&scope=snsapi_base&state=" + encodeURIComponent(state)
          }
      )
    },
    login: function () {
      this.code = this.$route.query.code
      this.state = this.$route.query.state
      setTimeout(() => {
        this.$loading(LoadingVeil)
        this.$refs.jdForm.submit();
      }, 200)
    }
  },
  mounted() {
    basicAxiosProxy.get("/api/options/jdUserAuthenticationServiceOption").then(
        response => {
          this.appId = response.data.option.appId;
        }
    )
    if (this.$route.query.code && this.$route.query.state) {
      let stateData = JSON.parse(Base64.decode(this.$route.query.state));
      if (stateData.jd) {
        //是回调
        this.login()
      }
    }
  }
}
</script>

<style scoped>
.jd-form-box {
  background-image: url("media/image/wechat.svg");
  background-size: 24px;
  background-repeat: no-repeat;
  background-position: center;
  cursor: pointer;
}
</style>