<template>
    <el-form ref="mobilePhoneForm" :model="formItems" :rules="validation" action="/login" method="POST">
        <input name="authenticationType" type="hidden" value="sms"/>
        <el-row>
            <el-form-item prop="mobilePhoneNumber">
                <el-input
                        v-model="formItems.mobilePhoneNumber"
                        :placeholder="$t('components.authentication.forms.sms.input.mobilePhoneNumber.placeholder')"
                        name="mobilePhoneNumber"
                        prefix-icon="el-icon-mobile">
                    <el-button slot="append" :disabled="coolDownSeconds > 0" class="request-sms-code-button-shadow"
                               @click="validateMobilePhoneNumber">
                        {{
                        $t("components.authentication.forms.sms.requestCode")
                        }}{{ coolDownSeconds > 0 ? '(' + coolDownSeconds + ')' : null }}
                    </el-button>
                </el-input>
                <button id="request-sms-code-button" style="display: none" type="button"></button>
            </el-form-item>
        </el-row>
        <el-row>
            <el-form-item prop="code">
                <el-input v-model="formItems.code"
                          :placeholder="$t('components.authentication.forms.sms.input.code.placeholder')"
                          name="code"></el-input>
            </el-form-item>
        </el-row>
        <el-row>
            <el-button class="sms-longin-button" plain type="primary" @click="login">
                {{ $t('components.authentication.forms.sms.submit') }}
            </el-button>
        </el-row>
    </el-form>
</template>

<script>
import LoadingVeil from "@/components/veil/LoadingVeil";
import basicAxiosProxy from "@/xhr/axios-aop";
import i18n from "@/i18n/i18n";

export default {
    name: "SmsAuthenticationForm.vue",
    computed: {
        validation() {
            return {
                mobilePhoneNumber: [{
                    required: true,
                    message: i18n.t("components.authentication.forms.sms.input.mobilePhoneNumber.invalid"),
                    trigger: 'blur'
                }],
                code: [{
                    required: true,
                    message: i18n.t("components.authentication.forms.sms.input.code.invalid"),
                    trigger: 'blur'
                }]
            }
        }
    },
    data() {
        return {
            formItems: {
                mobilePhoneNumber: "",
                code: ""
            },
            coolDownSeconds: 0,
            timerId: null,
        }
    },
    methods: {
        coolDown: function (coolDownSeconds) {
            this.coolDownSeconds = coolDownSeconds
            this.timerId = setInterval(
                () => {
                    --this.coolDownSeconds
                    if (this.coolDownSeconds === 0) {
                        clearInterval(this.timerId)
                        this.timerId = null
                    }
                },
                1000
            )
        },
        login: function () {
            this.$refs.mobilePhoneForm.validate(isValid => {
                if (isValid) {
                    this.$loading(LoadingVeil)
                    this.$refs.mobilePhoneForm.$el.submit()
                } else {
                    return false
                }
            })
        },
        validateMobilePhoneNumber: function () {
            this.$refs.mobilePhoneForm.validateField("mobilePhoneNumber", hasError => {
                    if (!hasError) {
                        document.getElementById("request-sms-code-button").click()
                    } else {
                        return false
                    }
                }
            )
        }
    },
    mounted() {
        new window['TencentCaptcha'](
            document.getElementById("request-sms-code-button"),
            2048219257,
            response => {
                if (response.ret !== 0) {
                    basicAxiosProxy.post("/api/authentication/sms", {
                        mobilePhoneNumber: this.formItems.mobilePhoneNumber,
                        captchaToken: response.ticket
                    }).then(
                        response => {
                            this.coolDown(response.data.cooldown);
                        }
                    ).catch(
                        error => {
                            let response = error.response
                            if (response.data !== undefined && response.data.error !== undefined) {
                                if (response.data.status === 429) {
                                    basicAxiosProxy.get("/api/authentication/sms?mobilePhoneNumber=" + this.mobilePhoneNumber).then(
                                        response => {
                                            this.coolDown(response.data.details.cooldown)
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            }
        )
    }
}
</script>

<style scoped>

.sms-longin-button.el-button--primary, .sms-longin-button.el-button--primary:hover, .sms-longin-button.el-button--primary:focus {
    width: 100%;
    background-color: #f94f3f;
    color: white;
    border: 0;
    border-radius: 3px;
    margin-top: 37px;
}
</style>