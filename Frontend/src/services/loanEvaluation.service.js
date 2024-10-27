import httpCommon from "../http-common";

const createLoanEvaluation = loanEvaluation => {
    return httpCommon.post("/PrestaBanco/loanEvaluation/createLoanEvaluation", loanEvaluation);
}

export default {createLoanEvaluation};