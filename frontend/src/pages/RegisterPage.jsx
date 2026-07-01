import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { forumApi } from "../api/forumApi";

export default function RegisterPage() {
  const navigate = useNavigate();

  const [registrationForm, setRegistrationForm] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: ""
  });

  const [registrationError, setRegistrationError] = useState("");
  const [isSubmittingRegistration, setIsSubmittingRegistration] = useState(false);

  function updateRegistrationField(fieldName, fieldValue) {
    setRegistrationForm((currentForm) => ({
      ...currentForm,
      [fieldName]: fieldValue
    }));
  }

  function validateRegistrationForm() {
    if (!registrationForm.username.trim()) {
      return "Username must be placed";
    }

    if (registrationForm.password.length < 8) {
      return "Password must be at least 8 symbols";
    }

    if (registrationForm.password !== registrationForm.confirmPassword) {
      return "Passwords not match.";
    }

    return "";
  }

  async function submitRegistration(event) {
    event.preventDefault();

    const validationMessage = validateRegistrationForm();

    if (validationMessage) {
      setRegistrationError(validationMessage);
      return;
    }

    setRegistrationError("");
    setIsSubmittingRegistration(true);

    try {
      await forumApi.registerUser({
        username: registrationForm.username,
        email: registrationForm.email,
        password: registrationForm.password
      });

      navigate("/login");
    } catch {
      setRegistrationError(
        "Registration failed. Username or email is already registered."
      );
    } finally {
      setIsSubmittingRegistration(false);
    }
  }

  return (
    <section className="narrow">
      <h1>Join the Forum</h1>

      {registrationError && <p className="error">{registrationError}</p>}

      <form className="form" onSubmit={submitRegistration}>
        <label>Username</label>
        <input
          value={registrationForm.username}
          onChange={(event) =>
            updateRegistrationField("username", event.target.value)
          }
          placeholder="Example: John"
        />

        <label>Email</label>
        <input
          type="email"
          value={registrationForm.email}
          onChange={(event) =>
            updateRegistrationField("email", event.target.value)
          }
          placeholder="email@example.com"
        />

        <label>Password</label>
        <input
          type="password"
          value={registrationForm.password}
          onChange={(event) =>
            updateRegistrationField("password", event.target.value)
          }
          placeholder="Minimum 8 symbols"
        />

        <label>Confirm Password</label>
        <input
          type="password"
          value={registrationForm.confirmPassword}
          onChange={(event) =>
            updateRegistrationField("confirmPassword", event.target.value)
          }
          placeholder="Repeat password"
        />

        <button type="submit" disabled={isSubmittingRegistration}>
          {isSubmittingRegistration ? "Creating account..." : "Create Account"}
        </button>
      </form>
    </section>
  );
}