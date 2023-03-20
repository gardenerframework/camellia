import VueI18n from "vue-i18n";
import messages from "@/i18n/messages";
import Vue from "vue";

Vue.use(VueI18n)

const i18n = new VueI18n({
    locale: 'cn',
    fallbackLocale: 'cn',
    messages
})

export default i18n;