<template>
    <div>
        <el-form ref="smaCodeMfaForm" :model="formItems" :rules="validation">
            <el-form-item prop="code">
                <el-input v-model="formItems.code" :disabled="!duration || duration < 0"
                          :placeholder="$t('components.mfa.sms.input.code.placeholder')">
                    <el-button slot="append" :disabled="!duration || duration < 0" plain type="primary"
                               @click="authenticate">{{
                        $t('components.mfa.sms.submit')
                        }}
                    </el-button>
                </el-input>
            </el-form-item>
        </el-form>
        <mfa-form ref="mfaForm"></mfa-form>

    </div>
</template>

<script>
import MfaForm from "../form/MfaForm.vue";
import i18n from "@/i18n/i18n";

export default {
    name: "SmsAuthenticator",
    components: {MfaForm},
    props: {
        duration: null
    },
    computed: {
        validation() {
            return {
                code: [{
                    required: true,
                    message: i18n.t("components.mfa.sms.input.code.invalid"),
                    trigger: 'blur'
                }]
            }
        }
    },
    data() {
        return {
            formItems: {
                code: "",
            },
            challengeId: this.$route.query.challengeId
        }
    },
    methods: {
        authenticate: function () {
            this.$refs.smaCodeMfaForm.validate(
                isValid => {
                    if (isValid) {
                        this.$refs.mfaForm.authenticate(this.challengeId, this.formItems.code)
                    } else {
                        return false
                    }
                }
            )
        }
    }
}

</script>

<style scoped>

/deep/ .el-button.el-button--primary, .el-button.el-button--primary:hover, .el-button.el-button--primary:focus {
    background-color: #f94f3f;
    color: white;
    border: 0;
    border-radius: 3px;
}

/deep/ .el-button.el-button--primary.is-disabled, .el-button.el-button--primary.is-disabled:focus, .el-button.el-button--primary.is-disabled:hover {
    background-color: white;
    color: rgba(0, 0, 0, 0.5);
    border: 0;
    border-radius: 3px;
}
</style>