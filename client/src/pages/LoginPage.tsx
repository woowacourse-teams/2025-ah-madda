import React from 'react';

import { GoogleLoginButton } from '../components/GoogleLoginButton';

export const LoginPage: React.FC = () => {
  const handleLoginStart = () => {
    console.log('Google 로그인 시작');
  };

  return (
    <div
      style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        flexDirection: 'column',
        gap: '32px',
        backgroundColor: '#f5f5f5',
      }}
    >
      <div
        style={{
          backgroundColor: 'white',
          padding: '48px',
          borderRadius: '12px',
          boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
          textAlign: 'center',
          maxWidth: '400px',
          width: '100%',
        }}
      >
        <h1
          style={{
            fontSize: '28px',
            fontWeight: '600',
            color: '#333',
            marginBottom: '16px',
          }}
        >
          로그인
        </h1>

        <p
          style={{
            fontSize: '16px',
            color: '#666',
            marginBottom: '32px',
            lineHeight: '1.5',
          }}
        >
          Google 계정으로 간편하게 로그인하세요
        </p>

        <GoogleLoginButton onLoginStart={handleLoginStart} />
      </div>
    </div>
  );
};
