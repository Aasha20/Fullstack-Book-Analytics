import React, { useState, useEffect } from 'react';
import './App.css';

function App() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetch('/books/stats/authors')
      .then((response) => {
        if (!response.ok) {
          throw new Error('Failed to fetch analytics data');
        }
        return response.json();
      })
      .then((data) => {
        setStats(data);
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
        <p>Loading Analytics Dashboard...</p>
      </div>
    );
  }

  if (error) {
    return <div className="error-container">Error: {error}</div>;
  }

  return (
    <div className="dashboard-container">
      {/* Header with Live Badge */}
      <header className="dashboard-header">
        <h1 className="dashboard-title">
          📚 Books & Author Analytics Dashboard
        </h1>
        <div className="status-badge">
          <span className="pulse-dot"></span> Backend Connected (Port 8080)
        </div>
      </header>

      {/* Cards Grid */}
      <div className="stats-grid">
        <div className="stat-card accent-blue">
          <div className="stat-label">Total Authors</div>
          <div className="stat-value">{stats?.totalAuthors}</div>
        </div>

        <div className="stat-card accent-purple">
          <div className="stat-label">Top Author</div>
          <div className="stat-value">{stats?.topAuthor}</div>
        </div>

        <div className="stat-card accent-orange">
          <div className="stat-label">Least Author</div>
          <div className="stat-value">{stats?.leastAuthor}</div>
        </div>

        <div className="stat-card accent-green">
          <div className="stat-label">Avg Books / Author</div>
          <div className="stat-value">{stats?.averageBooksPerAuthor}</div>
        </div>

        <div className="stat-card accent-teal">
          <div className="stat-label">Median Books / Author</div>
          <div className="stat-value">{stats?.medianBooksPerAuthor}</div>
        </div>
      </div>
    </div>
  );
}

export default App;