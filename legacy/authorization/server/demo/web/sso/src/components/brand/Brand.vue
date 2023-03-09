<template>
  <div class="container">
    <brand-logo class="brand-logo"></brand-logo>
    <brand-name class="brand-name"></brand-name>
  </div>
</template>

<script>
import BrandLogo from "@/components/brand/logo/BrandLogo.vue";
import BrandName from "@/components/brand/name/BrandName.vue";

export default {
  name: "Brand",
  components: {BrandName, BrandLogo}
}
</script>

<style scoped>
div {
  vertical-align: middle;
}
.brand-logo {
  width: 32px;
  height: 32px;
  display: inline-block;
}
.brand-name {
  margin-left: 16px;
}
</style>