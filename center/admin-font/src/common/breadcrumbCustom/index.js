import React, { Component } from 'react';
import { Breadcrumb } from 'antd';
import { Link ,withRouter} from 'react-router-dom';
import { connect } from 'react-redux';
import { blue } from 'ansi-colors';
//具体导航的名称
const breadcrumbNameMap = {
};
class BreadcrumbCustom extends Component {
    render() {
        const { location,breadList } = this.props;
        if(breadList){
            let list=JSON.parse(breadList);
            list.map((x)=>{
                breadcrumbNameMap[x.key]=x.title;
            })
        }
        const pathSnippets = location.pathname.split('/').filter(i => i);
        const extraBreadcrumbItems = pathSnippets.map((_, index) => {
            const url = `/${pathSnippets.slice(0, index + 1).join('/')}`;
            const showname=breadcrumbNameMap[url];
            if(showname!=null){
                return (
                    <Breadcrumb.Item key={url}>
                       <Link to={url}>{showname}</Link>
                    </Breadcrumb.Item>
            
                 );
            }
        });

        return (
            <Breadcrumb style={{color:blue}} separator=">">{extraBreadcrumbItems}</Breadcrumb>
        )
    }
}



const mapState = (state) => {
	return {
		breadList: state.getIn(['home','breadList'])
	}
}
//export default withRouter(SiderCustom);

export default withRouter(connect(mapState, null)(BreadcrumbCustom));