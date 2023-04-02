<template>
    <div class="error-page-box">
        <div class="error-image-box">

        </div>
        <div class="error-text-box">
            <div class="error-message-box">
                <h1>{{ status }}</h1>
                <p class="error-detail-text">{{ message == null ? phrase : message }}</p>
            </div>
            <div class="back-to-home-page-box">
                <el-button @click="backToHomePage">返回首页</el-button>
            </div>
        </div>
    </div>
</template>

<script>
import router from "@/router";

export default {
    name: "ErrorPage",
    data() {
        return {
            status: null,
            phrase: null,
            message: null
        }
    },
    methods: {
        backToHomePage() {
            router.push("/")
        }
    },
    mounted() {
        if (this.$route.params.pathMatch !== undefined) {
            this.status = 404
            this.phrase = "Not Found"
        } else {
            this.status = this.$route.query.status
            this.phrase = this.$route.query.phrase
            this.message = this.$route.query.message
        }
    }
}
</script>

<style scoped>
.error-image-box {
    display: inline-block;
    width: 400px;
    height: 220px;
    background-image: url("media/image/error.png");
    background-size: 100%;
    background-repeat: no-repeat;
    vertical-align: top;
}

.error-text-box {
    width: 185px;
    height: 220px;
    display: inline-block;
    vertical-align: top;
    initial-letter-wrap: 0;
}

.error-text-box > div {
    height: 110px;
    width: 100%;
    text-align: left;
}

.error-detail-text {
    color: rgba(0, 0, 0, 0.5);
}

/deep/ .el-button, .el-button:hover, .el-button:focus {
    margin-top: 20px;
    background: none;
    color: #f94f3f;
    border: 1px solid #f94f3f;
}

</style>