import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { designAPI } from '../services/api';
import './ImageGeneration.css';

const ImageGeneration = () => {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem('user') || '{}');
  
  const [loading, setLoading] = useState(false);
  const [generatedImage, setGeneratedImage] = useState(null);
  const [prompt, setPrompt] = useState('');
  const [preferenceId, setPreferenceId] = useState('');

  useEffect(() => {
    // localStorage'dan prompt ve preferenceId al
    const storedPrompt = localStorage.getItem('imagePrompt');
    const storedPrefId = localStorage.getItem('preferenceId');
    
    if (!storedPrompt) {
      alert('Prompt bulunamadı! Lütfen önce tercihlerinizi belirleyin.');
      navigate('/flower-config');
      return;
    }
    
    setPrompt(storedPrompt);
    setPreferenceId(storedPrefId || '');
  }, [navigate]);

  const handleGenerateImage = async () => {
    setLoading(true);
    setGeneratedImage(null);

    try {
      const response = await designAPI.generate({
        prompt: prompt,
        preferenceId: preferenceId
      });

      console.log('Tasarım oluşturuldu:', response.data);
      setGeneratedImage(response.data);

      // localStorage'ı temizle
      localStorage.removeItem('imagePrompt');
      localStorage.removeItem('preferenceId');

    } catch (error) {
      console.error('Hata:', error);
      alert(error.response?.data || 'Görsel oluşturulurken bir hata oluştu!');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="image-gen-container">
      <header className="image-gen-header">
        <button onClick={() => navigate('/dashboard')} className="btn-back">
          ← Ana Sayfa
        </button>
        <h1>🎨 AI Çiçek Tasarımı</h1>
        <div className="user-name">{user.name}</div>
      </header>

      <main className="image-gen-main">
        {!generatedImage && !loading && (
          <div className="prompt-section">
            <h2>Hazır mısınız?</h2>
            <p className="prompt-description">
              Tercihlerinize göre özel bir çiçek tasarımı oluşturulacak.
            </p>
            
            <div className="prompt-preview">
              <h3>🤖 AI Prompt'unuz:</h3>
              <p className="prompt-text">{prompt}</p>
            </div>

            <button onClick={handleGenerateImage} className="btn-generate">
              <span>✨</span>
              <span>Tasarımı Oluştur</span>
              <span>🌸</span>
            </button>
          </div>
        )}

        {loading && (
          <div className="loading-section">
            <div className="loader"></div>
            <h2>✨ AI Çiçek Tasarımınızı Oluşturuyor...</h2>
            <p>Bu işlem 10-30 saniye sürebilir</p>
            <div className="loading-steps">
              <div className="step">🎨 Renkler analiz ediliyor...</div>
              <div className="step">🌸 Çiçekler seçiliyor...</div>
              <div className="step">💐 Buket düzenleniyor...</div>
              <div className="step">✨ Son rötuşlar yapılıyor...</div>
            </div>
          </div>
        )}

        {generatedImage && (
          <div className="result-section">
            <h2>🎉 Tasarımınız Hazır!</h2>
            
            <div className="image-container">
              <img 
                src={generatedImage.imageUrl} 
                alt="Generated Flower Design"
                className="generated-image"
              />
            </div>

            <div className="design-info">
              <div className="info-card">
                <h3>📝 Orijinal Prompt</h3>
                <p>{generatedImage.imagePrompt}</p>
              </div>
              
              {generatedImage.revisedPrompt && (
                <div className="info-card">
                  <h3>🤖 AI'ın İyileştirdiği Prompt</h3>
                  <p>{generatedImage.revisedPrompt}</p>
                </div>
              )}
            </div>

            <div className="action-buttons">
              <a 
                href={generatedImage.imageUrl} 
                download="cicek-tasarimi.png"
                className="btn-download"
              >
                📥 İndir
              </a>
              <button onClick={() => navigate('/flower-config')} className="btn-new">
                🎨 Yeni Tasarım
              </button>
              <button onClick={() => navigate('/dashboard')} className="btn-dashboard">
                🏠 Ana Sayfa
              </button>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default ImageGeneration;