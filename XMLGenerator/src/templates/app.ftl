import { BrowserRouter } from "react-router-dom";

import Header from "./Header.jsx"
import AppRouter from "./Router.jsx"

function App() {
  return (
    <BrowserRouter>
      <Header />
      <AppRouter />
    </BrowserRouter>
  );
}

export default App;
