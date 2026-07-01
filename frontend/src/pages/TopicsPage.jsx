import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { forumApi } from "../api/forumApi";

export default function TopicsPage() {
  const [topics, setTopics] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    forumApi
      .getTopics()
      .then(setTopics)
      .catch(() => setError("Topics could not load."));
  }, []);

  return (
    <section>
      <h1>Topics</h1>

      {error && <p className="error">{error}</p>}

      <div className="grid">
        {topics.map((topic) => (
          <article key={topic.id} className="card">
            <h2>
              <Link to={`/topics/${topic.id}`}>{topic.title}</Link>
            </h2>

            <p>{topic.content}</p>

            <div className="meta">
              <span>Created: {new Date(topic.createdAt).toLocaleString()}</span>
              <span>Views: {topic.viewsCount ?? "N/A"}</span>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}