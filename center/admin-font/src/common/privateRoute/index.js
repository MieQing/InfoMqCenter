import React, {Component} from 'react';
import {Route, withRouter} from 'react-router-dom';



class PrivateRoute extends Component {
    constructor(props) {
        super(props);
    }

    componentWillMount() { 
    }

    render() {
        let { component: Component, ...rest} = this.props;
        return  (
            <Route {...rest} render={(props) =>
                ( <Component {...props} /> )} />
        )

    }
}

export default withRouter(PrivateRoute); 