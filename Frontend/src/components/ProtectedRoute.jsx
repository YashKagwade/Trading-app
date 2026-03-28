import { Navigate } from "react-router-dom";

const ProtectedRoute = ({ children, adminOnly = false }) => {
  const token = localStorage.getItem("token");
  const user = JSON.parse(localStorage.getItem("user") || "null");

  if (!token) {
    return <Navigate to="/login" />;
  }

  if (adminOnly && user && user.role !== "ROLE_ADMIN") {
    return <Navigate to="/dashboard" />;
  }

  return children;
};

export default ProtectedRoute;