<template>
  <div class="footer-box">
    <div class="text-box">
      <brand class="brand-footer-container"></brand>
      <span>{{ $t("components.footer.copyright") }}</span>
    </div>
  </div>
</template>

<script>
import Brand from "@/components/brand/Brand.vue";

export default {
  name: "Footer",
  components: {Brand}
}
</script>

<style scoped>
.footer-box {
  width: 100%;
  position: absolute;
  bottom: 0;
  z-index: 100;
}

.text-box {
  bottom: 0;
  margin-left: auto;
  margin-right: auto;
  margin-bottom: 48px;
  text-align: center;
  width: 100%;
}

.brand-footer-container {
  color: #FF0E00;
  margin-right: 16px;
  display: inline-block;
  z-index: 100;
}
</style>