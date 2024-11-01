import axios from "axios";

const prestaBancoServer = "localhost";
const prestaBancoPort = "80";

export default axios.create({
    baseURL: `http://${prestaBancoServer}:80`,
    headers: {
        "Content-type": "application/json"
    }
});
