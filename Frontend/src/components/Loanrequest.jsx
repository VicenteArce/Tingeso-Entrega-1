import { useState, useEffect } from "react";
import { useNavigate } from 'react-router-dom'; // Para redirigir
import loanRequestService from "../services/loanRequest.service"; // Import your service for the HTTP request
import loanRequestDocumentsService from "../services/documents.service";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import InputLabel from "@mui/material/InputLabel";
import FileUpload from "@mui/icons-material/FileUpload";
import Alert from "@mui/material/Alert";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import Typography from "@mui/material/Typography";

const Loanrequest = () => {
    const [loanAmount, setLoanAmount] = useState('');
    const [propertyValue, setPropertyValue] = useState('');
    const [loanTime, setLoanTime] = useState('');
    const [loanType, setLoanType] = useState('');
    const [requiredFiles, setRequiredFiles] = useState({});
    const [uploadedFiles, setUploadedFiles] = useState({});
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null);
    const [loanRequestId, setLoanRequestId] = useState(null); // Añadir estado para loanRequestId
    const [userId, setUserId] = useState(null); // Manejar userId con estado
    const navigate = useNavigate();

    

    // Obtener el userId desde el localStorage al cargar el componente
    useEffect(() => {
        const storedUserId = localStorage.getItem("userId");
        if (storedUserId) {
            setUserId(storedUserId);
        }
    }, []);

    // Handle loan type change and set required files
    const handleLoanTypeChange = (e) => {
        const selectedLoanType = e.target.value;
        setLoanType(selectedLoanType);

        // Set required files based on loan type
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
        setUploadedFiles({}); // Reset uploaded files when loan type changes
    };

    // Formateo con separador de miles
    const formatNumber = (value) => {
        return value.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
    };

    // Función para manejar la entrada y formatear automáticamente los miles
    const handleAmountChange = (e, setFunction) => {
        const rawValue = e.target.value.replace(/\./g, ''); // Eliminar los puntos para tratar el número sin formato
        if (!isNaN(rawValue)) {
            setFunction(formatNumber(rawValue));
        }
    };

    // Handle file upload
    const handleFileChange = (e, fileType) => {
        const file = e.target.files[0];
        setUploadedFiles((prev) => ({
            ...prev,
            [fileType]: file
        }));
    };

    // Render uploaded files list
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

    // Handle form submission
    const handleSubmit = async (e) => {
        e.preventDefault();

        // Validation
        if (!loanAmount || !propertyValue || !loanTime || !loanType) {
            setError('Por favor, llena todos los campos');
            return;
        }

        // Check if all required files are uploaded
        const missingFiles = Object.keys(requiredFiles).filter(fileType => !uploadedFiles[fileType]);
        if (missingFiles.length > 0) {
            setError('Por favor, sube todos los archivos requeridos');
            return;
        }

        const loanRequest = {
            loanAmount: loanAmount.replace(/\./g, ''), // Clean number formatting
            propertyValue: propertyValue.replace(/\./g, ''),
            loanTime,
            loanType,
            userId // Suponiendo que el userId lo tienes disponible
        };

        try {
            // Clear previous messages
            setError(null);
            setSuccessMessage(null);

            // Si no esta logueado, redirigir a login
            if (!userId) {
                setError('Debes iniciar sesión para enviar la solicitud.');
                return;
            }
            // Guarda el loan request y obtiene el loanRequestId
            const response = await loanRequestService.requestMortgage(loanRequest);
            setLoanRequestId(response.data.loanRequestId); // Guarda el loanRequestId
            
            // Guarda los documentos
            for (const fileType in uploadedFiles) {
                const formData = new FormData();
                formData.append('userId', userId);
                formData.append('file', uploadedFiles[fileType]);
                formData.append('loanRequestId', response.data.loanRequestId);
                formData.append('fileType', fileType);
                await loanRequestDocumentsService.saveLoanRequestDocument(formData);
            }

            setSuccessMessage('Solicitud enviada correctamente');
            
            // Hago que el usuario no pueda enviar la solicitud dos veces
            setLoanAmount('');
            setPropertyValue('');
            setLoanTime('');
            setLoanType('');
            
            // Espera 3 segundos antes de redirigir al usuario
            setTimeout(() => {
                navigate('/');
            }, 2000);
        } catch (error) {
            setError('Hubo un problema al enviar la solicitud.');
        }
    };

    return (
        <Box component="form" onSubmit={handleSubmit} noValidate autoComplete="off">
            <Typography variant="h4" gutterBottom>
                Solicitar préstamo
            </Typography>
            <FormControl fullWidth margin="normal">
                <InputLabel>Tipo de préstamo</InputLabel>
                <Select value={loanType} onChange={handleLoanTypeChange}>
                    <MenuItem value="1">Primera Vivienda</MenuItem>
                    <MenuItem value="2">Segunda Vivienda</MenuItem>
                    <MenuItem value="3">Propiedades Comerciales</MenuItem>
                    <MenuItem value="4">Remodelación</MenuItem>
                </Select>
            </FormControl>

            {Object.keys(requiredFiles).length > 0 && (
                <Box>
                    <h4>Archivos Requeridos:</h4>
                    {Object.keys(requiredFiles).map(fileType => (
                        <div key={fileType}>
                            <label>{requiredFiles[fileType]}</label>
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
                    {renderUploadedFiles()}
                </Box>
            )}

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
                margin="normal"
                value={loanTime}
                onChange={(e) => setLoanTime(e.target.value)}
            />

            {error && <Alert severity="error">{error}</Alert>}
            {successMessage && <Alert severity="success">{successMessage}</Alert>}
            
            <Button type="submit" variant="contained" sx={{ mt: 2 }}>
                Enviar Solicitud
            </Button>
        </Box>
    );
};

export default Loanrequest;
