import { Outlet } from 'react-router-dom';

// S.TODO: 추후 div -> AppLayout 컴포넌트로 변경 예정
export const App = () => {
  return (
    <div>
      <Outlet />
    </div>
  );
};
