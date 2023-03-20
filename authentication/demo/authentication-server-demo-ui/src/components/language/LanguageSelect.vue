<template>
  <div class="language-select-box">
    <span class="dropdown-text">
      {{ $t("components.languageSelect.text") }}</span>
    <el-dropdown @command="changeLocale">
      <span class="dropdown-link"><i class="el-icon-caret-bottom"></i></span>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item v-for="(_, index) in messages" :key="index" :command="index">{{
            messages[index].name
          }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<script>
import messages from "@/i18n/messages";
import i18n from "@/i18n/i18n";

export default {
  name: "LanguageSelect",
  methods: {
    changeLocale: function (command) {
      i18n.locale = command;
      localStorage.setItem("locale", command)
    }
  },
  computed: {
    messages() {
      return messages
    }
  },
  mounted() {
    i18n.locale = localStorage.getItem("locale")
  }
}
</script>

<style scoped>
.dropdown-text {
  color: black;
  background-image: url("media/image/earth.png");
  background-repeat: no-repeat;
  background-size: 16px;
  padding-left: 18px;
  background-position: left center;
}

.dropdown-link {
  cursor: pointer;
}

/deep/ .el-dropdown-menu__item:not(.is-disabled):hover {
  color: #f94f3f;
  background: none;
}
</style>