import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import IconButton from "@mui/material/IconButton";
import MenuIcon from "@mui/icons-material/Menu";
import { useState, useEffect } from "react";
import Sidemenu from "./Sidemenu";
import { useNavigate } from "react-router-dom";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import DialogTitle from "@mui/material/DialogTitle";

export default function Navbar() {
    const [open, setOpen] = useState(false);
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [logoutDialogOpen, setLogoutDialogOpen] = useState(false);

    const toggleDrawer = (open) => (event) => {
        setOpen(open);
    };

    const navigate = useNavigate();

    useEffect(() => {
        const userId = localStorage.getItem("userId");
        setIsLoggedIn(!!userId); // Si existe userId, está logueado

        // Escuchar cambios en el localStorage
        const handleStorageChange = () => {
            const updatedUserId = localStorage.getItem("userId");
            setIsLoggedIn(!!updatedUserId);
        };

        window.addEventListener('storage', handleStorageChange);

        // Limpiar el listener cuando el componente se desmonta
        return () => {
            window.removeEventListener('storage', handleStorageChange);
        };
    }, []);

    const handleLogoutClick = () => {
        setLogoutDialogOpen(true);
    };

    const handleConfirmLogout = () => {
        localStorage.removeItem("userId");
        localStorage.removeItem("userRole");
        setIsLoggedIn(false);
        setLogoutDialogOpen(false);
        navigate("/Login");
    };

    const handleCloseLogoutDialog = () => {
        setLogoutDialogOpen(false);
    };

    return (
        <Box sx={{ flexGrow: 1 }}>
            <AppBar position="static">
                <Toolbar>
                    <IconButton
                        size="large"
                        edge="start"
                        color="inherit"
                        aria-label="menu"
                        sx={{ mr: 2 }}
                        onClick={toggleDrawer(true)}
                    >
                        <MenuIcon />
                    </IconButton>

                    <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                        PrestaBanco
                    </Typography>

                    {isLoggedIn ? (
                        <Button color="inherit" onClick={handleLogoutClick}>
                            Logout
                        </Button>
                    ) : (
                        <>
                            <Button color="inherit" onClick={() => navigate("/Login")}>Login</Button>
                            <Button color="inherit" onClick={() => navigate("/Register")}>Register</Button>
                        </>
                    )}
                </Toolbar>
            </AppBar>

            <Sidemenu open={open} toggleDrawer={toggleDrawer}></Sidemenu>

            <Dialog
                open={logoutDialogOpen}
                onClose={handleCloseLogoutDialog}
                aria-labelledby="logout-dialog-title"
                aria-describedby="logout-dialog-description"
            >
                <DialogTitle id="logout-dialog-title">{"¿Seguro que quieres cerrar sesión?"}</DialogTitle>
                <DialogContent>
                    <DialogContentText id="logout-dialog-description">
                        Si cierras sesión, tendrás que volver a ingresar tus credenciales para acceder nuevamente.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseLogoutDialog} color="primary">
                        Cancelar
                    </Button>
                    <Button onClick={handleConfirmLogout} color="primary" autoFocus>
                        Cerrar sesión
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
}
