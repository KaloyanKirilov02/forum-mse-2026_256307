import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { forumApi } from "../api/forumApi";
import { useAuth } from "../state/AuthContext";
import { usePagination } from "../hooks/usePagination";

export default function TopicDetailsPage() {
  const { id } = useParams();
  const auth = useAuth();

  const [topic, setTopic] = useState(null);
  const [replies, setReplies] = useState([]);
  const [replyText, setReplyText] = useState("");
  const [error, setError] = useState("");

  const [isEditingTopic, setIsEditingTopic] = useState(false);
  const [editedTopicTitle, setEditedTopicTitle] = useState("");
  const [editedTopicContent, setEditedTopicContent] = useState("");

  const [editingReplyId, setEditingReplyId] = useState(null);
  const [editedReplyContent, setEditedReplyContent] = useState("");

  const pagination = usePagination(replies, 10);

  function canEdit(authorId) {
    if (!auth.isLogged || !auth.user) return false;

    return (
      auth.user.role === "ADMIN" ||
      auth.user.role === "MODERATOR" ||
      Number(auth.user.id) === Number(authorId)
    );
  }

  async function loadData() {
    const topicData = await forumApi.getTopic(id);
    const repliesData = await forumApi.getReplies(id);

    setTopic(topicData);
    setReplies(repliesData);
  }

  useEffect(() => {
    loadData().catch(() => setError("Could not load the topic."));
  }, [id]);

  async function submitReply(event) {
    event.preventDefault();

    if (!replyText.trim()) return;

    await forumApi.createReply(id, replyText);
    setReplyText("");
    await loadData();
  }

  function startTopicEdit() {
    setEditedTopicTitle(topic.title);
    setEditedTopicContent(topic.content);
    setIsEditingTopic(true);
  }

  async function submitTopicEdit(event) {
    event.preventDefault();

    await forumApi.updateTopic(topic.id, editedTopicTitle, editedTopicContent);

    setIsEditingTopic(false);
    await loadData();
  }

  function startReplyEdit(reply) {
    setEditingReplyId(reply.id);
    setEditedReplyContent(reply.content);
  }

  async function submitReplyEdit(event, replyId) {
    event.preventDefault();

    await forumApi.updateReply(replyId, editedReplyContent);

    setEditingReplyId(null);
    setEditedReplyContent("");
    await loadData();
  }

  if (error) return <p className="error">{error}</p>;
  if (!topic) return <p>Loading...</p>;

  return (
    <section>
      <article className="heroCard">
        {!isEditingTopic ? (
          <>
            <h1>{topic.title}</h1>
            <p>{topic.content}</p>

            <div className="meta">
              <span>Author ID: {topic.authorId ?? "N/A"}</span>
              <span>Created: {new Date(topic.createdAt).toLocaleString()}</span>
              <span>
                Modified:{" "}
                {topic.modifiedAt
                  ? new Date(topic.modifiedAt).toLocaleString()
                  : "N/A"}
              </span>
              <span>Views: {topic.viewsCount ?? "N/A"}</span>
            </div>

            {canEdit(topic.authorId) && (
              <button type="button" onClick={startTopicEdit}>
                Edit Topic
              </button>
            )}
          </>
        ) : (
          <form className="form" onSubmit={submitTopicEdit}>
            <h3>Edit Topic</h3>

            <label>Title</label>
            <input
              value={editedTopicTitle}
              onChange={(event) => setEditedTopicTitle(event.target.value)}
            />

            <label>Content</label>
            <textarea
              value={editedTopicContent}
              onChange={(event) => setEditedTopicContent(event.target.value)}
            />

            <button type="submit">Save Topic</button>

            <button type="button" onClick={() => setIsEditingTopic(false)}>
              Cancel
            </button>
          </form>
        )}
      </article>

      <h2>Replies</h2>

      <div className="replyList">
        {pagination.pageItems.map((reply) => (
          <article key={reply.id} className="reply">
            {editingReplyId === reply.id ? (
              <form onSubmit={(event) => submitReplyEdit(event, reply.id)}>
                <textarea
                  value={editedReplyContent}
                  onChange={(event) =>
                    setEditedReplyContent(event.target.value)
                  }
                />

                <button type="submit">Save Reply</button>

                <button type="button" onClick={() => setEditingReplyId(null)}>
                  Cancel
                </button>
              </form>
            ) : (
              <>
                <p>{reply.content}</p>

                <div className="meta">
                  <span>Author ID: {reply.authorId ?? "N/A"}</span>
                  <span>
                    Created: {new Date(reply.createdAt).toLocaleString()}
                  </span>
                  <span>
                    Modified:{" "}
                    {reply.modifiedAt
                      ? new Date(reply.modifiedAt).toLocaleString()
                      : "N/A"}
                  </span>
                </div>

                {canEdit(reply.authorId) && (
                  <button type="button" onClick={() => startReplyEdit(reply)}>
                    Edit Reply
                  </button>
                )}
              </>
            )}
          </article>
        ))}
      </div>

      <div className="pagination">
        <button type="button" onClick={pagination.previous}>
          Previous
        </button>

        <span>
          Page {pagination.page} / {pagination.totalPages}
        </span>

        <button type="button" onClick={pagination.next}>
          Next
        </button>
      </div>

      {auth.isLogged ? (
        <form className="form" onSubmit={submitReply}>
          <h3>Create reply</h3>

          <textarea
            value={replyText}
            onChange={(event) => setReplyText(event.target.value)}
            placeholder="Post a reply..."
          />

          <button type="submit">Publish Reply</button>
        </form>
      ) : (
        <p className="muted">Login, to be able to post</p>
      )}
    </section>
  );
}