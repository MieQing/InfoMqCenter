/**
 * 
 */
import React, { Component } from 'react';
import { Route, Redirect, Switch } from 'react-router-dom';
import AllComponents from '../allcomponents';
import queryString from 'query-string';
import { connect } from 'react-redux';

 class CRouter extends Component {
    render() {
        const { onRouterChange,menuList } = this.props;
        const list=JSON.parse(menuList);
        if(menuList!=null){ 
            return (
                <Switch>
                    {
                        Object.keys(list).map(key => 
                            list[key].map(r => {
                                const route = r => {
                                    const Component = AllComponents[r.component];
                                    return (
                                        <Route
                                            key={r.route || r.key}
                                            exact
                                            path={r.route || r.key}
                                            render={props => {
                                                const reg = /\?\S*/g;
                                                // 匹配?及其以后字符串
                                                const queryParams = window.location.hash.match(reg);
                                                // 去除?的参数
                                                const { params } = props.match;
                                                Object.keys(params).forEach(key => {
                                                    params[key] = params[key] && params[key].replace(reg, '');
                                                });
                                                props.match.params = { ...params };
                                                const merge = { ...props, query: queryParams ? queryString.parse(queryParams[0]) : {} };
                                                // 回传route配置
                                                onRouterChange && onRouterChange(r);
                                                return <Component {...merge} />
                                            }}
                                        />
                                    )
                                }
                                return r.component ? route(r) : r.subs.map(r => route(r));
                            })
                        )
                    }
                </Switch>
            )
        }
        else{
            return(
                null
            )
        }
        
    }
}


const mapState = (state) => {
	return {
		menuList: state.getIn(['home','menuList'])
	}
}

export default connect(mapState, null)(CRouter);