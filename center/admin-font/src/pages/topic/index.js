import React, { Component } from 'react';
import { Table, Button, Form, Divider, Popconfirm, message } from 'antd';
import { AxiosAjax } from '../../axios/index';
import { GetTopicListUrl,DeleteTopicUrl } from '../../axios/config';//, GetTopicInfoUrl
import AddTopic from './add';
import TopicInfo from './info';

const data = [];
const title = () => 'Topic管理';
const showHeader = true;
const scroll = { y: 900 };


class Topic extends Component {
    constructor(props) {
        super(props);
        this.handleGetdata = this.handleGetdata.bind(this);
        this.handleDelete = this.handleDelete.bind(this);
        this.CloseAdd = this.CloseAdd.bind(this);
        this.handleAdd = this.handleAdd.bind(this);
        this.CloseView=this.CloseView.bind(this);
        this.handleView=this.handleView.bind(this);
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
            selectTopic:'',
        };

        this.columns = [
            {
                title: 'Topic',
                dataIndex: 'topicName',
                key: 'topicName',
                width: 200
            },
            {
                title: '描述',
                dataIndex: 'description',
                key: 'description',
                width: 600
            },
            {
                title: '分区数',
                dataIndex: 'partition',
                key: 'partition',
                width: 200
            },
            {
                title: '副本数',
                dataIndex: 'replicationFactor',
                key: 'replicationFactor',
                width: 200
            },
            {
                title: '状态',
                dataIndex: 'status',
                key: 'status',
                width: 100,
                render: (text, record) => {
                    return ( 
                        record.status==1?
                        <span>可用</span>:
                        <span style={{color:'red'}}>未同步Kafka</span>
                    )
                }
            },
            {
                title: '操作',
                key: 'Operate',
                render: (text, record) => {
                    return (
                        (this.state.hasData && record.topicName) ? (
                            <span>
                                <a onClick={() => this.handleView(record.topicName)} >查看</a>
                                <Divider type="vertical" />
                                <Popconfirm okText="确定" cancelText="取消" title="确定要删除?" onConfirm={() => this.handleDelete(record.topicName)}>
                                    <a> 删除 </a>
                                </Popconfirm>
                            </span>
                        ) : null
                    )
                }

            }
        ];

    }


    handleView = (topicName) => {
       this.setState({
           selectTopic:topicName
       }) 
    }

    CloseView=()=>{
        this.setState({
            selectTopic:""
        }) 
    }


    handleDelete = (topicName) => {
        let param={type:'delete',url:DeleteTopicUrl+"?topicName="+topicName} 
        AxiosAjax(param)
            .then(res => {
                switch (res.data.code) {
                    case 200:
                        message.success("删除成功", 1);
                        this.handleGetdata();
                        break;
                    default:
                        message.warn(res.data.message);
                        break;
                }
            })
            .catch(err => {
                message.warn(err);
            })
    }

    handleAdd = () => {
        this.setState({
            isAdd: true
        })
    }

    CloseAdd = () => {
        this.setState({
            isAdd: false
        })
    }

    componentDidMount() {
        this.handleGetdata()
    }

    handleGetdata() {
        let param={type:"get",url:GetTopicListUrl};
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


    render() {
        const { isAdd ,selectTopic} = this.state;
        return (
            < div >
                < div
                    className="components-table-demo-control-bar" >
                    {isAdd ? <AddTopic close={this.CloseAdd} flushtable={this.handleGetdata} /> : null}
                    {selectTopic!=""?<TopicInfo topicName={selectTopic} close={this.CloseView} />:null}
                    <Form
                        layout="inline" >
                        <Form.Item
                            label="" >
                            <Button onClick={this.handleAdd}  type="primary"  style={{marginBottom: 16}}>
                                新增Topic
                            </Button>
                        </Form.Item>
                    </Form>
                </div>
                < Table {...this.state}
                    rowKey="topicName"
                    columns={this.columns}
                    dataSource={this.state.hasData ? this.state.data : null}
                />
            </div>
        )
            ;
    }
}

export default Topic;