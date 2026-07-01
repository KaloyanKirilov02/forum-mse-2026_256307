import { useEffect, useState } from "react";
import { forumApi } from "../api/forumApi";

export default function CommunityControlPage() {
  const [communityMembers, setCommunityMembers] = useState([]);
  const [controlError, setControlError] = useState("");
  const [activeRoleChangeId, setActiveRoleChangeId] = useState(null);

  async function loadCommunityMembers() {
    const loadedMembers = await forumApi.getUsers();
    setCommunityMembers(loadedMembers);
  }

  useEffect(() => {
    loadCommunityMembers().catch(() =>
      setControlError("Could not load all users.")
    );
  }, []);

  async function changeMemberRole(memberId, nextRole) {
    setActiveRoleChangeId(memberId);
    setControlError("");

    try {
      await forumApi.changeUserRole(memberId, nextRole);
      await loadCommunityMembers();
    } catch {
      setControlError("Role was NOT changed.");
    } finally {
      setActiveRoleChangeId(null);
    }
  }

  function roleLabel(role) {
    if (role === "ADMIN") return "ADMIN";
    if (role === "MODERATOR") return "MODERATOR";
    return "USER";
  }

  return (
    <section>
      <h1>Community Control</h1>

      <p className="muted">
        Admin panel
      </p>

      {controlError && <p className="error">{controlError}</p>}

      <div className="grid">
        {communityMembers.map((member) => (
          <article key={member.id} className={`card role-${member.role?.toLowerCase()}`}>
            <h2>{member.username}</h2>

            <p className="muted">{member.email || "No email"}</p>

            <div className="meta">
              <span>{roleLabel(member.role)}</span>
              <span>ID: {member.id}</span>
            </div>

            {member.role === "USER" && (
              <button
                onClick={() => changeMemberRole(member.id, "MODERATOR")}
                disabled={activeRoleChangeId === member.id}
              >
                Promote to Moderator
              </button>
            )}

            {member.role === "MODERATOR" && (
              <button
                onClick={() => changeMemberRole(member.id, "USER")}
                disabled={activeRoleChangeId === member.id}
              >
                Demote to User
              </button>
            )}

            {member.role === "ADMIN" && (
              <p className="muted">Administrator account</p>
            )}
          </article>
        ))}
      </div>
    </section>
  );
}