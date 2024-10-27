import axios from "axios";

const prestaBancoServer = import.meta.env.VITE_PRESTABANCO_BACKEND_SERVER;
const prestaBancoPort = import.meta.env.VITE_PRESTABANCO_BACKEND_PORT;

export default axios.create({
    baseURL: `http://${prestaBancoServer}:${prestaBancoPort}`,
    headers: {
        "Content-type": "application/json"
    }
});
