import React, { useEffect, useState } from "react";
import { Navigate, Outlet } from "react-router-dom";
import UserService from "../services/user.service";

export default function PrivateRoute({ allowedRoles }) {
  const [userRole, setUserRole] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const userId = localStorage.getItem("userId");
    if (userId) {
      UserService.getRol(userId)
        .then((response) => {
          setUserRole(response.data); // Asigna el rol obtenido
        })
        .catch((error) => {
          console.error("Error al obtener el rol del usuario:", error);
          setUserRole("guest"); // Opcional, establece un rol por defecto
        })
        .finally(() => setLoading(false)); // Finaliza la carga
    } else {
      setLoading(false); // Finaliza la carga si no hay userId en localStorage
    }
  }, []);

  if (loading) return null; // Retorna null o un spinner mientras carga

  if (userRole !== 0 && !userRole) {
    // Si no está logueado, redirige a la página de inicio de sesión
    return <Navigate to="/" replace />;
  }

  if (!allowedRoles.includes(userRole)) {
    // Si el rol no tiene acceso a la ruta, redirige a una página de acceso denegado o a Home
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
}
