import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { forumApi } from "../api/forumApi";

export default function CreateTopicPage() {
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [error, setError] = useState("");

  async function submit(e) {
    e.preventDefault();

    try {
      const created = await forumApi.createTopic(title, content);
      navigate(`/topics/${created.id}`);
    } catch {
      setError("We couldn't create the topic. Check if you are logged in.");
    }
  }

  return (
    <section className="narrow">
      <h1>Create Topic</h1>

      {error && <p className="error">{error}</p>}

      <form className="form" onSubmit={submit}>
        <label>Title</label>
        <input
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="A unique title..."
        />

        <label>Description / Content</label>
        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          placeholder="Short description..."
        />

        <button>Create</button>
      </form>
    </section>
  );
}