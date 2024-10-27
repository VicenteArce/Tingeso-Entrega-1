import { useState } from "react";
import loanRequestService from "../services/loanRequest.service";
import Box from "@mui/material/Box";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import FormControl from "@mui/material/FormControl";
import MenuItem from "@mui/material/MenuItem";
import SaveIcon from "@mui/icons-material/Save";
import Select from "@mui/material/Select";
import InputLabel from "@mui/material/InputLabel";
import Alert from "@mui/material/Alert";
import IconButton from "@mui/material/IconButton";
import Tooltip from "@mui/material/Tooltip";
import InfoIcon from "@mui/icons-material/Info";
import Popover from "@mui/material/Popover";
import SendIcon from '@mui/icons-material/Send';
import Typography from "@mui/material/Typography";

const Simulateloan = () => {
    const [loanAmount, setLoanAmount] = useState('');
    const [propertyValue, setPropertyValue] = useState('');
    const [loanTime, setLoanTime] = useState('');
    const [loanType, setLoanType] = useState('');
    const [error, setError] = useState(null);
    const [fieldError, setFieldError] = useState({}); // Para marcar los campos con error
    const [simulationResult, setSimulationResult] = useState(null);

    // Para el popover de información
    const [anchorEl, setAnchorEl] = useState(null);

    const handleInfoClick = (event) => {
        setAnchorEl(event.currentTarget);
    };

    const handleInfoClose = () => {
        setAnchorEl(null);
    };

    const open = Boolean(anchorEl);
    const id = open ? 'simple-popover' : undefined;

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

    const handleSubmit = (e) => {
        e.preventDefault();

        // Validación preliminar
        if (!loanAmount || !propertyValue || !loanTime || !loanType) {
            alert('Por favor, llena todos los campos');
            return;
        }

        loanRequestService.simulateMortgage(loanAmount.replace(/\./g, ''), propertyValue.replace(/\./g, ''), loanTime, loanType)
            .then((response) => {
                const result = response.data;

                // Manejo de los diferentes casos de error
                if (result === -1) {
                    setError('Valores negativos o cero no son válidos.');
                    setFieldError({
                        loanAmount: loanAmount <= 0,
                        propertyValue: propertyValue <= 0,
                        loanTime: loanTime <= 0
                    });
                } else if (result === -2) {
                    setError('Tipo de préstamo inválido.');
                    setFieldError({ loanType: true });
                } else if (result === -3) {
                    setError('El plazo del préstamo excede el máximo permitido.');
                    setFieldError({ loanTime: true });
                } else if (result === -4) {
                    setError('El monto del préstamo excede el máximo permitido para financiar.');
                    setFieldError({ loanAmount: true });
                } else {
                    // Si no hay errores, se muestra el resultado
                    setSimulationResult(`Valor de la cuota mensual: ${result.toFixed(2)}`);
                    setError(null);
                    setFieldError({});
                }
            })
            .catch((error) => {
                console.log('Error al simular el préstamo', error);
                alert('Error al simular el préstamo');
            });
    };

    return (
        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3 }}>
            <Typography variant="h4" gutterBottom>
                Simular Préstamo
            </Typography>
            {/* Ícono de información en la esquina superior derecha */}
            <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
                <Tooltip title="Requisitos de simulación">
                    <IconButton onClick={handleInfoClick}>
                        <InfoIcon />
                    </IconButton>
                </Tooltip>
            </Box>

            <Popover
                id={id}
                open={open}
                anchorEl={anchorEl}
                onClose={handleInfoClose}
                anchorOrigin={{
                    vertical: 'bottom',
                    horizontal: 'right',
                }}
            >
                <Typography sx={{ p: 2 }}>
                    <strong>Requisitos de Simulación:</strong><br />
                    - Monto del préstamo debe ser mayor que 0.<br />
                    - Valor de la propiedad debe ser mayor que 0.<br />
                    - El tiempo máximo del préstamo depende del tipo de propiedad:<br />
                    &nbsp;&nbsp;1. Primera propiedad: máximo 30 años.<br />
                    &nbsp;&nbsp;2. Segunda propiedad: máximo 20 años.<br />
                    &nbsp;&nbsp;3. Propiedad comercial: máximo 25 años.<br />
                    &nbsp;&nbsp;4. Remodelación: máximo 15 años.<br />
                    - Para primera propiedad: el monto del préstamo no puede exceder el 80% del valor de la propiedad.<br />
                    - Para segunda propiedad: el monto del préstamo no puede exceder el 70% del valor de la propiedad.<br />
                    - Para propiedad comercial: el monto del préstamo no puede exceder el 60% del valor de la propiedad.<br />
                    - Para remodelación: el monto del préstamo no puede exceder el 50% del valor de la propiedad.<br />
                </Typography>
            </Popover>

            <FormControl fullWidth margin="normal">
                <TextField
                    label="Monto del préstamo"
                    type="text"
                    value={loanAmount}
                    onChange={(e) => handleAmountChange(e, setLoanAmount)}
                    error={!!fieldError.loanAmount}
                    helperText={fieldError.loanAmount && "El monto es inválido"}
                />
            </FormControl>

            <FormControl fullWidth margin="normal">
                <TextField
                    label="Valor de la propiedad"
                    type="text"
                    value={propertyValue}
                    onChange={(e) => handleAmountChange(e, setPropertyValue)}
                    error={!!fieldError.propertyValue}
                    helperText={fieldError.propertyValue && "El valor de la propiedad es inválido"}
                />
            </FormControl>

            <FormControl fullWidth margin="normal">
                <TextField
                    label="Tiempo del préstamo (años)"
                    type="number"
                    value={loanTime}
                    onChange={(e) => setLoanTime(e.target.value)}
                    error={!!fieldError.loanTime}
                    helperText={fieldError.loanTime && "El tiempo excede el permitido para el tipo de propiedad"}
                />
            </FormControl>

            <FormControl fullWidth margin="normal">
                <InputLabel>Tipo de préstamo</InputLabel>
                <Select
                    value={loanType}
                    onChange={(e) => setLoanType(e.target.value)}
                    error={!!fieldError.loanType}
                >
                    <MenuItem value={1}>Primera propiedad</MenuItem>
                    <MenuItem value={2}>Segunda propiedad</MenuItem>
                    <MenuItem value={3}>Propiedad comercial</MenuItem>
                    <MenuItem value={4}>Remodelación</MenuItem>
                </Select>
                {fieldError.loanType && <Alert severity="error">El tipo de préstamo es inválido</Alert>}
            </FormControl>

            <Button type="submit" variant="contained" startIcon={<SendIcon />}>
                Simular
            </Button>

            {error && <Alert severity="error" sx={{ mt: 2 }}>{error}</Alert>}
            {simulationResult && <Alert severity="success" sx={{ mt: 2 }}>{simulationResult}</Alert>}
        </Box>
    );
};

export default Simulateloan;