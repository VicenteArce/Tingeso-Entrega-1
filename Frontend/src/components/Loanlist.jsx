import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import loanRequestService from "../services/loanRequest.service";
import userService from "../services/user.service"; // Importa el servicio de usuarios
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import EditIcon from "@mui/icons-material/Edit";
import { styled } from "@mui/material/styles";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";

const StyledTableRow = styled(TableRow)(({ theme, status }) => ({
  backgroundColor:
    status === 7 || status === 8 ? theme.palette.error.light : theme.palette.background.paper,
  "&:hover": {
    backgroundColor:
      status === 7 || status === 8 ? theme.palette.error.main : theme.palette.action.hover,
  },
}));

const Loanlist = () => {
  const [loanRequests, setLoanRequests] = useState([]);
  const [showFiltered, setShowFiltered] = useState(false); // Estado para controlar el filtrado
  const navigate = useNavigate();

  const init = async () => {
    try {
      const response = await loanRequestService.getLoanRequests();
      const loanRequestsData = response.data;

      // Para cada solicitud de préstamo, obtenemos el rut del usuario
      const loanRequestsWithRut = await Promise.all(
        loanRequestsData.map(async (loanRequest) => {
          const userResponse = await userService.getUserById(loanRequest.userId);
          return { ...loanRequest, rut: userResponse.data.rut }; // Agrega el rut a cada solicitud
        })
      );

      setLoanRequests(loanRequestsWithRut);
    } catch (error) {
      console.error("Error al obtener las solicitudes de préstamo o los usuarios:", error);
    }
  };

  useEffect(() => {
    init();
  }, []);

  const getLoanType = (type) => {
    switch (type) {
      case 1:
        return "Primera vivienda";
      case 2:
        return "Segunda vivienda";
      case 3:
        return "Propiedad comercial";
      case 4:
        return "Remodelación";
      default:
        return "Tipo desconocido";
    }
  };

  const formatLoanStatus = (status) => {
    switch (status) {
      case 1:
        return "En Revisión Inicial";
      case 2:
        return "Pendiente de Documentación";
      case 3:
        return "En Evaluación";
      case 4:
        return "Pre-aprobado";
      case 5:
        return "En Aprobación Final";
      case 6:
        return "Aprobada";
      case 7:
        return "Rechazada";
      case 8:
        return "Cancelada por el Cliente";
      case 9:
        return "En Desembolso";
      default:
        return "Estado Desconocido";
    }
  };

  const toggleFilter = () => {
    setShowFiltered((prev) => !prev);
  };

  const filteredLoanRequests = showFiltered
    ? loanRequests.filter((loanRequest) => loanRequest.loanStatus !== 7 && loanRequest.loanStatus !== 8)
    : loanRequests;

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom>
        Lista de Solicitudes
      </Typography>
      <Button variant="contained" onClick={toggleFilter} sx={{ mb: 2 }}>
        {showFiltered ? "Mostrar Todas" : "Ocultar Rechazadas y Canceladas"}
      </Button>
      <TableContainer component={Paper} sx={{ borderRadius: 2, overflow: "hidden" }}>
        <Table sx={{ minWidth: 650 }} size="small" aria-label="tabla de solicitudes">
          <TableHead sx={{ bgcolor: "primary.main" }}>
            <TableRow>
              <TableCell sx={{ color: "white", fontWeight: "bold" }}>RUT</TableCell> {/* Nueva columna para el RUT */}
              <TableCell align="right" sx={{ color: "white", fontWeight: "bold" }}>
                Monto Solicitado (CLP)
              </TableCell>
              <TableCell align="right" sx={{ color: "white", fontWeight: "bold" }}>
                Valor de la Propiedad (CLP)
              </TableCell>
              <TableCell align="right" sx={{ color: "white", fontWeight: "bold" }}>
                Tipo de Préstamo
              </TableCell>
              <TableCell align="right" sx={{ color: "white", fontWeight: "bold" }}>
                Estado
              </TableCell>
              <TableCell align="center" sx={{ color: "white", fontWeight: "bold" }}>
                Acciones
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {filteredLoanRequests.map((loanRequest) => (
              <StyledTableRow key={loanRequest.loanRequestId} status={loanRequest.loanStatus}>
                <TableCell component="th" scope="row">
                  {loanRequest.rut} {/* Muestra el RUT */}
                </TableCell>
                <TableCell align="right">{loanRequest.loanAmount.toLocaleString("es-CL", { style: "currency", currency: "CLP" })}</TableCell>
                <TableCell align="right">{loanRequest.propertyValue.toLocaleString("es-CL", { style: "currency", currency: "CLP" })}</TableCell>
                <TableCell align="right">{getLoanType(loanRequest.loanType)}</TableCell>
                <TableCell align="right">{formatLoanStatus(loanRequest.loanStatus)}</TableCell>
                <TableCell align="center">
                  {loanRequest.loanStatus === 3 ? (
                    <Button
                      variant="contained"
                      color="primary"
                      startIcon={<EditIcon />}
                      onClick={() => navigate(`/evaluateLoan/${loanRequest.loanRequestId}`)}
                    >
                      Evaluar Solicitud
                    </Button>
                  ) : (
                    <Typography variant="body2" color="textSecondary">
                      ---
                    </Typography>
                  )}
                </TableCell>
              </StyledTableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
};

export default Loanlist;
