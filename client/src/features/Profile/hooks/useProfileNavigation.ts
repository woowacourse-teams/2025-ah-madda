import { useNavigate, useParams } from 'react-router-dom';

export const useProfileNavigation = () => {
  const navigate = useNavigate();
  const { organizationId } = useParams();

  const goBack = () => navigate(-1);

  return {
    organizationId: organizationId ? Number(organizationId) : 0,
    goBack,
  };
};
