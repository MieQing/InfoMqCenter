import React, { PureComponent } from 'react';
import DocumentTitle from 'react-document-title';
import SiderCustom from '../../common/sidercustom';
import HeaderCustom from '../../common/headerCustom';
import { Layout } from 'antd';
import { connect } from 'react-redux';
import { actionCreators } from './store/index'
import Routes from '../../routes';
import RouteConfigs from '../../routes/config';
import BreadConfigs from '../../routes/breadconfig';
import BreadcrumbCustom from '../../common/breadcrumbCustom'


const { Content } = Layout;

class Home extends PureComponent {

    constructor(props) {
        super(props);
        this.handleGetMenuList = this.handleGetMenuList.bind(this);
        this.handleGetBreadList = this.handleGetBreadList.bind(this);
    }


    handleGetMenuList() {
        this.props.changeMenuLsit(JSON.stringify(RouteConfigs));
    }


    handleGetBreadList() {
        this.props.changeBreadLsit(JSON.stringify(BreadConfigs));
    }

    componentWillMount(){
        this.handleGetMenuList();
        this.handleGetBreadList();
    }
		 


    render() {
        return (
            <DocumentTitle
                title={"队列平台"} >
                <Layout>
                    <SiderCustom collapsed={false} />
                    <Layout style={{ flexDirection: 'column' }}>
                        <HeaderCustom />
                        <Content style={{ margin: '0 16px', overflow: 'initial', flex: '1 1 0' }}>
                            <BreadcrumbCustom />
                            <Routes />
                        </Content>
                    </Layout>
                </Layout>
            </DocumentTitle>
        )

    }
}

const mapDispatch = (dispatch) => ({
    changeMenuLsit(listJson) {
        dispatch(actionCreators.menuLsit(listJson));
    },
    changeBreadLsit(breadlist) {
        dispatch(actionCreators.breadLsit(breadlist));
    },
});


export default connect(null, mapDispatch)(Home);