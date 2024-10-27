import httpCommon from "../http-common";

const getLoanRequestById = (loanRequestId) => {
    return httpCommon.get(`/PrestaBanco/loanRequest/getLoanRequestById/${loanRequestId}`);
}

const getClientRequests = (userId) => {
    return httpCommon.get(`/PrestaBanco/loanRequest/getClientRequests/${userId}`);
}

const getLoanRequestsByStatus = loanStatus => {
    return httpCommon.get(`/PrestaBanco/loanRequest/getLoanRequestsByStatus/${loanStatus}`);
}

const getLoanRequests = () => {
    return httpCommon.get(`/PrestaBanco/loanRequest/getLoanRequests`);
}

const simulateMortgage = (loanAmount, propertyValue, loan_time, loan_type) => {
    return httpCommon.post(`/PrestaBanco/loanRequest/simulateMortgage/${loanAmount}/${propertyValue}/${loan_time}/${loan_type}`);
}

const calculateTotalCost = loanRequest =>{
    return httpCommon.post("/PrestaBanco/loanRequest/calculateTotal", loanRequest);
}

const requestMortgage = loanRequest => {
    return httpCommon.post("/PrestaBanco/loanRequest/requestMortgage", loanRequest);
}

const evaluateLoanRequest = loanEvaluation =>{
    return httpCommon.put("/PrestaBanco/loanRequest/evaluateLoanRequest", loanEvaluation);
}

const acceptConditions = (loanRequestId, response) => {
    return httpCommon.put(`/PrestaBanco/loanRequest/acceptConditions/${loanRequestId}/${response}`);
}

export default {getLoanRequestById, getClientRequests, getLoanRequestsByStatus, getLoanRequests, simulateMortgage, calculateTotalCost, requestMortgage, evaluateLoanRequest, acceptConditions};