import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { designAPI } from '../services/api';
import './Dashboard.css';

const Dashboard = () => {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const token = localStorage.getItem('token');
  const [designs, setDesigns] = useState([]);
  const [loading, setLoading] = useState(true);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/login');
  };

  const handleCreateDesign = () => {
    navigate('/flower-config');
  };

  const handleFetchDesigns = async () => {
    try {
      const data = await designAPI.getMyDesigns();
      console.log('Gelen veri:', data);
      setDesigns(Array.isArray(data) ? data : []); // güvenli atama
    } catch (error) {
      console.error('Tasarım verileri alınamadı:', error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    handleFetchDesigns();
  }, [token]);

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div className="logo">
          <span className="flower-icon">🌸</span>
          <span>Çiçek Tasarımcısı</span>
        </div>
        <div className="user-info">
          <span>Merhaba, {user.name}</span>
          <button onClick={handleLogout} className="btn-logout">
            Çıkış Yap
          </button>
        </div>
      </header>

      <main className="dashboard-main">
        <div className="hero-section">
          <h1 className="hero-title">🌺 Hoş Geldiniz, {user.name}!</h1>
          <p className="hero-subtitle">
            Size özel AI destekli çiçek tasarımları oluşturmaya hazır mısınız?
          </p>
          
          <button onClick={handleCreateDesign} className="btn-create-design">
            <span className="btn-icon">🎨</span>
            <span className="btn-text">
              <span className="btn-main-text">Bana Çiçek Lazım!</span>
              <span className="btn-sub-text">Hadi birlikte tasarlayalım</span>
            </span>
            <span className="btn-arrow">→</span>
          </button>

          <div className="features-grid">
            <div className="feature-card">
              <div className="feature-icon">🎯</div>
              <h3>Kişiselleştirilmiş</h3>
              <p>Tercihlerinize özel tasarımlar</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">🤖</div>
              <h3>AI Destekli</h3>
              <p>Yapay zeka ile en uygun kombinasyonlar</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">⚡</div>
              <h3>Hızlı & Kolay</h3>
              <p>Sadece birkaç soru ile hazır</p>
            </div>
          </div>
        </div>

        <div className="recent-section">
          <h2 className="section-title">Geçmiş Tasarımlarınız</h2>
          {loading ? (
            <p>Yükleniyor...</p>
          ) : designs.length === 0 ? (
            <div className="empty-state">
              <div className="empty-icon">🌼</div>
              <p>Henüz bir tasarımınız yok</p>
              <p className="empty-hint">
                İlk tasarımınızı oluşturmak için yukarıdaki butona tıklayın
              </p>
            </div>
          ) : (
            <div className="design-grid">
              {designs.map((design) => (
                <div key={design.id} className="design-card">
                  <img
                    src={design.imageUrl}
                    alt={design.imagePrompt}
                    className="design-image"
                  />
                  <div className="design-info">
                    <p className="design-date">
                      {new Date(design.createdAt).toLocaleString('tr-TR')}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </main>
    </div>
  );
};

export default Dashboard;