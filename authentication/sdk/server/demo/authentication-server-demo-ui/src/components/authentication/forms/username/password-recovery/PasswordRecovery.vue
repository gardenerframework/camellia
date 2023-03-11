<template>
  <div class="password-recovery-box">
    <brand class="brand-location"></brand>
    <div class="password-recovery-form">
      <el-tabs v-model="formItems.type">
        <el-tab-pane :label="$t('components.authentication.forms.username.passwordRecovery.type.mobilePhoneNumber')"
                     name="mobilePhoneNumber">
        </el-tab-pane>
        <el-tab-pane :label="$t('components.authentication.forms.username.passwordRecovery.type.email')"
                     name="email">
        </el-tab-pane>
      </el-tabs>
      <el-form ref="principalForm" :model="formItems" :rules="validation">
        <el-form-item prop="principal">
          <el-input v-model="formItems.principal" :disabled="cooldown > 0"
                    :placeholder="$t('components.authentication.forms.username.passwordRecovery.input.principal.placeholder')"
                    name="principal" prefix-icon="el-icon-user">
            <el-button id="password-recovery-send-code-button" slot="append" :disabled="cooldown > 0"
                       class="password-recovery-authenticate"
                       plain>{{
                cooldown > 0 ? cooldown : $t('components.authentication.forms.username.passwordRecovery.input.sendCode')
              }}
            </el-button>
          </el-input>
        </el-form-item>
      </el-form>
      <el-form ref="recoveryActionForm" :model="formItems" :rules="validation">
        <el-form-item prop="code">
          <el-input v-model="formItems.code" :disabled="!challengeId"
                    :placeholder="$t('components.authentication.forms.username.passwordRecovery.input.code.placeholder')"
                    name="code" prefix-icon="el-icon-key">
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="formItems.password" :disabled="!challengeId"
                    :placeholder="$t('components.authentication.forms.username.passwordRecovery.input.password.placeholder')"
                    name="password" prefix-icon="el-icon-key" show-password type="password">
          </el-input>
        </el-form-item>
        <el-form-item prop="confirm">
          <el-input v-model="formItems.confirm" :disabled="!challengeId"
                    :placeholder="$t('components.authentication.forms.username.passwordRecovery.input.confirm.placeholder')"
                    name="confirm" prefix-icon="el-icon-key" show-password type="password">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button :disabled="!challengeId" class="password-recovery-submit" plain
                     @click="recovery">
            {{
              $t('components.authentication.forms.username.passwordRecovery.submit')
            }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import Brand from "@/components/brand/Brand.vue";
import i18n from "@/i18n/i18n";
import basicAxiosProxy from "@/xhr/axios-aop";
import LoadingVeil from "@/components/veil/LoadingVeil";
import {Notification} from "element-ui";
import user from "@/user/user";

export default {
  name: "PasswordRecovery",
  components: {Brand},
  data() {
    return {
      user: user,
      challengeId: null,
      cooldownTimer: null,
      cooldown: 0,
      formItems: {
        principal: null,
        type: null,
        code: null,
        password: null,
        confirm: null
      }
    }
  },
  methods: {
    startCooldown: function (cooldown) {
      this.cooldown = cooldown
      this.cooldownTimer = setInterval(
          () => {
            if (this.cooldown > 0) {
              --this.cooldown
            } else {
              clearInterval(this.cooldownTimer)
            }
          },
          1000
      )
    },
    authenticate: function (captchaToken) {
      this.$refs.principalForm.validate(isValid => {
        if (isValid) {
          let loading = this.$loading(LoadingVeil);
          basicAxiosProxy.post(
              "/api/me/password:recover",
              {
                captchaToken: captchaToken,
                type: this.formItems.type,
                username: this.formItems.principal
              }
          ).then(
              response => {
                this.challengeId = response.data.challengeId
                this.startCooldown(response.data.cooldown)
              }
          ).catch(
              error => {
                let response = error.response
                if (response.data !== undefined && response.data.error !== undefined) {
                  if (response.data.status === 429) {
                    this.startCooldown(response.data.details.cooldown)
                  }
                }
              }
          ).finally(
              () => {
                loading.close()
              }
          )
        } else {
          return false
        }
      })
    },
    recovery: function () {
      this.$refs.recoveryActionForm.validate(isValid => {
        if (isValid) {
          let loading = this.$loading(LoadingVeil)
          basicAxiosProxy.put(
              "/api/me/password",
              {
                challengeId: this.challengeId,
                response: this.formItems.code,
                password: this.formItems.password,
              }
          ).then(() => {
            let notification = Notification.success(i18n.t("components.authentication.forms.username.passwordRecovery.done"))
            let loading = this.$loading(LoadingVeil)
            setTimeout(() => {
              loading.close()
              notification.close()
              this.$router.push("/")
            }, 1000)
          }).catch(
              error => {
                Notification.error(error.response.data.message)
              }
          ).finally(
              () => {
                loading.close()
              }
          )
        } else {
          return false
        }
      })
    }
  },
  computed: {
    validation() {
      let validatePassword = function (rule, value, callback) {
        console.log(value)
        if (value === '' || value === null) {
          callback(new Error(i18n.t("components.authentication.forms.username.passwordRecovery.input.confirm.invalid")))
        } else if (value !== this.ref.formItems.password) {
          callback(new Error(i18n.t("components.authentication.forms.username.passwordRecovery.input.confirm.notMatch")))
        } else {
          callback()
        }
      }
      return {
        principal: [{
          required: true,
          message: i18n.t("components.authentication.forms.username.passwordRecovery.input.principal.invalid"),
          trigger: 'blur'
        }],
        code: [{
          required: true,
          message: i18n.t("components.authentication.forms.username.passwordRecovery.input.code.invalid"),
          trigger: 'blur'
        }],
        password: [{
          required: true,
          message: i18n.t("components.authentication.forms.username.passwordRecovery.input.password.invalid"),
          trigger: 'blur'
        }],
        confirm: [{
          required: true,
          trigger: 'blur',
          validator: validatePassword,
          ref: this
        }]
      }
    }
  },
  mounted() {
    user.reload()
    new window['TencentCaptcha'](
        document.getElementById("password-recovery-send-code-button"),
        2048219257,
        response => {
          if (response.ret === 0) {
            this.authenticate(response.ticket)
          }
        }
    )
  }
}
</script>

<style scoped>
/deep/ .brand-name {
  color: black;
}

.password-recovery-box {
  width: 456px;
  background: white;
  border-radius: 5px;
  box-shadow: 1px 2px 8px 0 rgba(0, 0, 0, 0.5);
  padding: 91px 56px 60px;
  min-height: 660px;
  box-sizing: border-box;
}


.password-recovery-form {
  margin-top: 16px;
}

.password-recovery-authenticate.el-button, .password-recovery-authenticate.el-button:hover, .password-recovery-authenticate.el-button:focus {
  background-color: #f94f3f;
  color: white;
  border: 0;
  border-radius: 3px;
}

.password-recovery-submit.el-button, .password-recovery-submit.el-button:hover, .password-recovery-submit.el-button:focus {
  background-color: #f94f3f;
  color: white;
  border-radius: 3px;
  width: 100%;
}

/deep/ .el-tabs__item.is-active {
  color: #f94f3f;
}

/deep/ .el-tabs__active-bar {
  background: #f94f3f;
}

/deep/ .el-tabs__item:hover {
  color: #f94f3f;
}

/deep/ .el-input__inner:focus {
  border-color: #f94f3f;
}
</style>