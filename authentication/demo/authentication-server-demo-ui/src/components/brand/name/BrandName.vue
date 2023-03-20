<template>
  <div class="brand-name-box">{{ $t("components.brand.name") }}</div>
</template>

<script>
export default {
  name: "BrandName",
  props: {
    name: String
  }
}
</script>

<style scoped>
.brand-name-box {
  display: inline-block;
  height: 100%;
  color: #f94f3f;
}
</style>