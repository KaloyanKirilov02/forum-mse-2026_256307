import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../state/AuthContext";

export default function LoginPage() {
  const auth = useAuth();
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  async function submit(e) {
    e.preventDefault();

    try {
      await auth.login(username, password);
      navigate("/");
    } catch {
      setError("Wrong username or password");
    }
  }

  return (
    <section className="narrow">
      <h1>Login</h1>

      {error && <p className="error">{error}</p>}

      <form className="form" onSubmit={submit}>
        <label>Username</label>

        <input
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          placeholder="Username"
        />

        <label>Password</label>

        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Password"
        />

        <button type="submit">
          Login
        </button>
      </form>
    </section>
  );
}