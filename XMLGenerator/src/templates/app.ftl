import Header from "./components/Header"
import AppRouter from "./containers/router/index"
import {BrowserRouter} from "react-router-dom";

function App() {
  return (
    <BrowserRouter>
      <Header />
      <AppRouter />
    </BrowserRouter>
  );
}

export default App;
