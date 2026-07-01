const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:9000";

function getToken() {
  return localStorage.getItem("forum_token");
}

async function request(path, options = {}) {
  const token = getToken();

  const response = await fetch(`${API_URL}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...(options.headers ?? {})
    }
  });

  if (!response.ok) {
    throw new Error(`API error: ${response.status}`);
  }

  if (response.status === 204) return null;

  return response.json();
}

export const forumApi = {
  login: (username, password) =>
    request("/auth/login", {
      method: "POST",
      body: JSON.stringify({ username, password })
    }),

  getTopics: () => request("/posts"),

  getTopic: (id) => request(`/posts/${id}`),

  createTopic: (title, content) =>
    request("/posts", {
      method: "POST",
      body: JSON.stringify({ title, content })
    }),

  getReplies: (topicId) => request(`/posts/${topicId}/replies`),

  createReply: (topicId, content) =>
    request(`/posts/${topicId}/replies`, {
      method: "POST",
      body: JSON.stringify({ content })
    }),

  registerUser: ({ username, email, password }) =>
      request("/auth/register", {
        method: "POST",
        body: JSON.stringify({
          username,
          email,
          password
        })
      }),

    getUsers: () => request("/users"),

    changeUserRole: (id, role) =>
      request(`/users/${id}`, {
        method: "PATCH",
        body: JSON.stringify({ role })
      }),

    updateTopic: (id, title, content) =>
      request(`/posts/${id}`, {
        method: "PUT",
        body: JSON.stringify({ title, content })
      }),

    updateReply: (id, content) =>
      request(`/replies/${id}`, {
        method: "PUT",
        body: JSON.stringify({ content })
      })
  };
