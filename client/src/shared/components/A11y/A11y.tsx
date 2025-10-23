export const INTRO_DESC_ID = 'event-intro-desc';

type A11yProps = {
  introDesc?: string;
};

const srHiddenStyle: React.CSSProperties = {
  position: 'absolute',
  width: 1,
  height: 1,
  margin: -1,
  padding: 0,
  border: 0,
  clip: 'rect(0 0 0 0)',
  overflow: 'hidden',
};

export const A11y = ({ introDesc }: A11yProps) => {
  return (
    <>
      <div
        id="a11y-live-assertive"
        role="status"
        aria-live="assertive"
        aria-atomic="true"
        style={srHiddenStyle}
      />
      <div
        id="a11y-live-polite"
        role="status"
        aria-live="polite"
        aria-atomic="true"
        style={srHiddenStyle}
      />
      <div id={INTRO_DESC_ID} style={srHiddenStyle}>
        {introDesc}
      </div>
    </>
  );
};
