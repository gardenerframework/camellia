<template>
    <div class="mfa-authentication-box">
        <div>
            <brand class="brand-location"></brand>
            <div>
                <h1>{{ $t('components.page.mfaAuthenticationPage.title') }} </h1>
                <p class="text"> {{ $t('components.page.mfaAuthenticationPage.text') }}</p>
                <div class="mfa-authenticator-container">
                    <sms-authenticator v-if="this.$route.query.challengeAuthenticatorName==='sms'"
                                       :duration="duration"></sms-authenticator>
                </div>
                <span v-if="duration > 0"> {{ $t('components.page.mfaAuthenticationPage.timeLimit') }}</span>
                <span class="time-limit"> {{
                    duration ? (duration > 0 ? Math.floor(duration / 1000) : $t('components.page.mfaAuthenticationPage.expired')) : "?"
                    }}</span>
                {{ duration > 0 ? (s) : "" }}
            </div>
        </div>
    </div>
</template>

<script>
import Brand from "@/components/brand/Brand.vue";
import SmsAuthenticator from "@/components/mfa/sms/SmsAuthenticator.vue";

export default {
    name: "MfaAuthenticationPage",
    components: {SmsAuthenticator, Brand},
    data() {
        return {
            expiryTime: null,
            duration: null,
            timer: null
        }
    },
    methods: {
        countdown: function () {
            if (this.expiryTime) {
                this.timer = setInterval(
                    () => {
                        this.duration = this.expiryTime - new Date();
                        if (this.duration < 0) {
                            clearInterval(this.timer)
                        }
                    },
                    1
                )
            }
        }
    },
    mounted() {
        this.expiryTime = this.$route.query.expiryTime ? new Date(decodeURIComponent(this.$route.query.expiryTime)) : null;
        this.countdown()
    }
}
</script>

<style scoped>
/deep/ .brand-name {
    color: black;
}

.mfa-authentication-box {
    width: 456px;
    background: white;
    border-radius: 5px;
    box-shadow: 1px 2px 8px 0 rgba(0, 0, 0, 0.5);
    padding: 91px 56px 60px;
    min-height: 660px;
    box-sizing: border-box;
}

.text {
    color: rgb(0, 0, 0, 0.5);
}

.time-limit {
    color: #f94f3f;
}

.mfa-authenticator-container {
    margin-top: 32px;
}

/deep/ .el-input__inner:focus {
    border-color: #f94f3f;
}
</style>