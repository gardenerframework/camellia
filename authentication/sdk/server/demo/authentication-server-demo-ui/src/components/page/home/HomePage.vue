<template>
  <div class="home-page-box">
    <authentication-forms></authentication-forms>
  </div>
</template>

<script>
import AuthenticationForms from "@/components/authentication/forms/AuthenticationForms.vue";

export default {
  name: "HomePage",
  components: {AuthenticationForms}
}
</script>

<style scoped>

</style>