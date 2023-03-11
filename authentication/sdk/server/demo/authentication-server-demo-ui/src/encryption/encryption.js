import basicAxiosProxy from "@/xhr/axios-aop";

const Encryption = {
    getKey: function () {
        let storedData = localStorage.getItem("encryption-key");
        if (!storedData) {
            return null;
        }
        let data = JSON.parse(storedData);
        if (!data || new Date(data.expiryTime) < new Date()) {
            //过期存储移除
            localStorage.removeItem("encryption-key")
            return null;
        } else {
            return data
        }
    },
    reload: function () {
        basicAxiosProxy.post("/api/security/encryption/key").then(
            response => {
                localStorage.setItem("encryption-key", JSON.stringify(response.data))
            }
        )
    }
}

export default Encryption;