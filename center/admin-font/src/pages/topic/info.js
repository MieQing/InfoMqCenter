import React, { Component } from 'react';
import { Table, Modal,Spin } from 'antd';
import { AxiosAjax } from '../../axios/index';
import { GetTopicInfoUrl } from '../../axios/config';
 

const data = [];
const title = () => 'Topic描述信息';
const showHeader = true;
const scroll = { y: 900 };


class TopicInfo extends Component {
    constructor(props) {
        super(props);
        this.handleGetdata = this.handleGetdata.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
        this.state = {
            data,
            hasData: false,
            bordered: true,
            loading: false,
            size: 'default',
            title,
            showHeader,
            scroll,
            isEdit: false,
            isAdd: false,
        };

        this.columns = [
            {
                title: 'Topic',
                dataIndex: 'name',
                key: 'name',
                width: 200
            },
            {
                title: '是否内部主题',
                dataIndex: 'internal',
                key: 'internal',
                width: 200,
                render: (text, record) => {
                    return ( 
                        record.internal?
                        <span>是</span>:
                        <span>否</span>
                    )
                }
            },
            {
                title: 'Leader',
                dataIndex: 'leader',
                key: 'leader',
                width: 200
            },
            {
                title: '分区信息',
                dataIndex: 'partition',
                key: 'partition',
                width: 200
            },
            {
                title: '副本信息',
                dataIndex: 'replicas',
                key: 'replicas',
                width: 200
            },
            {
                title: 'ISR信息',
                dataIndex: 'isr',
                key: 'isr',
            },
        ];

    }
 

    componentDidMount() {
        this.handleGetdata()
    }

    handleGetdata() {
        let param={type:"get",url:GetTopicInfoUrl+'?topicName='+this.props.topicName};
        AxiosAjax(param)
            .then(res => {
                this.setState({
                    loading: false,
                    data: res.data,
                    hasData: res.data.length > 0 ? true : false
                })
            })
            .catch(err => {
                this.setState({
                    loading: false,
                    data: [],
                    hasData: false
                })
            })
    }

    handleCancel = () => {
        this.props.close();
    };



    render() {
        const { isAdd } = this.state;
        return (
            <Modal
                okText={"确定"}
                cancelText={"取消"}
                width={1500}
                title={"Topic描述信息"}
                visible={true}
                onOk={this.handleCancel}
                onCancel={this.handleCancel}
            >
                <Spin
                    spinning={this.state.loading} >
                    < Table {...this.state}
                        rowKey="partition"
                        columns={this.columns}
                        dataSource={this.state.hasData ? this.state.data : null}
                    />
                </Spin>
            </Modal>
        )
            ;
    }
}

export default TopicInfo;