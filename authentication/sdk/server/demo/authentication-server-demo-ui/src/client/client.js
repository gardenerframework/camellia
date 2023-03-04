import basicAxiosProxy from "@/xhr/axios-aop";

const Client = {
    clientId: null,
    name: null,
    description: null,
    logo: null,
    reload: function (clientId) {
        basicAxiosProxy.get("/api/client/" + clientId).then(
            response => {
                let data = response.data;
                this.clientId = data.clientId;
                this.name = data.name;
                this.logo = data.logo;
                this.description = data.description;
            }
        ).catch(
            (error) => {
                if (error.response.data.status === 401) {
                    this.clientId = null;
                    this.name = null;
                    this.logo = null;
                    this.description = null;
                }
            }
        )
    }
}

export default Client;