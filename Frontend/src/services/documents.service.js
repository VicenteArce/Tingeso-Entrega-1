import httpCommon from "../http-common"; // Asegúrate de que httpCommon esté configurado correctamente para el backend

// Función para subir un documento de solicitud de préstamo
const saveLoanRequestDocument = (formData) => {
    return httpCommon.post("/PrestaBanco/loanRequestDocument/upload", formData, {
        headers: {
            'Content-Type': 'multipart/form-data', // Necesario para enviar archivos
        }
    });
};

// Función para obtener todos los documentos asociados a un loanRequestId específico
const getDocumentsByLoanRequestId = (loanRequestId) => {
    return httpCommon.get(`/PrestaBanco/loanRequestDocument/loanRequest/${loanRequestId}`);
};

export default {saveLoanRequestDocument,getDocumentsByLoanRequestId};
