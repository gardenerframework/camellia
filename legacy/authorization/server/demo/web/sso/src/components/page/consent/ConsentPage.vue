<template>
    <div>
        <div v-if="user.name" class="consent-page-box">
            <div class="application-detail-box">
                <div class="application-info">
                    <img :src="client.logo" class="application-logo"/>
                    <div class="application-text">
                        <div class="application-title">{{ client.name }}</div>
                        <div class="application-description">{{ client.description }}</div>
                    </div>
                </div>
                <div v-if="this.$route.query.scope" class="authorization-notice">
                    <div class="authorization-notice-text">
                        {{
                        $t("authorization.notice")
                        }}
                    </div>
                    <div class="authorization-list">
                        <div v-for="scope in this.$route.query.scope.split(' ')" :key="scope">
                            <i class="el-icon-check"></i>
                            {{
                            $t("authorization.scope['" + scope + "']")
                            }}
                        </div>
                    </div>
                </div>
            </div>
            <div class="authorization-consent-box">
                <div class="authorization-consent-notice">
                    <div class="authorization-consent-application-name">{{ client.name }}</div>
                    <div CLASS="authorization-consent-claim">{{ $t("authorization.claim") }}</div>
                </div>
                <div class="authorization-consent-avatar">
                    <img :src="user.avatar"/>
                </div>
                <div class="authorization-consent-username">
                    {{ user.name }}
                </div>
                <div class="authorization-consent-form">
                    <el-form v-if="this.$route.query.scope" ref="openIdConsentForm" action="/oauth2/authorize"
                             method="POST">
                        <input :value="client.clientId" name="client_id" readonly type="hidden"/>
                        <input v-for="(scope, index) in this.$route.query.scope.split(' ')" :key="index" :value="scope"
                               name="scope"
                               readonly
                               type="hidden"/>
                        <input :value="state" name="state" readonly type="hidden"/>
                        <el-button class="consent" @click="consent">授权</el-button>
                    </el-form>
                </div>
            </div>
        </div>
        <div v-if="!user.name">
            <h1>
    <span>{{
        $t("components.page.consentPage.unauthorized")
        }}
    </span>
                <span v-if="this.user.name" class="username">{{ this.user.name }}</span>
            </h1>
        </div>
    </div>
</template>

<script>
import Client from "@/client/client";
import User from "@/user/user";
import user from "@/user/user";
import LoadingVeil from "@/components/veil/LoadingVeil";

export default {
    name: "ConsentPage",
    data() {
        return {
            client: Client,
            user: User,
            state: null
        }
    },
    methods: {
        consent: function () {
            this.$loading(LoadingVeil)
            this.$refs.openIdConsentForm.$el.submit()
        }
    },
    mounted() {
        if (this.$route.query.client_id) {
            this.client.reload(this.$route.query.client_id)
        }
        user.reload()
        this.state = this.$route.query.state;
    }
}
</script>

<style scoped>
.application-detail-box, .authorization-consent-box {
    height: 260px;
    display: inline-block;
    vertical-align: top;
}

.application-detail-box {
    width: 360px;
    border-radius: 4px;
    border: 1px solid #CDCDCD;
}

.application-info, .authorization-notice {
    margin-left: 42px;
}

.application-info {
    vertical-align: top;
    margin-top: 42px;
    display: flex;
}

.application-logo {
    width: 50px;
    height: 50px;
    display: inline-block;
    border: 1px dotted #CDCDCD;
}

.application-text {
    margin-left: 8px;
    display: inline-block;
    margin-top: 8px;
    margin-right: 42px;
}

.application-title {
    font-size: 16px;
    color: #f94f3f;
}

.application-description {
    font-size: 14px;
    color: #979797;
    display: inline-block;
}

.authorization-notice {
    margin-top: 30px;
}

.authorization-list {
    margin-top: 3px;
}

.authorization-consent-box {
    margin-left: 100px;
    margin-top: 10px;
}

.authorization-consent-application-name {
    display: inline-block;
    color: #f94f3f;
}

.authorization-consent-claim {
    display: inline-block;
    margin-left: 3px;
}

.authorization-consent-avatar {
    display: flex;
    align-items: center;
}

.authorization-consent-avatar img {
    width: 80px;
    height: 80px;
    margin-left: auto;
    margin-right: auto;
    margin-top: 20px;
    border: 1px dotted #CDCDCD;
}

.authorization-consent-username {
    margin-top: 20px;
    text-align: center;
}

.authorization-consent-form {
    text-align: center;
}

.consent.el-button, .consent.el-button:hover, .consent.el-button:focus {
    width: 220px;
    background-color: #f94f3f;
    color: white;
    border: 0;
    border-radius: 3px;
    margin-top: 40px;
}
</style>