import React, { useState } from 'react';
import { TextField, Button, Box, Typography, Container, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from '@mui/material';
import { useNavigate } from 'react-router-dom'; // Para redirigir
import userService from '../services/user.service';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [open, setOpen] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = (e) => {
        e.preventDefault();

        if (!email || !password) {
            setError('Por favor, llena todos los campos');
            return;
        }

        const user = { email, password };

        userService.login(user)
            .then((response) => {
                // Guarda el userId en el localStorage y notifica otros componentes
                localStorage.setItem('userId', response.data.userId);
                window.dispatchEvent(new Event('storage')); // Lanza el evento para notificar a otros componentes

                // Redirige al usuario al inicio
                navigate('/');
            })
            .catch((error) => {
                console.log('Error al iniciar sesión', error);
                setOpen(true);
            });

        setError('');
    };

    const handleClose = () => {
        setOpen(false);
    };

    return (
        <Container component="main" maxWidth="xs">
            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginTop: 8 }}>
                <Typography component="h1" variant="h5" className="custom-typography">
                    Iniciar Sesión
                </Typography>
                <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3 }}>
                    <TextField
                        variant="outlined"
                        margin="normal"
                        required
                        fullWidth
                        id="email"
                        label="Correo Electrónico"
                        name="email"
                        autoComplete="email"
                        autoFocus
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />
                    <TextField
                        variant="outlined"
                        margin="normal"
                        required
                        fullWidth
                        name="password"
                        label="Contraseña"
                        type="password"
                        id="password"
                        autoComplete="current-password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        
                    />
                    {error && (
                        <Typography color="error" variant="body2">{error}</Typography>
                    )}
                    <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2 }}>Iniciar Sesión</Button>
                </Box>
            </Box>

            <Dialog open={open} onClose={handleClose}>
                <DialogTitle>Error</DialogTitle>
                <DialogContent>
                    <DialogContentText>No se pudo iniciar sesión. Por favor, verifica tu correo y contraseña.</DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose} color="primary" autoFocus>Cerrar</Button>
                </DialogActions>
            </Dialog>
        </Container>
    );
};

export default Login;
