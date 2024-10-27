import * as React from "react";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Box from "@mui/material/Box";
import List from "@mui/material/List";
import ListItemIcon from "@mui/material/ListItemIcon";
import HomeIcon from "@mui/icons-material/Home";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemText from "@mui/material/ListItemText";
import Drawer from "@mui/material/Drawer";
import { Divider } from "@mui/material";
import AttachMoneyIcon from "@mui/icons-material/AttachMoney";
import RequestQuoteIcon from "@mui/icons-material/RequestQuote";
import PlaylistAddCheckIcon from "@mui/icons-material/PlaylistAddCheck";
import FormatListBulletedIcon from "@mui/icons-material/FormatListBulleted";
import UserService from "../services/user.service";

export default function Sidemenu({ open, toggleDrawer }) {
  const navigate = useNavigate();
  const [userRole, setUserRole] = useState(null);

  useEffect(() => {
    const fetchUserRole = () => {
      const userId = localStorage.getItem("userId");
      if (userId) {
        UserService.getRol(userId)
          .then((response) => {
            setUserRole(response.data);
          })
          .catch((error) => {
            console.error("Error al obtener el rol del usuario:", error);
          });
      } else {
        setUserRole("guest");
      }
    };
  
    // Llama a fetchUserRole al montar el componente
    fetchUserRole();
  
    // Agrega el listener para detectar cambios en el storage
    const handleStorageChange = () => fetchUserRole();
    window.addEventListener("storage", handleStorageChange);
  
    // Limpia el listener al desmontar el componente
    return () => {
      window.removeEventListener("storage", handleStorageChange);
    };
  }, []);

  const handleNavigation = (path) => {
    if (userRole === "guest") {
      alert("Para acceder a este apartado debes estar logueado");
    } else {
      navigate(path);
    }
  };

  const listOptions = () => (
    <Box role="presentation" onClick={toggleDrawer(false)} onKeyDown={toggleDrawer(false)}>
      <List>
        <ListItemButton onClick={() => navigate("/")}>
          <ListItemIcon>
            <HomeIcon />
          </ListItemIcon>
          <ListItemText primary="Home" />
        </ListItemButton>
        <Divider />

        {/* Opciones para el cliente (userRole === 1) o si no está logueado (userRole === "guest") */}
        {(userRole === 1 || userRole === "guest") && (
          <>
            <ListItemButton onClick={() => handleNavigation("/Simulateloan")}>
              <ListItemIcon>
                <AttachMoneyIcon />
              </ListItemIcon>
              <ListItemText primary="Simular Crédito" />
            </ListItemButton>
            <ListItemButton onClick={() => handleNavigation("/Loanrequest")}>
              <ListItemIcon>
                <RequestQuoteIcon />
              </ListItemIcon>
              <ListItemText primary="Solicitar Crédito" />
            </ListItemButton>
            <ListItemButton onClick={() => handleNavigation("/Myrequests")}>
              <ListItemIcon>
                <PlaylistAddCheckIcon />
              </ListItemIcon>
              <ListItemText primary="Mis Solicitudes" />
            </ListItemButton>
          </>
        )}

        {/* Opción Lista de Solicitudes: Solo visible para el ejecutivo (userRole === 0) */}
        {userRole === 0 && (
          <ListItemButton onClick={() => navigate("/Loanlist")}>
            <ListItemIcon>
              <FormatListBulletedIcon />
            </ListItemIcon>
            <ListItemText primary="Lista de Solicitudes" />
          </ListItemButton>
        )}
      </List>
    </Box>
  );

  return (
    <Drawer anchor="left" open={open} onClose={toggleDrawer(false)}>
      {listOptions()}
    </Drawer>
  );
}
