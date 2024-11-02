import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import Alert from "@mui/material/Alert";
import { FormControlLabel } from "@mui/material";
import Switch from "@mui/material/Switch";
import Tooltip from "@mui/material/Tooltip";
import InfoIcon from "@mui/icons-material/Info";
import loanRequestService from "../services/loanRequest.service";
import documentsService from "../services/documents.service";
import Typography from "@mui/material/Typography";


const Evaluateloan = () => {
    const { loanRequestId } = useParams();
    const [formData, setFormData] = useState({
        loanRequestId: loanRequestId || "",
        dicom: false,
        stability: false,
        savingsHistory: false,
        periodicSavings: false,
        recentsWithdrawals: false,
        savingAntiquity: "",
        monthlyDebt: "",
        savings: ""
    });
    const [alertMessage, setAlertMessage] = useState("");
    const [alertType, setAlertType] = useState("success");
    const [evaluationResult, setEvaluationResult] = useState("");
    const [documents, setDocuments] = useState([]);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData({
            ...formData,
            [name]: type === "checkbox" ? checked : value,
        });
    };

    const documentTypeTranslations = {
        incomeProof: "Comprobante de ingresos",
        appraisalCertificate: "Certificado de avalúo",
        creditHistory: "Historial crediticio",
        deedFirstHome: "Escritura de la primera vivienda",
        businessPlan: "Plan de negocios",
        financialStatement: "Estado financiero del negocio",
        renovationBudget: "Presupuesto de la remodelación",
        updatedAppraisal: "Certificado de avalúo actualizado"
    };
    
    const fetchDocuments = async () => {
        try {
            const response = await documentsService.getDocumentsByLoanRequestId(loanRequestId);
            const documentUrls = response.data.map(doc => {
                const byteCharacters = atob(doc.document);
                const byteNumbers = Array.from(byteCharacters, char => char.charCodeAt(0));
                const byteArray = new Uint8Array(byteNumbers);
                const blob = new Blob([byteArray], { type: 'application/pdf' });
                return { url: window.URL.createObjectURL(blob), type: doc.type };
            });
            setDocuments(documentUrls);
        } catch (error) {
            console.error("Error al obtener los documentos:", error);
            setAlertMessage("Hubo un error al obtener los documentos.");
            setAlertType("error");
        }
    };

    const handleEvaluateLoan = async (e) => {
        e.preventDefault();
        try {
            const response = await loanRequestService.evaluateLoanRequest(formData);
            const loanStatus = response.data.loanStatus;

            if (loanStatus === 2) {
                setEvaluationResult("Falta de documentación.");
                setAlertType("warning");
            } else if (loanStatus === 4) {
                setEvaluationResult("Préstamo Pre-Aprobado.");
                setAlertType("success");
            } else if (loanStatus === 7) {
                setEvaluationResult("Préstamo Rechazado.");
                setAlertType("error");
            }
        } catch (error) {
            console.error("Error al evaluar el crédito:", error);
            setAlertMessage("Error al enviar la evaluación del crédito.");
            setAlertType("error");
        }
    };

    useEffect(() => {
        if (loanRequestId) {
            fetchDocuments();
        }
    }, [loanRequestId]);

    return (
        <Box component="form" onSubmit={handleEvaluateLoan} sx={{ p: 3, maxWidth: 600, margin: "auto" }}>
            <Typography variant="h4" gutterBottom className="custom-typography">
                Evaluar Solicitud de Crédito
            </Typography>
            {alertMessage && <Alert severity={alertType}>{alertMessage}</Alert>}
            <TextField
                label="ID de Solicitud de Crédito"
                name="loanRequestId"
                value={formData.loanRequestId}
                onChange={handleChange}
                fullWidth
                required
                margin="normal"
                disabled
            />

            <FormControlLabel
                control={
                    <Switch
                        name="dicom"
                        checked={formData.dicom}
                        onChange={handleChange}
                    />
                }
                label={
                    <Box sx={{ display: "flex", alignItems: "center" }}>
                        <span>¿Está en Dicom?</span>
                        <Tooltip title="Indica si el solicitante está en el sistema Dicom.">
                            <InfoIcon sx={{ ml: 1, color: "info.main" }} />
                        </Tooltip>
                    </Box>
                }
            />

            <FormControlLabel
                control={
                    <Switch
                        name="stability"
                        checked={formData.stability}
                        onChange={handleChange}
                    />
                }
                label={
                    <Box sx={{ display: "flex", alignItems: "center" }}>
                        <span>Estabilidad</span>
                        <Tooltip title="Evalúa la estabilidad laboral del solicitante.">
                            <InfoIcon sx={{ ml: 1, color: "info.main" }} />
                        </Tooltip>
                    </Box>
                }
            />

            <FormControlLabel
                control={
                    <Switch
                        name="savingsHistory"
                        checked={formData.savingsHistory}
                        onChange={handleChange}
                    />
                }
                label={
                    <Box sx={{ display: "flex", alignItems: "center" }}>
                        <span>Historial de Ahorro</span>
                        <Tooltip title="Indica si el solicitante tiene un historial de ahorro.">
                            <InfoIcon sx={{ ml: 1, color: "info.main" }} />
                        </Tooltip>
                    </Box>
                }
            />

            <FormControlLabel
                control={
                    <Switch
                        name="periodicSavings"
                        checked={formData.periodicSavings}
                        onChange={handleChange}
                    />
                }
                label={
                    <Box sx={{ display: "flex", alignItems: "center" }}>
                        <span>Ahorros Periódicos</span>
                        <Tooltip title="Indica si el solicitante realiza ahorros periódicos.">
                            <InfoIcon sx={{ ml: 1, color: "info.main" }} />
                        </Tooltip>
                    </Box>
                }
            />

            <FormControlLabel
                control={
                    <Switch
                        name="recentsWithdrawals"
                        checked={formData.recentsWithdrawals}
                        onChange={handleChange}
                    />
                }
                label={
                    <Box sx={{ display: "flex", alignItems: "center" }}>
                        <span>Retiros Recientes</span>
                        <Tooltip title="Indica si el solicitante ha realizado retiros recientemente.">
                            <InfoIcon sx={{ ml: 1, color: "info.main" }} />
                        </Tooltip>
                    </Box>
                }
            />

            <TextField
                label="Antigüedad de cuenta de ahorro (años)"
                name="savingAntiquity"
                type="number"
                value={formData.savingAntiquity}
                onChange={handleChange}
                fullWidth
                required
                margin="normal"
                InputProps={{
                    endAdornment: (
                        <Tooltip title="Indique la cantidad de años que ha tenido su cuenta de ahorro.">
                            <InfoIcon color="info" />
                        </Tooltip>
                    ),
                }}
            />

            <TextField
                label="Deuda Mensual"
                name="monthlyDebt"
                type="number"
                value={formData.monthlyDebt}
                onChange={handleChange}
                fullWidth
                required
                margin="normal"
                InputProps={{
                    endAdornment: (
                        <Tooltip title="Monto total de deuda mensual del solicitante.">
                            <InfoIcon color="info" />
                        </Tooltip>
                    ),
                }}
            />

            <TextField
                label="Ahorros"
                name="savings"
                type="number"
                value={formData.savings}
                onChange={handleChange}
                fullWidth
                required
                margin="normal"
                InputProps={{
                    endAdornment: (
                        <Tooltip title="Total de ahorros actuales del solicitante.">
                            <InfoIcon color="info" />
                        </Tooltip>
                    ),
                }}
            />

            <Typography variant="h6" sx={{ mt: 3 }}>Documentos</Typography>
            {documents.map((doc, index) => (
                <Box key={index} sx={{ display: "flex", alignItems: "center", mt: 1, justifyContent:"space-between"}}>
                    <Typography sx={{ mr: 2 }}>{documentTypeTranslations[doc.type] || doc.type}</Typography>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => window.open(doc.url)}
                    >
                        Descargar
                    </Button>
                </Box>
            ))}

            <Button variant="contained" color="primary" type="submit" fullWidth sx={{ mt: 3 }}>
                Evaluar Crédito
            </Button>

            {evaluationResult && (
                <Alert severity={alertType} sx={{ mt: 3 }}>
                    {evaluationResult}
                </Alert>
            )}
        </Box>
    );
};

export default Evaluateloan;
