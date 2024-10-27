import './App.css';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Home from './components/Home';
import Navbar from './components/Navbar';
import Register from './components/Register'; 
import Login from './components/Login'; 
import SimulateLoan from './components/Simulateloan'; 
import Loanrequest from './components/Loanrequest';
import Myrequests from './components/Myrequests';
import Loanlist from './components/Loanlist';
import Evaluateloan from './components/Evaluateloan';
import PrivateRoute from './components/PrivateRoute'; // Importa el componente PrivateRoute

function App() {
  return (
    <Router>
      <div className="container">
        <Navbar />
        <Routes>
          {/* Rutas públicas */}
          <Route path="/" element={<Home />} />
          <Route path="/Login" element={<Login />} />
          <Route path="/Register" element={<Register />} />

          {/* Rutas protegidas para el cliente (userRole === 1) */}
          <Route element={<PrivateRoute allowedRoles={[1]} />}>
            <Route path="/SimulateLoan" element={<SimulateLoan />} />
            <Route path="/Loanrequest" element={<Loanrequest />} />
            <Route path="/Myrequests" element={<Myrequests />} />
          </Route>

          {/* Rutas protegidas para el ejecutivo (userRole === 0) */}
          <Route element={<PrivateRoute allowedRoles={[0]} />}>
            <Route path="/Loanlist" element={<Loanlist />} />
            <Route path="/evaluateLoan/:loanRequestId" element={<Evaluateloan />} />
          </Route>

          {/* Ruta de página no encontrada */}
          <Route path="*" element={<h1>Not Found</h1>} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
