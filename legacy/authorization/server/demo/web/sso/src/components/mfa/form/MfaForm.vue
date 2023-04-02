<template>
    <el-form ref="mfaLoginForm" action="/login" method="POST">
        <input name="authenticationType" type="hidden" value="mfa"/>
        <el-input v-model="challengeId" name="challengeId" type="hidden"/>
        <el-input v-model="response" name="response" type="hidden"/>
    </el-form>
</template>

<script>

import LoadingVeil from "@/components/veil/LoadingVeil";

export default {
    name: "MfaForm",
    data() {
        return {
            challengeId: "",
            response: ""
        }
    },
    methods: {
        authenticate: function (challengeId, response) {
            this.challengeId = challengeId;
            this.response = response;
            this.$loading(LoadingVeil)
            this.$nextTick(() => {
                this.$refs.mfaLoginForm.$el.submit();
            })
        }
    }
}
</script>

<style scoped>

</style>