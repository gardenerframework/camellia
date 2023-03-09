<template>
  <div class="avatar-box">
    <el-avatar :src="user.avatar"></el-avatar>
    <div class="username">{{
        user.name ? user.name.substring(0, 4) : $t("components.avatar.unauthorized")
      }}
    </div>
    <el-dropdown @command="logout" v-if="user.name">
      <span class="dropdown-link">
        <i class="el-icon-caret-bottom"></i>
      </span>
      <el-dropdown-menu>
        <el-dropdown-item>{{ $t("components.avatar.logout") }}</el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
  </div>
</template>

<script>

import User from "@/user/user";

export default {
  name: "Avatar",
  data() {
    return {
      user: User
    }
  },
  methods: {
    logout: function () {
      window.location.href = "/logout";
    }
  },
  mounted() {
    User.reload()
  }
}
</script>

<style scoped>
.avatar-box {
  display: flex;
  align-items: center;
  vertical-align: middle;
}

.username {
  display: inline-block;
  margin-left: 5px;
}

.dropdown-link {
  cursor: pointer;
}

/deep/ .el-dropdown-menu__item:not(.is-disabled):hover {
  color: #f94f3f;
  background: none;
}
</style>