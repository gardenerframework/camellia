<template>
    <div id="qrcode-landing-container">
        <div>
            <i class="el-icon-monitor"></i>
            <div id="qrcode-landing-text">扫码成功，请确认是您本人操作</div>
            <el-button v-show="showConfirmButton && !confirmed" id="qrcode-landing-confirm-button" type="success"
                       @click="confirmLogin">确认登录
            </el-button>
            <div v-show="confirmed" id="qrcode-landing-success-text">确认完成</div>
            <div v-show="errorHappened" id="qrcode-landing-error-text">
                {{ errorMessage === null ? "后台出错了！" : errorMessage }}
            </div>
        </div>
    </div>
</template>

<script>

import axios from "axios";

export default {
    name: "QrCodeAuthenticationLandingView",
    data() {
        return {
            showConfirmButton: false,
            confirmed: false,
            errorHappened: false,
            errorMessage: null
        }
    },
    methods: {
        confirmLogin: function () {
            axios.post("/api/qrcode/" + this.$route.query.token + ":confirm").then(
                () => {
                    this.confirmed = true
                },
                error => {
                    if (error.response.data.error !== undefined) {
                        this.errorMessage = error.response.data.message;
                    }
                    this.errorHappened = true
                }
            )
        }
    },
    mounted() {
        axios.post("/api/qrcode/" + this.$route.query.token + ":scan").then(
            () => {
                this.showConfirmButton = true
            },
            (error) => {
                if (error.response.data.error !== undefined) {
                    this.errorMessage = error.response.data.message;
                }
                this.errorHappened = true
            }
        )
    }
}
</script>

<style scoped>
#qrcode-landing-container {
    text-align: center;
    height: 100vh;
    display: flex;
}

#qrcode-landing-container > div {
    margin: auto;
}

#qrcode-landing-container .el-icon-monitor {
    font-size: xxx-large;
    color: aliceblue;
}

#qrcode-landing-text {
    padding-top: 20px;
    color: aliceblue;
}

#qrcode-landing-confirm-button {
    margin-top: 20px;
}

#qrcode-landing-success-text {
    color: ghostwhite;
    padding-top: 20px;
}

#qrcode-landing-error-text {
    color: ghostwhite;
    padding-top: 20px;
}
</style>