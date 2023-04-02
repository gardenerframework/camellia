<template>
  <span class="home-page-box" @click="redirect">
    {{ $t("components.homePage.text") }}
  </span>
</template>

<script>
import router from "@/router";

export default {
    name: "HomePage",
    methods: {
        redirect: function () {
            router.push("/")
        }
    }
}
</script>

<style scoped>
.home-page-box {
    display: inline-block;
    cursor: pointer;
    background-image: url("media/image/house.png");
    background-repeat: no-repeat;
    background-size: 16px;
    padding-left: 18px;
    background-position: left center;
}
</style>