<template>
    <div>
        <el-form ref="usernamePasswordForm" :model="formItems" :rules="validation" action="/login" method="POST">
            <input name="authenticationType" type="hidden" value="username"/>
            <el-form-item prop="username">
                <el-input
                        v-model="formItems.username"
                        :placeholder="$t('components.authentication.forms.username.input.username.placeholder')"
                        name="username"
                        prefix-icon="el-icon-user">
                </el-input>
            </el-form-item>
            <input v-model="formItems.cipher" name="password" type="hidden"/>
            <el-form-item prop="password">
                <el-input v-model="formItems.password"
                          :placeholder="$t('components.authentication.forms.username.input.password.placeholder')"
                          prefix-icon="el-icon-key"
                          show-password
                          type="password"></el-input>
            </el-form-item>
            <el-input v-model="formItems.captchaToken" name="captchaToken" type="hidden"></el-input>
            <div class="forget-password-box">
                <router-link to="/password/recovery">
                    {{ $t("components.authentication.forms.username.forgetPassword") }}
                </router-link>
            </div>
            <div>
                <el-button class="username-longin-button-shadow" plain type="primary" @click="validateForm">
                    {{ $t('components.authentication.forms.username.submit') }}
                </el-button>
                <button id="username-longin-button" style="display: none" type="button"></button>
            </div>
        </el-form>
    </div>
</template>

<script>
import LoadingVeil from "@/components/veil/LoadingVeil";
import i18n from "@/i18n/i18n";
import basicAxiosProxy from "@/xhr/axios-aop";
import cryptoJs from "crypto-js";

export default {
    name: "UsernameAuthenticationForm",
    data() {
        return {
            formItems: {
                username: "",
                password: "",
                cipher: "",
                captchaToken: ""
            }
        }
    },
    computed: {
        validation() {
            return {
                username: [{
                    required: true,
                    message: i18n.t("components.authentication.forms.username.input.username.invalid"),
                    trigger: 'blur'
                }],
                password: [{
                    required: true,
                    message: i18n.t("components.authentication.forms.username.input.password.invalid"),
                    trigger: 'blur'
                }]
            }
        }
    },
    methods: {
        login: function (key) {
            this.$loading(LoadingVeil)
            //执行加密
            if (key) {
                this.formItems.cipher = this.encrypt(this.formItems.password, key)
            } else {
                this.formItems.cipher = this.formItems.password;
            }
            setTimeout(
                () => {
                    this.$refs.usernamePasswordForm.$el.submit()
                },
                300
            )
        },
        validateForm: function () {
            this.$refs.usernamePasswordForm.validate(
                isValid => {
                    if (isValid) {
                        document.getElementById("username-longin-button").click()
                    } else {
                        return false
                    }
                }
            )
        },
        encrypt: function (password, key) {
            let keyBlock = cryptoJs.enc.Base64.parse(key)
            let option = {mode: cryptoJs.mode.ECB, padding: cryptoJs.pad.Pkcs7}
            return cryptoJs.DES.encrypt(password, keyBlock, option)
        }
    },
    mounted() {
        new window['TencentCaptcha'](
            document.getElementById("username-longin-button"),
            2048219257,
            (response) => {
                if (response.ret !== 0) {
                    this.formItems.captchaToken = response.ticket
                    //必要之恶
                    basicAxiosProxy.post(
                        "/api/authentication/username/key"
                    ).then(
                        (response) => {
                            //执行加密
                            this.login(response.data.key)
                        }
                    )
                }
            }
        )
    }
}
</script>

<style scoped>
.username-longin-button-shadow.el-button--primary, .username-longin-button-shadow.el-button--primary:hover, .username-longin-button-shadow.el-button--primary:focus {
    width: 100%;
    background-color: #f94f3f;
    color: white;
    border: 0;
    border-radius: 3px;
    margin-top: 20px;
}

/deep/ .el-form-item__error {
    color: #f94f3f;
}

.forget-password-box {
    width: 100%;
    text-align: right;
}

.forget-password-box a {
    color: #f94f3f;
}
</style>