import { useEffect, useState } from "react";
import loanRequestService from "../services/loanRequest.service";
import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableContainer from "@mui/material/TableContainer";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Paper from "@mui/material/Paper";
import Button from "@mui/material/Button";
import DeleteIcon from "@mui/icons-material/Delete";
import Typography from "@mui/material/Typography";
import { styled } from "@mui/material/styles";
import Box from "@mui/material/Box";

const StyledTableRow = styled(TableRow)(({ theme, status }) => ({
  backgroundColor:
    status === 7 || status === 8
      ? theme.palette.error.light
      : theme.palette.background.paper,
  "&:hover": {
    backgroundColor:
      status === 7 || status === 8
        ? theme.palette.error.main
        : theme.palette.action.hover,
  },
}));

const LoanRequestList = () => {
  const [loanRequests, setLoanRequests] = useState([]);
  const [userId, setUserId] = useState(null);
  const [showRejectedAndCancelled, setShowRejectedAndCancelled] = useState(true);

  const fetchLoanRequests = async () => {
    if (!userId) return;
    try {
      const response = await loanRequestService.getClientRequests(userId);
      const requests = response.data;

      const updatedRequests = await Promise.all(
        requests.map(async (request) => {
          const totalCostResponse = await loanRequestService.calculateTotalCost(request);
          return { ...request, totalCost: totalCostResponse.data };
        })
      );

      setLoanRequests(updatedRequests);
      console.log("Solicitudes de préstamo obtenidas con costo total:", updatedRequests);
    } catch (error) {
      console.error("Error al obtener las solicitudes de préstamo:", error);
    }
  };

  useEffect(() => {
    const storedUserId = localStorage.getItem("userId");
    if (storedUserId) {
      setUserId(storedUserId);
    }
  }, []);

  useEffect(() => {
    fetchLoanRequests();
  }, [userId]);

  const handleCancel = (loanRequestId) => {
    const confirmCancel = window.confirm(
      "¿Está seguro que desea cancelar esta solicitud de préstamo?"
    );
    if (confirmCancel) {
      loanRequestService
        .acceptConditions(loanRequestId, false)
        .then((response) => {
          console.log("Solicitud cancelada:", response.data);
          fetchLoanRequests();
        })
        .catch((error) => {
          console.error("Error al cancelar la solicitud:", error);
        });
    }
  };

  const handleAccept = (loanRequestId) => {
    const confirmAccept = window.confirm(
      "¿Está seguro que desea aceptar esta solicitud de préstamo?"
    );
    if (confirmAccept) {
      loanRequestService
        .acceptConditions(loanRequestId, true)
        .then((response) => {
          console.log("Solicitud aceptada:", response.data);
          fetchLoanRequests();
        })
        .catch((error) => {
          console.error("Error al aceptar la solicitud:", error);
        });
    }
  };

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

  const getLoanStatus = (status) => {
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

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom>
        Mis Solicitudes
      </Typography>
      <Button
        variant="contained"
        color="primary"
        onClick={() => setShowRejectedAndCancelled(!showRejectedAndCancelled)}
        sx={{ mb: 2 }}
      >
        {showRejectedAndCancelled ? "Ocultar" : "Mostrar"} Rechazadas y Canceladas
      </Button>
      <TableContainer component={Paper} sx={{ borderRadius: 2, overflow: "hidden" }}>
        <Table sx={{ minWidth: 650 }} aria-label="tabla de solicitudes de préstamo">
          <TableHead sx={{ bgcolor: "primary.main" }}>
            <TableRow>
              <TableCell sx={{ color: "white", fontWeight: "bold" }}>Monto Solicitado (CLP)</TableCell>
              <TableCell sx={{ color: "white", fontWeight: "bold" }}>Valor de la Propiedad (CLP)</TableCell>
              <TableCell sx={{ color: "white", fontWeight: "bold" }}>Tipo de Préstamo</TableCell>
              <TableCell sx={{ color: "white", fontWeight: "bold" }}>Tiempo del Préstamo (años)</TableCell>
              <TableCell sx={{ color: "white", fontWeight: "bold" }}>Estado</TableCell>
              <TableCell sx={{ color: "white", fontWeight: "bold" }}>Costo Total (CLP)</TableCell>
              <TableCell sx={{ color: "white", fontWeight: "bold" }}>Operaciones</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {loanRequests
              .filter(
                (request) =>
                  showRejectedAndCancelled || (request.loanStatus !== 7 && request.loanStatus !== 8)
              )
              .map((request) => (
                <StyledTableRow key={request.loanRequestId} status={request.loanStatus}>
                  <TableCell align="left">{request.loanAmount.toLocaleString("es-CL", { style: "currency", currency: "CLP" })}</TableCell>
                  <TableCell align="left">{request.propertyValue.toLocaleString("es-CL", { style: "currency", currency: "CLP" })}</TableCell>
                  <TableCell align="left">{getLoanType(request.loanType)}</TableCell>
                  <TableCell align="left">{request.loanTime}</TableCell>
                  <TableCell align="left">{getLoanStatus(request.loanStatus)}</TableCell>
                  <TableCell align="left">
                    {request.totalCost?.toLocaleString("es-CL", { style: "currency", currency: "CLP" }) || "Calculando..."}
                  </TableCell>
                  <TableCell align="center">
                    {request.loanStatus === 4 ? (
                      <Box display="flex" gap={1}>
                        <Button
                          variant="contained"
                          color="primary"
                          size="small"
                          onClick={() => handleAccept(request.loanRequestId)}
                        >
                          Aceptar
                        </Button>
                        <Button
                          variant="contained"
                          color="error"
                          size="small"
                          onClick={() => handleCancel(request.loanRequestId)}
                          startIcon={<DeleteIcon />}
                        >
                          Cancelar
                        </Button>
                      </Box>
                    ) : (
                      (request.loanStatus === 1 || request.loanStatus === 2 || request.loanStatus === 3) && (
                        <Button
                          variant="contained"
                          color="error"
                          size="small"
                          onClick={() => handleCancel(request.loanRequestId)}
                          startIcon={<DeleteIcon />}
                        >
                          Cancelar
                        </Button>
                      )
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

export default LoanRequestList;
