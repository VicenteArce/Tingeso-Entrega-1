import axios from "axios";

const prestaBancoServer = "prestabanco-tingeso-app.westus2.cloudapp.azure.com";
// const prestaBancoServer = "localhost:8090";
// const prestaBancoServer = "localhost:80";
export default axios.create({
    baseURL: `http://${prestaBancoServer}`,
    headers: {
        "Content-type": "application/json"
    }
});
