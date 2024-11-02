import { useState, useEffect } from "react";
import { useNavigate } from 'react-router-dom';
import loanRequestService from "../services/loanRequest.service";
import loanRequestDocumentsService from "../services/documents.service";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import InputLabel from "@mui/material/InputLabel";
import Alert from "@mui/material/Alert";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import Typography from "@mui/material/Typography";
import Grid from "@mui/material/Grid"; // Importar Grid

const Loanrequest = () => {
    const [loanAmount, setLoanAmount] = useState('');
    const [propertyValue, setPropertyValue] = useState('');
    const [loanTime, setLoanTime] = useState('');
    const [loanType, setLoanType] = useState('');
    const [requiredFiles, setRequiredFiles] = useState({});
    const [uploadedFiles, setUploadedFiles] = useState({});
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null);
    const [loanRequestId, setLoanRequestId] = useState(null);
    const [userId, setUserId] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const storedUserId = localStorage.getItem("userId");
        if (storedUserId) {
            setUserId(storedUserId);
        }
    }, []);

    const handleLoanTypeChange = (e) => {
        const selectedLoanType = e.target.value;
        setLoanType(selectedLoanType);
        switch (selectedLoanType) {
            case '1':
                setRequiredFiles({
                    incomeProof: 'Comprobante de ingresos',
                    appraisalCertificate: 'Certificado de avalúo',
                    creditHistory: 'Historial crediticio'
                });
                break;
            case '2':
                setRequiredFiles({
                    incomeProof: 'Comprobante de ingresos',
                    appraisalCertificate: 'Certificado de avalúo',
                    deedFirstHome: 'Escritura de la primera vivienda',
                    creditHistory: 'Historial crediticio'
                });
                break;
            case '3':
                setRequiredFiles({
                    financialStatement: 'Estado financiero del negocio',
                    incomeProof: 'Comprobante de ingresos',
                    appraisalCertificate: 'Certificado de avalúo',
                    businessPlan: 'Plan de negocios'
                });
                break;
            case '4':
                setRequiredFiles({
                    incomeProof: 'Comprobante de ingresos',
                    renovationBudget: 'Presupuesto de la remodelación',
                    updatedAppraisal: 'Certificado de avalúo actualizado'
                });
                break;
            default:
                setRequiredFiles({});
        }
        setUploadedFiles({});
    };

    const formatNumber = (value) => {
        return value.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
    };

    const handleAmountChange = (e, setFunction) => {
        const rawValue = e.target.value.replace(/\./g, '');
        if (!isNaN(rawValue)) {
            setFunction(formatNumber(rawValue));
        }
    };

    const handleFileChange = (e, fileType) => {
        const file = e.target.files[0];
        setUploadedFiles((prev) => ({
            ...prev,
            [fileType]: file
        }));
    };

    const renderUploadedFiles = () => {
        return (
            <List>
                {Object.keys(uploadedFiles).map((fileType) => (
                    <ListItem key={fileType}>
                        <ListItemText
                            primary={requiredFiles[fileType]}
                            secondary={uploadedFiles[fileType] ? uploadedFiles[fileType].name : "No se ha seleccionado un archivo"}
                        />
                    </ListItem>
                ))}
            </List>
        );
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!loanAmount || !propertyValue || !loanTime || !loanType) {
            setError('Por favor, llena todos los campos');
            return;
        }

        const missingFiles = Object.keys(requiredFiles).filter(fileType => !uploadedFiles[fileType]);
        if (missingFiles.length > 0) {
            setError('Por favor, sube todos los archivos requeridos');
            return;
        }

        const loanRequest = {
            loanAmount: loanAmount.replace(/\./g, ''),
            propertyValue: propertyValue.replace(/\./g, ''),
            loanTime,
            loanType,
            userId
        };

        try {
            setError(null);
            setSuccessMessage(null);

            if (!userId) {
                setError('Debes iniciar sesión para enviar la solicitud.');
                return;
            }

            const response = await loanRequestService.requestMortgage(loanRequest);
            setLoanRequestId(response.data.loanRequestId);

            for (const fileType in uploadedFiles) {
                const formData = new FormData();
                formData.append('userId', userId);
                formData.append('file', uploadedFiles[fileType]);
                formData.append('loanRequestId', response.data.loanRequestId);
                formData.append('fileType', fileType);
                await loanRequestDocumentsService.saveLoanRequestDocument(formData);
            }

            setSuccessMessage('Solicitud enviada correctamente');
            setLoanAmount('');
            setPropertyValue('');
            setLoanTime('');
            setLoanType('');

            setTimeout(() => {
                navigate('/');
            }, 2000);
        } catch (error) {
            setError('Hubo un problema al enviar la solicitud.');
        }
    };

    return (
        <Box component="form" onSubmit={handleSubmit} noValidate autoComplete="off">
            <Typography component="h1" variant="h5" gutterBottom className="custom-typography">
                Solicitar préstamo
            </Typography>
            <Grid container spacing={3}>
                <Grid item xs={12} md={6}>
                    <FormControl fullWidth margin="normal">
                        <InputLabel>Tipo de préstamo</InputLabel>
                        <Select value={loanType} onChange={handleLoanTypeChange}>
                            <MenuItem value="1">Primera Vivienda</MenuItem>
                            <MenuItem value="2">Segunda Vivienda</MenuItem>
                            <MenuItem value="3">Propiedades Comerciales</MenuItem>
                            <MenuItem value="4">Remodelación</MenuItem>
                        </Select>
                    </FormControl>

                    <TextField
                        label="Monto del préstamo"
                        fullWidth
                        margin="normal"
                        value={loanAmount}
                        onChange={(e) => handleAmountChange(e, setLoanAmount)}
                    />

                    <TextField
                        label="Valor de la propiedad"
                        fullWidth
                        margin="normal"
                        value={propertyValue}
                        onChange={(e) => handleAmountChange(e, setPropertyValue)}
                    />

                    <TextField
                        label="Tiempo de préstamo (años)"
                        fullWidth
                        type="number"
                        margin="normal"
                        value={loanTime}
                        onChange={(e) => setLoanTime(e.target.value)}
                    />

                    {error && <Alert severity="error">{error}</Alert>}
                    {successMessage && <Alert severity="success">{successMessage}</Alert>}

                    <Button type="submit" variant="contained" sx={{ mt: 2 }}>
                        Enviar Solicitud
                    </Button>
                </Grid>

                <Grid item xs={12} md={6}>
                    <Typography variant="h6" gutterBottom>
                        Archivos Requeridos
                    </Typography>
                    <Box>
                        {Object.keys(requiredFiles).map((fileType) => (
                            <div
                                key={fileType}
                                style={{
                                    display: "flex",
                                    alignItems: "center",
                                    justifyContent: "space-between",
                                    marginTop: "8px"
                                }}
                            >
                                <label>{requiredFiles[fileType]}</label>
                                 {/* Mostrar nombre del archivo recortado si es muy largo */}
                                {uploadedFiles[fileType] && (
                                    <Typography
                                        variant="body2"
                                        sx={{
                                            mx: 2,
                                            color: "#646cff",
                                            overflow: "hidden",
                                            textOverflow: "ellipsis",
                                            whiteSpace: "nowrap",
                                            maxWidth: "150px"
                                        }}
                                        title={uploadedFiles[fileType].name} // Tooltip con nombre completo
                                    >
                                        {uploadedFiles[fileType].name}
                                    </Typography>
                                )}
                                <Button
                                    variant="outlined"
                                    component="label"
                                    startIcon={<CloudUploadIcon />}
                                    sx={{ mt: 1, mb: 2 }}
                                >
                                    Adjuntar archivo
                                    <input
                                        type="file"
                                        hidden
                                        onChange={(e) => handleFileChange(e, fileType)}
                                    />
                                </Button>
                            </div>
                        ))}
                    </Box>
                </Grid>
            </Grid>
        </Box>
    );
};

export default Loanrequest;
