import httpCommon from "../http-common";


const getUserById = (id) => {
    return httpCommon.get(`/PrestaBanco/user/getUser/${id}`);
}

const getUserByRut = (rut) => {
    return httpCommon.get(`/PrestaBanco/user/getUserByRut/${rut}`);
}

const saveUser = (user) => {
    return httpCommon.post("/PrestaBanco/user/saveUser", user);
}

const login = user => {
    return httpCommon.post("/PrestaBanco/user/login", user);
}

const getRol = userId => {
    return httpCommon.get(`/PrestaBanco/user/getRol/${userId}`);
}

export default {getUserById, getUserByRut, saveUser, login, getRol};