<template>
  <div id="qrcode-line-block">
    <div id="qrcode-container">
      <div v-show="qrCodeState === QrCodeState.WAIT_FOR_CONFIRMING || qrCodeState === QrCodeState.CONFIRMED"
           id="qrcode-scanned-veil">
        <span>{{
            qrCodeState === QrCodeState.WAIT_FOR_CONFIRMING ? "已扫描" : null
          }}</span>
      </div>
      <div v-show="loadState === LoadState.FAILED" id="qrcode-reload-veil">
        <el-tooltip class="item" content="刷新" effect="dark" placement="top">
          <i class="el-icon-refresh-left" @click="reloadQrCode"></i>
        </el-tooltip>
      </div>
      <form ref="qrcodeLoginForm" action="/login" method="post">
        <input name="type" type="hidden" value="qrcode"/>
        <input v-model="token" name="token" type="hidden"/>
      </form>
      <el-image v-loading="loadState === null" :src="image" alt="二维码"></el-image>
    </div>
  </div>
</template>

<script>
import basicAxiosProxy from "@/xhr/axios-aop";
import LoginLoadingVeil from "../../veil/LoadingVeil";

const LoadState = {
  LOADING: "LOADING",
  FAILED: "FAILED",
  DONE: "DONE"
}

const QrCodeState = {
  EXPIRED: "EXPIRED",
  WAIT_FOR_CONFIRMING: "WAIT_FOR_CONFIRMING",
  CONFIRMED: "CONFIRMED"
}

export default {
  name: "QrCodeForm",
  data() {
    return {
      LoadState: LoadState,
      QrCodeState: QrCodeState,
      token: null,
      image: "",
      loadState: null,
      timerId: null,
      qrCodeState: null
    }
  },
  methods: {
    login: function () {
      this.$loading(LoginLoadingVeil)
      this.$refs.qrcodeLoginForm.submit();
    },
    reloadQrCode: function () {
      //停掉监控定时器
      this.stopWatch()
      this.image = ""
      this.qrCodeState = null
      this.loadState = LoadState.LOADING
      this.token = null;
      basicAxiosProxy.post("/api/qrcode")
          .then(response => {
            this.image = "data:image/png;base64," + response.data.image
            this.loadState = LoadState.DONE
            this.token = response.data.token
            this.watchQrCodeState(response.data.token)
          })
          .catch(error => {
            if (error.response.data.status === 501) {
              this.$notify.error({
                title: "出错啦",
                message: error.response.data.message
              })
            } else {
              this.loadState = LoadState.FAILED
            }
          })
    },
    /**
     * 检查token的扫描状态
     * @param token
     */
    watchQrCodeState: function (token) {
      this.timerId = setInterval(
          token => {
            basicAxiosProxy.get("/api/qrcode/" + token).then(
                response => {
                  this.qrCodeState = response.data.state
                  if (this.qrCodeState === QrCodeState.EXPIRED) {
                    this.reloadQrCode()
                  } else if (this.qrCodeState === QrCodeState.CONFIRMED) {
                    this.login()
                  }
                },
                () => {
                  this.image = ""
                  this.loadState = LoadState.FAILED
                  this.qrCodeState = null
                  this.stopWatch()
                }
            )
          },
          5000,
          token
      )
    },
    stopWatch: function () {
      if (this.timerId) {
        clearInterval(this.timerId)
        this.timerId = null
      }
    }
  },
  mounted() {
    this.reloadQrCode();
  }
}
</script>

<style scoped>
#qrcode-line-block {
  text-align: center;
}

#qrcode-container {
  display: inline-block;
  width: 200px;
  height: 200px;
  border: 1px solid black;
  position: relative;
}

#qrcode-scanned-veil {
  background: rgba(0, 0, 0, 0.8);
  z-index: 10;
  top: 0;
  bottom: 0;
  right: 0;
  left: 0;
  position: absolute;
  color: aliceblue;
  display: flex;
}

#qrcode-scanned-veil span {
  margin: auto;
}

#qrcode-reload-veil {
  background: none;
  z-index: 11;
  top: 0;
  bottom: 0;
  right: 0;
  left: 0;
  position: absolute;
  color: black;
}

#qrcode-reload-veil .el-icon-refresh-left {
  font-size: xxx-large;
  line-height: 200px;
}

#qrcode-reload-veil .el-icon-refresh-left:hover {
  cursor: pointer;
}

#qrcode-veil span {
  color: aliceblue;
  line-height: 200px;
}

/deep/ .el-image {
  width: 100%;
  height: 100%;
}

/deep/ .el-image__error {
  background: none;
  color: black;
}

/deep/ .el-loading-mask {
  background: rgba(0, 0, 0, 0.1);
}

/deep/ .el-image__error {
  display: none;
}
</style>