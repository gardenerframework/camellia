<template>
  <h1>
    <span>{{
        this.user.name ? $t("components.page.welcomePage.greeting") : $t("components.page.welcomePage.unauthorized")
      }}
    </span>
    <span v-if="this.user.name" class="username">{{ this.user.name }}</span>
  </h1>
</template>

<script>

import User from "@/user/user";

export default {
  name: "WelcomePage",
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
.username {
  color: #f94f3f;
}
</style>