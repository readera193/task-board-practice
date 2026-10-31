import AuthPage from './components/AuthPage';
import TaskBoard from './components/TaskBoard';
import { useAuth } from './hooks/useAuth';

import './App.css';

function App() {
  const { currentUser, initializing, demoLoginEnabled, login, demoLogin, register, logout } =
    useAuth();

  if (initializing) {
    return <div className="loading-screen">載入中...</div>;
  }

  if (!currentUser) {
    return (
      <AuthPage
        demoLoginEnabled={demoLoginEnabled}
        onLogin={login}
        onDemoLogin={demoLogin}
        onRegister={register}
      />
    );
  }

  return <TaskBoard currentUser={currentUser} onLogout={logout} />;
}

export default App;
