import { useState } from 'react';

interface AuthProps {
  onLogin: (token: string, username: string) => void;
}

export default function Auth({ onLogin }: AuthProps) {
  const [isLogin, setIsLogin] = useState(true);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState(''); // Sadece kayıt için
  const [error, setError] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    const url = isLogin 
      ? 'http://localhost:8080/api/auth/login' 
      : 'http://localhost:8080/api/auth/register';
      
    const body = isLogin 
      ? { username, password }
      : { username, password, email };

    fetch(url, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    })
    .then(async res => {
      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || "Bir hata oluştu");
      }
      return isLogin ? res.json() : res.text();
    })
    .then(data => {
      if (isLogin) {
        onLogin(data.token, username);
      } else {
        alert("Kayıt başarılı! Şimdi giriş yapabilirsiniz.");
        setIsLogin(true);
      }
    })
    .catch(err => {
      setError(err.message);
    });
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>{isLogin ? 'Hoş Geldiniz 👋' : 'Aramıza Katılın 🚀'}</h2>
        <p className="auth-subtitle">
          TaskBoard'a erişmek için {isLogin ? 'giriş yapın' : 'hesap oluşturun'}.
        </p>

        {error && <div className="auth-error">{error}</div>}

        <form onSubmit={handleSubmit} className="auth-form">
          <input 
            type="text" 
            placeholder="Kullanıcı Adı" 
            value={username} 
            onChange={e => setUsername(e.target.value)} 
            required 
          />
          {!isLogin && (
            <input 
              type="email" 
              placeholder="E-posta Adresi" 
              value={email} 
              onChange={e => setEmail(e.target.value)} 
              required 
            />
          )}
          <input 
            type="password" 
            placeholder="Şifre" 
            value={password} 
            onChange={e => setPassword(e.target.value)} 
            required 
          />
          <button type="submit" className="auth-submit-btn">
            {isLogin ? 'Giriş Yap' : 'Kayıt Ol'}
          </button>
        </form>

        <p className="auth-switch">
          {isLogin ? "Hesabınız yok mu? " : "Zaten hesabınız var mı? "}
          <button onClick={() => setIsLogin(!isLogin)} className="switch-btn">
            {isLogin ? 'Kayıt Ol' : 'Giriş Yap'}
          </button>
        </p>
      </div>
    </div>
  );
}
