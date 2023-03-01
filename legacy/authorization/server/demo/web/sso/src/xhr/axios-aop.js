import axios from "axios";
import {Notification} from 'element-ui'
import router from "@/router";

const basicAxiosProxy = axios.create();
basicAxiosProxy.interceptors.response.use(
    response => {
        return response
    },
    error => {
        const response = error.response;
        if (response.data !== undefined && response.data.error !== undefined) {
            if (response.data.status !== 401) {
                Notification.error({
                    title: "出错啦",
                    message: response.data.message
                })
            }
        } else {
            Notification.error({
                title: "出错啦",
                message: error
            })
            router.push({
                name: "error",
                query: {
                    status: error.response.status,
                    phrase: error.response.statusText
                }
            })
        }
        return Promise.reject(error);
    }
)
export default basicAxiosProxy