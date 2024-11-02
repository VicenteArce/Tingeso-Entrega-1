import axios from "axios";

const prestaBancoServer = "prestabanco-tingeso-app.westus2.cloudapp.azure.com";

export default axios.create({
    baseURL: `http://${prestaBancoServer}`,
    headers: {
        "Content-type": "application/json"
    }
});
