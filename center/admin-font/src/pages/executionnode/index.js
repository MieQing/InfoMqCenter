import React, { Component } from 'react';
import { Table, Button, Form, Divider, Popconfirm, message,notification } from 'antd';
import { AxiosAjax } from '../../axios/index';
import { GetExecutionList,DeleteExecution,GetWorkThread} from '../../axios/config'; 
import Add from './add';
import Update from './update';

const FormItem = Form.Item;

const data = [];
const title = () => '执行器管理';
const showHeader = true;
const scroll = { y: 900 };


class Execution extends Component {
    constructor(props) {
        super(props);
        this.handleGetdata = this.handleGetdata.bind(this);
        this.handleGetThread=this.handleGetThread.bind(this);
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
            selectid: 0,
        };

        this.columns = [
            {
                title: 'Code',
                dataIndex: 'code',
                key: 'code',
                width: 150
            },
            {
                title: '地址',
                dataIndex: 'address',
                key: 'address',
                width: 300
            },
            {
                title: '名称',
                dataIndex: 'name',
                key: 'name',
                width: 300
            },
            {
                title: '状态',
                dataIndex: 'status',
                key: 'status',
                width: 300,
                render: (text, record) => {
                    if (text == 1) {
                        return <span > 启用 </span>
                    } else if (text == 0) {
                        return <span style={{color: 'red'}}>禁用 </span>
                    }
                }
            },
            {
                title: '操作',
                key: 'Operate',
                render: (text, record) => {
                    return (
                        (this.state.hasData && record.code) ? (
                            <span>
                                <a onClick={()=>this.handleUpdate(record.id)} >修改</a>
                                < Divider type="vertical" />
                                <Popconfirm okText="确定" cancelText="取消"  title="确定要删除?"
                                    onConfirm={()=>this.handleDelete(record.id, record.code)}>
                                    <a > 删除 </a>
                                </Popconfirm>
                                < Divider type="vertical" />
                                <a onClick={()=>this.handleGetThread(record.code)} >执行线程</a>
                            </span>
                        ) :
                            null
                    )
                }

            }
        ];

    }


    handleGetThread = (code) => {
        let param={type:'get',url:GetWorkThread+"?code="+code} 
        AxiosAjax(param)
            .then(res => {
                const args = {
                    message: '当前执行线程',
                    description:
                        res.data,
                    duration: 0,
                    style: {
                        width: 1200,
                        marginLeft: 335 - 1600,
                    },
                };
                notification.open(args);
            })
            .catch(err => {
            })


    }

    handleUpdate = (id) => {
        this.setState({
            selectid: id
        })
    }

    handleDelete = (id, code) => {
        let param={type:'delete',url:DeleteExecution+"?id="+id+"&code="+code} 
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

    CloseUpdate = () => {
        this.setState({
            selectid: 0
        })
    }

    componentDidMount() {
        this.handleGetdata()
    }

    handleGetdata() {
        let param={type:'get',url:GetExecutionList} 
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
        const { isAdd, selectid } = this.state;
        return (
            <div>
                <div>
                    {selectid > 0 ?<Update close={this.CloseUpdate} id={selectid} flushtable={this.handleGetdata} /> : null}
                    {isAdd ?<Add close={this.CloseAdd} flushtable={this.handleGetdata} /> : null}
                    <Form
                        layout="inline" >
                        <Form.Item
                            label="" >
                            <Button onClick={this.handleAdd} type="primary" style={{marginBottom: 16}}>
                                新增执行器
                            </Button>
                        </Form.Item>
                    </Form>
                </div>
                < Table   {...this.state}
                    rowKey="id"
                    columns={this.columns}
                    dataSource={this.state.hasData ? this.state.data : null}
                />
            </div>
        )
            ;
    }
}
 

export default Execution;