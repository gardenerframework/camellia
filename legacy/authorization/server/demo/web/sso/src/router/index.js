import Vue from 'vue'
import VueRouter from 'vue-router'
import QrCodeAuthenticationLandingView from "@/view/QrCodeAuthenticationLandingView";
import ErrorPage from "@/components/page/error/ErrorPage.vue";
import WelcomePage from "@/components/page/welcome/WelcomePage.vue";
import HomePage from "@/components/page/home/HomePage.vue";
import GoodbyePage from "@/components/page/goodbye/GoodbyePage.vue";
import MfaAuthenticationPage from "@/components/page/mfa/MfaAuthenticationPage.vue";
import ConsentPage from "@/components/page/consent/ConsentPage.vue";
import PasswordRecovery from "@/components/authentication/forms/username/password-recovery/PasswordRecovery.vue";

Vue.use(VueRouter)

const routes = [
    {path: '/', component: HomePage},
    {path: '/welcome', component: WelcomePage},
    {path: '/mfa', component: MfaAuthenticationPage},
    {path: '/consent', component: ConsentPage},
    {path: '/error', component: ErrorPage, props: true, name: "error"},
    {path: '/qrcode/landing', component: QrCodeAuthenticationLandingView},
    {path: "/password/recovery", component: PasswordRecovery},
    {path: "/goodbye", component: GoodbyePage},
    {path: '*', component: ErrorPage},
]

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})

export default router
