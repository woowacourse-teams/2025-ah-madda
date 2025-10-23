import { Helmet } from 'react-helmet-async';

type SEOProps = {
  title: string;
  description?: string;
  eventId?: number;
  organizationId?: number;
};

export const SEO = ({
  title,
  description = '아맞다! 팀 이벤트 또 놓쳤다고? – 미리 알려드릴게요. 😎',
  eventId,
  organizationId,
}: SEOProps) => {
  return (
    <Helmet>
      <meta property="og:type" content="website" />
      <meta property="og:title" content={title} />
      <meta property="og:description" content={description} />
      <meta property="og:url" content={`https://ahmadda.com/${organizationId}/event/${eventId}`} />
      <meta property="og:image" content="/main.webp" />
      <meta name="viewport" content="width=device-width, initial-scale=1.0" />
      <link rel="icon" type="image/png" href="/favicon.png" media="(prefers-color-scheme: light)" />
      <link rel="manifest" href="/manifest.json" />
    </Helmet>
  );
};
