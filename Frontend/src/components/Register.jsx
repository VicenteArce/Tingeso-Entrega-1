import React, { useState } from 'react';
import { TextField, Button, Box, Typography, Container, MenuItem } from '@mui/material';
import userService from '../services/user.service';

const Register = () => {
    const [rut, setRut] = useState('');
    const [name, setName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [monthlyIncome, setMonthlyIncome] = useState('');
    const [rol, setRol] = useState('');
    const [birthDay, setBirthDay] = useState('');
    const [birthMonth, setBirthMonth] = useState('');
    const [birthYear, setBirthYear] = useState('');
    const [error, setError] = useState('');

    const currentYear = new Date().getFullYear();

    // Función para validar que solo se ingresen números y la letra K (sin puntos)
    const handleRutChange = (e) => {
        let value = e.target.value.replace(/[^\dKk]/g, ''); // Solo números y k/K
        if (value.length > 9) return;
        setRut(value);
    };

    // Función para formatear el RUT antes de enviar
    const formatRutForSubmission = (rut) => {
        // Agregar guion antes del último carácter si no existe
        return rut.length > 1 && !rut.includes('-')
            ? rut.slice(0, -1) + '-' + rut.slice(-1)
            : rut;
    };

    // Validación del número máximo de días según el mes y si es año bisiesto
    const isLeapYear = (year) => year % 4 === 0 && (year % 100 !== 0 || year % 400 === 0);
    const getDaysInMonth = (month, year) => {
        const daysInMonth = [31, isLeapYear(year) ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
        return daysInMonth[month - 1] || 31;
    };

    // Validación de día
    const handleBirthDayChange = (e) => {
        const day = e.target.value.replace(/\D/g, '');
        const maxDays = getDaysInMonth(parseInt(birthMonth, 10), parseInt(birthYear, 10));
        if (day > maxDays) return;
        setBirthDay(day.slice(0, 2));
    };

    // Validación de mes (1-12)
    const handleBirthMonthChange = (e) => {
        const month = e.target.value.replace(/\D/g, '').slice(0, 2);
        if (parseInt(month, 10) > 12) return;
        setBirthMonth(month);
        if (birthDay && parseInt(birthDay, 10) > getDaysInMonth(parseInt(month, 10), parseInt(birthYear, 10))) {
            setBirthDay('');
        }
    };

    // Validación del año (1930 - Año Actual)
    const handleBirthYearChange = (e) => {
        const year = e.target.value.replace(/\D/g, '').slice(0, 4);
    
        // Permitir ingreso parcial hasta alcanzar 4 dígitos
        if (year.length < 4) {
            setBirthYear(year);
            return;
        }
    
        // Aplicar la validación de rango solo cuando el año tiene 4 dígitos
        const yearNumber = parseInt(year, 10);
        if (yearNumber >= 1930 && yearNumber <= currentYear) {
            setBirthYear(year);
        }
    };

    const handleMonthlyIncomeChange = (e) => {
        const value = e.target.value.replace(/\D/g, '');
        setMonthlyIncome(value ? parseInt(value, 10) : '');
    };

    const handleRolChange = (e) => {
        setRol(e.target.value);
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        if (!rut || !name || !lastName || !email || !password || !monthlyIncome || !rol || !birthDay || !birthMonth || !birthYear) {
            setError('Por favor, llena todos los campos');
            return;
        }

        if (monthlyIncome <= 0) {
            setError('El ingreso mensual debe ser mayor a 0');
            return;
        }

        const birthDate = `${birthMonth}-${birthDay}-${birthYear}`;
        const formattedRut = formatRutForSubmission(rut);

        const user = {
            rut: formattedRut,
            name,
            lastName,
            email,
            password,
            monthlyIncome,
            rol,
            birthDate
        };

        userService.saveUser(user)
            .then((response) => {
                console.log("El usuario ha sido registrado de manera exitosa", response);
                setError('');
            })
            .catch((error) => {
                console.log("Error al registrar al usuario", error);
                setError('Error al registrar el usuario');
            });

        // Limpiar el formulario
        setRut('');
        setName('');
        setLastName('');
        setEmail('');
        setPassword('');
        setMonthlyIncome('');
        setRol('');
        setBirthDay('');
        setBirthMonth('');
        setBirthYear('');
    };

    return (
        <Container component="main" maxWidth="xs">
            <Box
                sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    marginTop: 8,
                }}
            >
                <Typography component="h1" variant="h5">
                    Registrarse
                </Typography>
                <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3 }}>
                    <TextField
                        variant="outlined"
                        margin="normal"
                        required
                        fullWidth
                        id="rut"
                        label="Rut"
                        name="rut"
                        autoComplete="rut"
                        autoFocus
                        value={rut}
                        helperText="Rut sin puntos ni guión: xxxxxxxxk"
                        onChange={handleRutChange}
                    />
                    <TextField
                        variant="outlined"
                        margin="normal"
                        required
                        fullWidth
                        id="name"
                        label="Nombre"
                        name="name"
                        value={name}
                        helperText="Ejemplo: Vicente"
                        onChange={(e) => setName(e.target.value)}
                    />
                    <TextField
                        variant="outlined"
                        margin="normal"
                        required
                        fullWidth
                        id="lastName"
                        label="Apellido"
                        name="lastName"
                        value={lastName}
                        helperText="Ejemplo: Arce"
                        onChange={(e) => setLastName(e.target.value)}
                    />
                    <TextField
                        variant="outlined"
                        margin="normal"
                        required
                        fullWidth
                        id="email"
                        label="Correo Electrónico"
                        name="email"
                        value={email}
                        helperText="Ejemplo: vicente.arce.p@usach.cl"
                        onChange={(e) => setEmail(e.target.value)}
                    />
                    <TextField
                        variant="outlined"
                        margin="normal"
                        required
                        fullWidth
                        id="password"
                        label="Contraseña"
                        name="password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    <TextField
                        variant="outlined"
                        margin="normal"
                        required
                        fullWidth
                        id="monthlyIncome"
                        label="Ingreso Mensual"
                        name="monthlyIncome"
                        value={monthlyIncome}
                        helperText="Ejemplo: 500000"
                        onChange={handleMonthlyIncomeChange}
                    />
                    <TextField
                        select
                        variant="outlined"
                        margin="normal"
                        required
                        fullWidth
                        id="rol"
                        label="Rol"
                        name="rol"
                        value={rol}
                        onChange={handleRolChange}
                        helperText="Selecciona tu rol"
                    >
                        <MenuItem value="0">Ejecutivo</MenuItem>
                        <MenuItem value="1">Cliente</MenuItem>
                    </TextField>

                    <Typography variant="subtitle1" sx={{ mt: 2 }}>
                        Fecha de Nacimiento
                    </Typography>
                    <Box display="flex" gap={1} alignItems="center">
                        <TextField
                            variant="outlined"
                            margin="normal"
                            required
                            id="birthDay"
                            label="Día (DD)"
                            name="birthDay"
                            value={birthDay}
                            onChange={handleBirthDayChange}
                        />
                        <TextField
                            variant="outlined"
                            margin="normal"
                            required
                            id="birthMonth"
                            label="Mes (MM)"
                            name="birthMonth"
                            value={birthMonth}
                            onChange={handleBirthMonthChange}
                        />
                        <TextField
                            variant="outlined"
                            margin="normal"
                            required
                            id="birthYear"
                            label="Año (YYYY)"
                            name="birthYear"
                            value={birthYear}
                            onChange={handleBirthYearChange}
                        />
                    </Box>

                    {error && (
                        <Typography color="error" variant="body2">
                            {error}
                        </Typography>
                    )}

                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{ mt: 3, mb: 2 }}
                    >
                        Registrarme
                    </Button>
                </Box>
            </Box>
        </Container>
    );
};

export default Register;
