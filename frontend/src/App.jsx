import { Link, Route, Routes } from "react-router-dom";
import { useAuth } from "./state/AuthContext";
import TopicsPage from "./pages/TopicsPage";
import TopicDetailsPage from "./pages/TopicDetailsPage";
import CreateTopicPage from "./pages/CreateTopicPage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import CommunityControlPage from "./pages/CommunityControlPage";

export default function App() {
  const auth = useAuth();

  return (
    <div className="app">
      <nav className="navbar">
        <Link to="/" className="brand">Forum</Link>

        <div className="navLinks">
          <Link to="/">Topics</Link>

          {auth.isLogged && <Link to="/topics/create">Create Topic</Link>}

          {!auth.isLogged ? (
            <>
              <Link to="/login">Login</Link>
              <Link to="/register">Register</Link>
            </>
          ) : (
            <>
            {auth.user?.role === "ADMIN" && (
                      <Link to="/community-control">Community Control</Link>
                    )}
              <span className="userBadge">
                {auth.user.username} / {auth.user.role}
              </span>
              <button onClick={auth.logout}>Logout</button>
            </>
          )}

        </div>
      </nav>

      <main>
        <Routes>
          <Route path="/" element={<TopicsPage />} />
          <Route path="/topics/create" element={<CreateTopicPage />} />
          <Route path="/topics/:id" element={<TopicDetailsPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/community-control" element={<CommunityControlPage />} />
        </Routes>
      </main>
    </div>
  );
}