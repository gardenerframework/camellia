import Vue from 'vue'
import App from './App.vue'
import router from './router'
import axios from "axios"
import VueAxios from 'vue-axios'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import i18n from "@/i18n/i18n";

Vue.use(ElementUI)
Vue.use(VueAxios, axios)
Vue.config.productionTip = false

new Vue({
    i18n,
    router,
    render: h => h(App)
}).$mount('#app')
