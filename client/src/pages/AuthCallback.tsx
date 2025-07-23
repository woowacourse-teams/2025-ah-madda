import React, { useEffect } from 'react';

import { useNavigate } from 'react-router-dom';

import { useGoogleAuth } from '../hooks/useGoogleAuth';

export const AuthCallback = () => {
  const navigate = useNavigate();
  const { handleCallback, isLoading, error } = useGoogleAuth();

  useEffect(() => {
    const processCallback = async () => {
      const urlParams = new URLSearchParams(window.location.search);
      const code = urlParams.get('code');

      if (!code) {
        navigate('/', { replace: true });
        return;
      }

      await handleCallback();

      const token = localStorage.getItem('access_token');
      if (token) {
        navigate('/', { replace: true });
      }
    };

    processCallback();
  }, []);

  if (isLoading) {
    return (
      <div
        style={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '100vh',
          flexDirection: 'column',
          gap: '16px',
        }}
      >
        <div
          style={{
            width: '40px',
            height: '40px',
            border: '4px solid #f3f3f3',
            borderTop: '4px solid #4285f4',
            borderRadius: '50%',
            animation: 'spin 1s linear infinite',
          }}
        />
        <p>로그인 처리 중...</p>
        <style>{`
          @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
          }
        `}</style>
      </div>
    );
  }

  if (error) {
    return (
      <div
        style={{
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          height: '100vh',
          flexDirection: 'column',
          gap: '16px',
        }}
      >
        <h2>로그인 오류</h2>
        <p>{error}</p>
        <button
          onClick={() => navigate('/')}
          style={{
            padding: '12px 24px',
            backgroundColor: '#4285f4',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
          }}
        >
          홈으로 돌아가기
        </button>
      </div>
    );
  }

  return <div>AuthCallback</div>;
};
