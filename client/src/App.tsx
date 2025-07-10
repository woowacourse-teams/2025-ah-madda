import React, { useState } from 'react';

const App = () => {
  const [count, setCount] = useState(0);

  const handleButtonClick = () => {
    setCount(count + 1);
  };

  return (
    <div className="container">
      <h1>Hello World</h1>
      <button onClick={handleButtonClick}>Click me</button>
      <p>{count}</p>
      <h1>React + TypeScript + Webpack</h1>
    </div>
  );
};

export default App;
