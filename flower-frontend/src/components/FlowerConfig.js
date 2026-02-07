import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { preferenceAPI } from '../services/api';
import './FlowerConfig.css';

const FlowerConfig = () => {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem('user') || '{}');
  const [loading, setLoading] = useState(false);

  const [config, setConfig] = useState({
    flowerTypes: [],
    colors: [],
    size: '',
    concept: '',
    bouquetStyle: '',
    cardMessage: ''
  });

  // Çiçek türleri
  const flowerTypes = [
    { id: 'rose', name: 'Gül', icon: '🌹' },
    { id: 'tulip', name: 'Lale', icon: '🌷' },
    { id: 'sunflower', name: 'Ayçiçeği', icon: '🌻' },
    { id: 'lily', name: 'Zambak', icon: '🌺' },
    { id: 'orchid', name: 'Orkide', icon: '🌸' },
    { id: 'daisy', name: 'Papatya', icon: '🌼' },
    { id: 'carnation', name: 'Karanfil', icon: '💐' },
    { id: 'peony', name: 'Şakayık', icon: '🏵️' },
    { id: 'hydrangea', name: 'Ortanca', icon: '🌺' },
    { id: 'lavender', name: 'Lavanta', icon: '💜' }
  ];

  // Boyutlar
  const sizes = [
    { id: 'small', name: 'Küçük', description: '10-15 dal', icon: '🌸' },
    { id: 'medium', name: 'Orta', description: '20-30 dal', icon: '💐' },
    { id: 'large', name: 'Büyük', description: '40-50 dal', icon: '🌺' },
    { id: 'extra', name: 'Extra', description: '60+ dal', icon: '🏵️' }
  ];

  // Konseptler
  const concepts = [
    { id: 'romantic', name: 'Romantik', icon: '💕' },
    { id: 'elegant', name: 'Zarif', icon: '✨' },
    { id: 'modern', name: 'Modern', icon: '🎨' },
    { id: 'rustic', name: 'Rustik', icon: '🌿' },
    { id: 'luxurious', name: 'Lüks', icon: '👑' },
    { id: 'minimalist', name: 'Minimalist', icon: '⚪' },
    { id: 'colorful', name: 'Renkli', icon: '🌈' },
    { id: 'natural', name: 'Doğal', icon: '🍃' }
  ];

  // Buket stilleri
  const bouquetStyles = [
    { id: 'round', name: 'Yuvarlak Buket', icon: '⚪' },
    { id: 'cascade', name: 'Şelale Buket', icon: '💧' },
    { id: 'hand-tied', name: 'El Bağlaması', icon: '🎀' },
    { id: 'basket', name: 'Sepet Aranjman', icon: '🧺' },
    { id: 'box', name: 'Kutu Aranjman', icon: '📦' },
    { id: 'vase', name: 'Vazo Aranjman', icon: '🏺' }
  ];

  // Çiçek türü seçimi
  const handleFlowerTypeToggle = (typeId) => {
    if (config.flowerTypes.includes(typeId)) {
      setConfig({
        ...config,
        flowerTypes: config.flowerTypes.filter(id => id !== typeId)
      });
    } else {
      if (config.flowerTypes.length < 3) {
        setConfig({
          ...config,
          flowerTypes: [...config.flowerTypes, typeId]
        });
      } else {
        alert('Maksimum 3 çiçek türü seçebilirsiniz!');
      }
    }
  };

  // Renk ekleme
  const handleAddColor = (color) => {
    if (config.colors.length < 3) {
      if (!config.colors.includes(color)) {
        setConfig({
          ...config,
          colors: [...config.colors, color]
        });
      }
    } else {
      alert('Maksimum 3 renk seçebilirsiniz!');
    }
  };

  // Renk kaldırma
  const handleRemoveColor = (color) => {
    setConfig({
      ...config,
      colors: config.colors.filter(c => c !== color)
    });
  };

  // Form gönderimi
  const handleSubmit = async (e) => {
    e.preventDefault();

    // Validasyon
    if (config.flowerTypes.length === 0) {
      alert('Lütfen en az 1 çiçek türü seçin!');
      return;
    }
    if (config.colors.length === 0) {
      alert('Lütfen en az 1 renk seçin!');
      return;
    }
    if (!config.size) {
      alert('Lütfen boyut seçin!');
      return;
    }
    if (!config.concept) {
      alert('Lütfen konsept seçin!');
      return;
    }
    if (!config.bouquetStyle) {
      alert('Lütfen buket stili seçin!');
      return;
    }

    setLoading(true);

    try {
      // Backend'e tercihleri kaydet ve prompt al
      const response = await preferenceAPI.save(config);
      
      console.log('✅ API Response:', response);
      console.log('✅ Preference Data:', response.data.preference);
      console.log('✅ Image Prompt:', response.data.preference.imagePrompt);
      console.log('✅ Preference ID:', response.data.preference.id);
      
      // Prompt'u localStorage'a kaydet
      const imagePrompt = response.data.preference.imagePrompt;
      const preferenceId = response.data.preference.id;
      
      if (!imagePrompt) {
        console.error('❌ Image prompt bulunamadı!');
        alert('Image prompt oluşturulamadı!');
        return;
      }
      
      localStorage.setItem('imagePrompt', imagePrompt);
      localStorage.setItem('preferenceId', preferenceId);
      
      console.log('✅ localStorage set edildi');
      console.log('✅ Yönlendirme yapılıyor: /image-generation');
      
      // Image generation sayfasına yönlendir
      navigate('/image-generation');
      
    } catch (error) {
      console.error('❌ Hata:', error);
      console.error('❌ Error Response:', error.response);
      alert(error.response?.data || 'Tercihler kaydedilirken bir hata oluştu!');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flower-config-container">
      <header className="config-header">
        <button onClick={() => navigate('/dashboard')} className="btn-back">
          ← Geri
        </button>
        <h1>🌸 Çiçeğini Tasarla</h1>
        <div className="user-name">{user.name}</div>
      </header>

      <form onSubmit={handleSubmit} className="config-form">
        {/* Çiçek Türleri */}
        <section className="config-section">
          <div className="section-header">
            <h2>🌹 Çiçek Türleri</h2>
            <span className="selection-count">
              {config.flowerTypes.length}/3 seçildi
            </span>
          </div>
          <p className="section-description">En fazla 3 çiçek türü seçebilirsiniz</p>
          
          <div className="options-grid">
            {flowerTypes.map(type => (
              <button
                key={type.id}
                type="button"
                className={`option-card ${config.flowerTypes.includes(type.id) ? 'selected' : ''}`}
                onClick={() => handleFlowerTypeToggle(type.id)}
              >
                <span className="option-icon">{type.icon}</span>
                <span className="option-name">{type.name}</span>
              </button>
            ))}
          </div>
        </section>

        {/* Renkler */}
        <section className="config-section">
          <div className="section-header">
            <h2>🎨 Renkler</h2>
            <span className="selection-count">
              {config.colors.length}/3 seçildi
            </span>
          </div>
          <p className="section-description">Renk paletinden istediğiniz renkleri seçin (maksimum 3)</p>
          
          <div className="color-picker-section">
            <input
              type="color"
              className="color-input"
              onChange={(e) => handleAddColor(e.target.value)}
            />
            <p className="color-hint">↑ Renk seçmek için tıklayın</p>
          </div>

          {config.colors.length > 0 && (
            <div className="selected-colors">
              <p className="selected-colors-title">Seçilen Renkler:</p>
              <div className="color-chips">
                {config.colors.map((color, index) => (
                  <div key={index} className="color-chip">
                    <div 
                      className="color-preview" 
                      style={{ backgroundColor: color }}
                    ></div>
                    <span className="color-code">{color}</span>
                    <button
                      type="button"
                      className="color-remove"
                      onClick={() => handleRemoveColor(color)}
                    >
                      ×
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}
        </section>

        {/* Boyut */}
        <section className="config-section">
          <div className="section-header">
            <h2>📏 Boyut</h2>
          </div>
          <p className="section-description">Buketinizin boyutunu seçin</p>
          
          <div className="options-grid size-grid">
            {sizes.map(size => (
              <button
                key={size.id}
                type="button"
                className={`option-card size-card ${config.size === size.id ? 'selected' : ''}`}
                onClick={() => setConfig({ ...config, size: size.id })}
              >
                <span className="option-icon">{size.icon}</span>
                <div className="size-info">
                  <span className="option-name">{size.name}</span>
                  <span className="size-description">{size.description}</span>
                </div>
              </button>
            ))}
          </div>
        </section>

        {/* Konsept */}
        <section className="config-section">
          <div className="section-header">
            <h2>✨ Konsept</h2>
          </div>
          <p className="section-description">Buketinizin genel havasını seçin</p>
          
          <div className="options-grid">
            {concepts.map(concept => (
              <button
                key={concept.id}
                type="button"
                className={`option-card ${config.concept === concept.id ? 'selected' : ''}`}
                onClick={() => setConfig({ ...config, concept: concept.id })}
              >
                <span className="option-icon">{concept.icon}</span>
                <span className="option-name">{concept.name}</span>
              </button>
            ))}
          </div>
        </section>

        {/* Buket Stili */}
        <section className="config-section">
          <div className="section-header">
            <h2>💐 Buket Stili</h2>
          </div>
          <p className="section-description">Çiçeklerinizin nasıl sunulacağını seçin</p>
          
          <div className="options-grid">
            {bouquetStyles.map(style => (
              <button
                key={style.id}
                type="button"
                className={`option-card ${config.bouquetStyle === style.id ? 'selected' : ''}`}
                onClick={() => setConfig({ ...config, bouquetStyle: style.id })}
              >
                <span className="option-icon">{style.icon}</span>
                <span className="option-name">{style.name}</span>
              </button>
            ))}
          </div>
        </section>

        {/* Kart Yazısı */}
        <section className="config-section">
          <div className="section-header">
            <h2>💌 Kart Yazısı (Opsiyonel)</h2>
          </div>
          <p className="section-description">Çiçeğinizle birlikte gönderilecek özel mesajınızı yazın</p>
          
          <textarea
            className="card-message-input"
            placeholder="Örn: Doğum günün kutlu olsun! Seni çok seviyorum..."
            value={config.cardMessage}
            onChange={(e) => setConfig({ ...config, cardMessage: e.target.value })}
            rows="4"
            maxLength="200"
          />
          <p className="char-count">{config.cardMessage.length}/200 karakter</p>
        </section>

        {/* Submit Button */}
        <div className="submit-section">
          <button type="submit" className="btn-submit" disabled={loading}>
            {loading ? (
              <>
                <span>⏳</span>
                <span>Kaydediliyor...</span>
              </>
            ) : (
              <>
                <span>🎨</span>
                <span>Tasarımı Oluştur</span>
                <span>→</span>
              </>
            )}
          </button>
        </div>
      </form>
    </div>
  );
};

export default FlowerConfig;