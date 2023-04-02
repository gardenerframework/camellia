<template>
    <div class="header-box">
        <brand class="brand-header"></brand>
        <div class="title">{{ $t("components.header.title") }}</div>
        <div class="dock-right">
            <home-page class="home-page-container"></home-page>
            <language-select class="language-select-container"></language-select>
            <avatar></avatar>
        </div>
    </div>
</template>

<script>

import Brand from "@/components/brand/Brand.vue";
import HomePage from "@/components/home/HomePage.vue";
import LanguageSelect from "@/components/language/LanguageSelect.vue";
import Avatar from "@/components/avatar/Avatar.vue";

export default {
    name: "Header",
    components: {Avatar, LanguageSelect, HomePage, Brand}
}
</script>

<style scoped>
.header-box {
    background: white;
    top: 0;
    height: 64px;
    width: 100%;
    display: flex;
    align-items: center;
    z-index: 100;
    position: relative;
}

.brand-header {
    margin-left: 81px;
    display: flex;
    align-items: center;
}

.title {
    margin-left: 16px;
}

.dock-right {
    height: 64px;
    margin-right: 197px;
    margin-left: auto;
    display: flex;
    align-items: center;
}

.home-page-container, .language-select-container {
    height: 64px;
    line-height: 64px;
}

.home-page-container {
    margin-right: 24px;
}

.language-select-container {
    margin-right: 24px;
}
</style>