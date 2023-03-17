const messages = {
    cn: {
        name: "中文",
        components: {
            brand: {
                name: "camellia"
            },
            header: {
                title: "统一身份认证系统",
            },
            homePage: {
                text: "主页"
            },
            languageSelect: {
                text: "语言"
            },
            footer: {
                copyright: "版权所属 Gardenerframework"
            },
            avatar: {
                unauthorized: "请登录",
                logout: "退出"
            },
            mfa: {
                sms: {
                    submit: "继续",
                    input: {
                        code: {
                            placeholder: "请输入短信验证码",
                            invalid: "请输入我们发送给您的短信验证码"
                        }
                    }
                }
            },
            page: {
                welcomePage: {
                    greeting: "欢迎您",
                    unauthorized: "请登录"
                },
                goodbyePage: {
                    goodbye: "您已经成功登出",
                    stillSignedIn: "您还没登出"
                },
                mfaAuthenticationPage: {
                    title: "多因子验证",
                    text: "检测到您的登录可能存在风险，需要进行多因子验证",
                    timeLimit: "认证因子有效期至",
                    expired: "已过期，请重新登录"
                },
                consentPage: {
                    unauthorized: "请登录"
                }
            },
            authentication: {
                authenticationForms: {
                    title: "第三方账号登录"
                },
                forms: {
                    username: {
                        passwordRecovery: {
                            done: "密码重置成功，稍候将返回首页",
                            submit: "找回",
                            type: {
                                mobilePhoneNumber: "手机号找回",
                                email: "邮箱找回"
                            },
                            input: {
                                principal: {
                                    placeholder: "请输入登录名",
                                    invalid: "请输入登录名"
                                },
                                sendCode: "发送验证码",
                                code: {
                                    placeholder: "请输入发送给您的验证码",
                                    invalid: "请输入发送给您的验证码"
                                },
                                password: {
                                    placeholder: "请输入新的密码",
                                    invalid: "请输入新的密码"
                                },
                                confirm: {
                                    placeholder: "请确认您的新密码",
                                    invalid: "请确认您的新密码",
                                    notMatch: "两次输入不一致"
                                },
                            }
                        },
                        title: "密码登录",
                        input: {
                            username: {
                                placeholder: "请输入用户名",
                                invalid: "请输入用户名"
                            },
                            password: {
                                placeholder: "请输入密码",
                                invalid: "请输入密码"
                            }
                        },
                        submit: "登录",
                        forgetPassword: "忘记密码？"
                    },
                    sms: {
                        submit: "登录",
                        title: "短信验证码",
                        input: {
                            mobilePhoneNumber: {
                                placeholder: "请输入手机号",
                                invalid: "请输入手机号"
                            },
                            code: {
                                placeholder: "请输入验证码",
                                invalid: "请输入验证码"
                            }
                        },
                        requestCode: "发送"
                    }
                }
            }
        },
        authorization: {
            claim: "申请获取您的部分个人信息",
            notice: "授权该应用后，它将可以访问",
            scope: {
                profile: "您的公开信息(昵称，头像等)"
            }
        }
    },
    en: {
        name: "English",
        components: {
            brand: {
                name: "camellia"
            },
            header: {
                title: "Unified Authentication Server",
            },
            homePage: {
                text: "Home page"
            },
            languageSelect: {
                text: "Language"
            },
            footer: {
                copyright: "Copyright© Gardenerframework"
            },
            avatar: {
                unauthorized: "Sign in",
                logout: "Logout"
            },
            mfa: {
                sms: {
                    submit: "Continue",
                    input: {
                        code: {
                            placeholder: "Input the sms code received",
                            invalid: "Input the sms code received"
                        }
                    }
                }
            },
            page: {
                welcomePage: {
                    greeting: "Welcome",
                    unauthorized: "Please sign in"
                },
                goodbyePage: {
                    goodbye: "You have successfully been logged out",
                    stillSignedIn: "You are still signed in"
                },
                mfaAuthenticationPage: {
                    title: "MFA Authentication",
                    text: "For your safety, MFA authentication is required",
                    timeLimit: "Authentication will expire at",
                    expired: "Expired. Please Sign in again"
                },
                consentPage: {
                    unauthorized: "Please sign in"
                }
            },
            authentication: {
                authenticationForms: {
                    title: "Sign in by SNS account"
                },
                forms: {
                    username: {
                        passwordRecovery: {
                            done: "Password reset successfully. Redirecting to home page",
                            submit: "Reset password",
                            type: {
                                mobilePhoneNumber: "By my phone",
                                email: "By my email"
                            },
                            input: {
                                principal: {
                                    placeholder: "Input your login name",
                                    invalid: "Input your login name"
                                },
                                sendCode: "Send",
                                code: {
                                    placeholder: "Input code we sent to you",
                                    invalid: "Input code we sent to you"
                                },
                                password: {
                                    placeholder: "Input your new password",
                                    invalid: "Input your new password"
                                },
                                confirm: {
                                    placeholder: "Confirm your new password",
                                    invalid: "Please input your new password again",
                                    notMatch: "Password not match"
                                },
                            }
                        },
                        title: "Use password",
                        input: {
                            username: {
                                placeholder: "Input your username",
                                invalid: "Input your username"
                            },
                            password: {
                                placeholder: "Input your password",
                                invalid: "Input your password"
                            }
                        },
                        submit: "Sign in",
                        forgetPassword: "Forget password？"
                    },
                    sms: {
                        submit: "Sign in",
                        title: "Sms Code",
                        input: {
                            mobilePhoneNumber: {
                                placeholder: "Input your mobile phone number",
                                invalid: "Input your mobile phone number"
                            },
                            code: {
                                placeholder: "Input the code",
                                invalid: "Input the code"
                            }
                        },
                        requestCode: "Send"
                    }
                }
            }
        },
        authorization: {
            claim: "requesting to access your private information",
            notice: "If authorize, it can access",
            scope: {
                profile: "Your profile(avatar, nickname...)"
            }
        }
    }
}


export default messages