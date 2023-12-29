import { ConnectedRouter } from 'connected-react-router';
import ReactDOM from 'react-dom';
import { Provider } from 'react-redux';
import { PersistGate } from 'redux-persist/es/integration/react';

import { initGA } from './ga';
import { history, persistor, store } from './modules/store';

import './styles/index.scss';

import App from './routes/App';

require('typeface-open-sans');
require('typeface-roboto');

initGA(history);

ReactDOM.render(
  <Provider store={store}>
    <PersistGate persistor={persistor}>
      <ConnectedRouter history={history}>
        <App />
      </ConnectedRouter>
    </PersistGate>
  </Provider>,
  document.getElementById('root')
);
