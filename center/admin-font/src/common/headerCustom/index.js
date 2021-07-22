import React, { Component, Fragment } from 'react';
import { Menu, Layout, Avatar, Button } from 'antd';
import { connect } from 'react-redux';
import { withRouter } from 'react-router-dom'; 
const { Header } = Layout;
const SubMenu = Menu.SubMenu;

class HeaderCustom extends Component {
    constructor(props) {
        super(props);  

    }  
    render() {
        return (
            <Header className="custom-theme header" >
                <Menu
                    mode="horizontal"
                    style={{ lineHeight: '60px', float: 'right' }}
                    onClick={this.menuClick}
                >
                    <SubMenu title={<Fragment><Avatar style={{ color: '#FFFFFF', backgroundColor: '#0099FF' }} size="default" >Info</Avatar></Fragment>}> 
                    </SubMenu>
                </Menu>
            </Header>
        )
    }
}




export default withRouter(connect(null, null)(HeaderCustom));