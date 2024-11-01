import axios from "axios";

const prestaBancoServer = "20.191.125.13";
const prestaBancoPort = "80";

export default axios.create({
    baseURL: `http://${prestaBancoServer}:80`,
    headers: {
        "Content-type": "application/json"
    }
});
