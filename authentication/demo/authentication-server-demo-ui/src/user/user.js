import basicAxiosProxy from "@/xhr/axios-aop";

const User = {
    name: null,
    avatar: null,
    reload: function (onSuccess) {
        basicAxiosProxy.get("/api/me").then(
            response => {
                let data = response.data;
                this.name = data.name;
                this.avatar = data.avatar;
                if (onSuccess) {
                    onSuccess(data)
                }
            }
        ).catch(
            (error) => {
                if (error.response.data.status === 401) {
                    this.name = null
                    this.avatar = null
                }
            }
        )
    }
}

export default User;