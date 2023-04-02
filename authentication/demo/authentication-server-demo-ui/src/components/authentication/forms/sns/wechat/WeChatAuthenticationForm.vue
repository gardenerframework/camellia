<template>
    <form ref="weChatForm" action="/login" method="POST">
        <input name="authenticationType" type="hidden" value="wechat"/>
        <input v-model="code" name="code" type="hidden"/>
        <input v-model="state" name="state" type="hidden"/>
        <sns-authentication-form-wrapper class="wechat-form-box"
                                         @click.n.native="redirectToWechat"/>
    </form>
</template>

<script>
import SnsAuthenticationFormWrapper
    from "@/components/authentication/forms/sns/wrapper/SnsAuthenticationFormWrapper.vue";
import basicAxiosProxy from "@/xhr/axios-aop";
import LoadingVeil from "@/components/veil/LoadingVeil";
import {Base64} from "js-base64";

export default {
    name: "WeChatAuthenticationForm",
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
        redirectToWechat: function () {
            let state = null;
            basicAxiosProxy.post("/api/authentication/state/oauth2/wechat").then(
                response => {
                    state = response.data.state;
                    window.location.href = "https://open.weixin.qq.com/connect/qrconnect?appid="
                        + this.appId
                        + "&redirect_uri=" + encodeURIComponent(this.$props.redirectUrl) + "&response_type=code&scope=snsapi_login&state=" + encodeURIComponent(state)
                        + "#wechat_redirect";
                }
            )
        },
        login: function () {
            this.code = this.$route.query.code
            this.state = this.$route.query.state
            setTimeout(() => {
                this.$loading(LoadingVeil)
                this.$refs.weChatForm.submit();
            }, 200)
        }
    },
    mounted() {
        basicAxiosProxy.get("/api/options/weChatUserAuthenticationServiceOption").then(
            response => {
                this.appId = response.data.option.appId;
            }
        )
        if (this.$route.query.code && this.$route.query.state) {
            let stateData = JSON.parse(Base64.decode(this.$route.query.state));
            if (stateData.wechat) {
                //是回调
                this.login()
            }
        }
    }
}
</script>

<style scoped>
.wechat-form-box {
    background-image: url("media/image/wechat.svg");
    background-size: 24px;
    background-repeat: no-repeat;
    background-position: center;
    cursor: pointer;
}
</style>