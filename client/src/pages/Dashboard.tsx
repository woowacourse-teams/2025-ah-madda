import React from 'react';

import { useGoogleAuth } from '../hooks/useGoogleAuth';

export const Dashboard: React.FC = () => {
  const { user, logout, isAuthenticated } = useGoogleAuth();

  if (!isAuthenticated || !user) {
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
        <h2>로그인이 필요합니다</h2>
        <a
          href="/login"
          style={{
            padding: '12px 24px',
            backgroundColor: '#4285f4',
            color: 'white',
            textDecoration: 'none',
            borderRadius: '4px',
          }}
        >
          로그인하기
        </a>
      </div>
    );
  }

  return (
    <div
      style={{
        maxWidth: '800px',
        margin: '0 auto',
        padding: '24px',
        fontFamily: 'system-ui, -apple-system, sans-serif',
      }}
    >
      <header
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: '32px',
          padding: '16px',
          backgroundColor: 'white',
          borderRadius: '8px',
          boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
        }}
      >
        <h1 style={{ fontSize: '24px', color: '#333', margin: 0 }}>대시보드</h1>
        <button
          onClick={logout}
          style={{
            padding: '8px 16px',
            backgroundColor: '#f44336',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
            fontSize: '14px',
          }}
        >
          로그아웃
        </button>
      </header>

      <div
        style={{
          backgroundColor: 'white',
          padding: '24px',
          borderRadius: '8px',
          boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
        }}
      >
        <h2 style={{ fontSize: '20px', marginBottom: '16px', color: '#333' }}>사용자 정보</h2>

        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            gap: '16px',
            marginBottom: '16px',
          }}
        >
          <img
            src={user.picture}
            alt={user.name}
            style={{
              width: '64px',
              height: '64px',
              borderRadius: '50%',
              objectFit: 'cover',
            }}
          />
          <div>
            <h3 style={{ fontSize: '18px', margin: '0 0 4px 0', color: '#333' }}>{user.name}</h3>
            <p style={{ fontSize: '14px', margin: 0, color: '#666' }}>{user.email}</p>
          </div>
        </div>

        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
            gap: '16px',
            marginTop: '24px',
          }}
        >
          <div
            style={{
              padding: '16px',
              backgroundColor: '#f5f5f5',
              borderRadius: '4px',
            }}
          >
            <strong style={{ color: '#333' }}>사용자 ID:</strong>
            <p style={{ margin: '4px 0 0 0', color: '#666', fontSize: '14px' }}>{user.id}</p>
          </div>

          <div
            style={{
              padding: '16px',
              backgroundColor: '#f5f5f5',
              borderRadius: '4px',
            }}
          >
            <strong style={{ color: '#333' }}>이메일:</strong>
            <p style={{ margin: '4px 0 0 0', color: '#666', fontSize: '14px' }}>{user.email}</p>
          </div>
        </div>
      </div>

      <div
        style={{
          marginTop: '24px',
          padding: '16px',
          backgroundColor: '#e8f5e8',
          borderRadius: '8px',
          border: '1px solid #4caf50',
        }}
      >
        <p style={{ margin: 0, color: '#2e7d32' }}>
          ✅ Google OAuth 로그인이 성공적으로 완료되었습니다!
        </p>
      </div>
    </div>
  );
};
