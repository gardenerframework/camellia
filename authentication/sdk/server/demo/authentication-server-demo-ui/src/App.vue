<template>
  <div id="app">
    <Header></Header>
    <div class="content-box">
      <div class="content">
        <router-view/>
      </div>
    </div>
    <Footer></Footer>
  </div>
</template>

<script>
import Header from "@/components/header/Header.vue";
import Footer from "@/components/footer/Footer.vue";

document.title = "Camellia 统一身份认证系统"

export default {
  components: {Header, Footer}
}
</script>
<style>
@font-face {
  font-family: "京东郎正体";
  src: url("media/fonts/京东郎正体.ttf");
}

body {
  margin: 0;
  padding: 0;
}

#app {
  position: relative;
  min-width: 1440px;
  min-height: 900px;
  margin: 0;
  height: 100vh;
  /*默认使用京东朗正体*/
  font-family: "京东郎正体", "Microsoft YaHei", serif;
  background: #F7F7F7;
}

.content-box {
  position: absolute;
  top: 0;
  bottom: 0;
  width: 100%;
  z-index: 10;
  display: flex;
  align-items: center;
}

.content {
  display: inline-block;
  margin-left: auto;
  margin-right: auto;
}
</style>