<template>
    <div class="goodbye-page-box" style="text-align: center">
        <h1 class="goodbye-text">
            {{
            this.user.name ? $t('components.page.goodbyePage.stillSignedIn') : $t('components.page.goodbyePage.goodbye')
            }}
        </h1>
    </div>
</template>

<script>

import User from "@/user/user";

export default {
    name: "GoodbyePage",
    data() {
        return {
            user: User
        }
    },
    mounted() {
        User.reload()
    }
}
</script>

<style scoped>
.goodbye-page-box {
    text-align: center;
}


</style>