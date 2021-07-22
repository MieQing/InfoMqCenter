import React, { Component } from 'react';
import { Provider } from 'react-redux';
import { HashRouter as Router, Switch, Route } from 'react-router-dom';
import Home from './pages/home';
import store from './store';
//import PrivateRoute from "./common/privateRoute";

class App extends Component {
  constructor(props) {
    super(props);
  }



  render() {
    return (   
      <Provider store={store}>
        <Router>
          <Switch>
            <Route path='/' component={Home}></Route>
          </Switch>
        </Router>
      </Provider>
    );
  }
}

export default App;
