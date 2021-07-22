
import React, { Component } from 'react';
import { Table, Button, Form, Divider, Popconfirm, Select,DatePicker,message } from 'antd';
import moment from 'moment'
import { AxiosAjax } from '../../axios/index';
import { GetErrorList,GetTopicListUrl ,IgnoreErrorUrl,ErrorReSendUrl} from '../../axios/config'; 
const { RangePicker } = DatePicker;

const { Option } = Select
const data = [];
const title = () => '错误信息管理';
const showHeader = true;
const scroll = { y: 900 };

class ErrorList extends Component {
    constructor(props) {
        super(props);
        this.handleGetdata = this.handleGetdata.bind(this);
        this.handleGetTopic= this.handleGetTopic.bind(this);
        this.handleChangePage= this.handleChangePage.bind(this);
        this.handleSearch= this.handleSearch.bind(this);
        this.state = {
            data:[],
            hasData:false,
            bordered: true,
            loading: false,
            size: 'default',
            title,
            showHeader,
            scroll,
            pageSize: 20,
            current: 1,
            total: 0,
            topicData:[]
        };

        this.columns = [
            {
                title: '操作',
                key: 'Operate',
                render: (text, record) => {
                    return (
                        <span>
                            <Popconfirm okText="确定" cancelText="取消"  title="确定要重试?"
                                onConfirm={()=>this.handleReSend((record.id))}>
                                <a style={{color:'blue'}} >重试</a>
                            </Popconfirm>
                            < Divider type="vertical" />
                            <a style={{color:'blue'}}  onClick={()=>this.handleIgnore(record.id)} >忽略</a>
                        </span>
                    )
                },
                width: 150

            },
            {
                title: '主题',
                dataIndex: 'topicName',
                key: 'topicName',
                width: 150
            },
            {
                title: '创建时间',
                dataIndex: 'createTime',
                key: 'createTime',
                width: 300,
                render: (text, record) => {
                    return moment(text).format('YYYY-MM-DD HH:mm:ss')
                }
            },
            {
                title: '任务编码',
                dataIndex: 'taskCode',
                key: 'taskCode',
                width: 100
            },
            {
                title: '状态',
                dataIndex: 'status',
                key: 'status',
                width: 100,
                render: (text, record) => {
                    if (text == 1) {
                        return <span style={{color: 'blue'}}> 已处理 </span>
                    } 
                    else if (text == 2) {
                        return <span style={{color: 'green'}}>忽略 </span>
                    }else if (text == 0) {
                        return <span style={{color: 'red'}}>未处理 </span>
                    }
                }
            },
            {
                title: '消息体',
                dataIndex: 'mesBody',
                key: 'mesBody', 
            },
            {
                title: '错误消息',
                dataIndex: 'errorMsg',
                key: 'errorMsg', 
            }
        ];

    }


    formRef = React.createRef();

    componentDidMount() {
        this.handleGetdata(this.state.current, this.state.pageSize)
        this.handleGetTopic();
    }


    handleReSend=(id)=>{
        let data = {
            "id": id,
        }
        let param={ url:ErrorReSendUrl,type:"put",data:data}
        AxiosAjax(param)
                .then(res => {
                    switch (res.data.code) {
                        case 200:
                            message.success("重试成功，请等待结果", 1, () => {
                                this.handleGetdata(this.state.current, this.state.pageSize)
                            });
                            break;
                        default:
                            message.warn(res.data.message);
                    }

                })
                .catch(err => {
                    message.warn(err);
                });
    }

    handleIgnore=(id)=>{
        let data = {
            "id": id,
        }
        let param={ url:IgnoreErrorUrl,type:"put",data:data}
        AxiosAjax(param)
                .then(res => {
                    switch (res.data.code) {
                        case 200:
                            message.success("忽略成功", 1, () => {
                                this.handleGetdata(this.state.current, this.state.pageSize)
                            });
                            break;
                        default:
                            message.warn(res.data.message);
                    }

                })
                .catch(err => {
                    message.warn(err);
                });
    }


    handleChangePage(page, pageSize) {
        this.handleGetdata(page, pageSize);
    }
    handleSearch() {
        this.handleGetdata(1, this.state.pageSize);
    }

    handleGetTopic() {
        let param={type:"get",url:GetTopicListUrl};
        AxiosAjax(param)
            .then(res => {
                this.setState({
                    loading: false,
                    topicData: res.data,
                })
            })
            .catch(err => {
                this.setState({
                    loading: false,
                    topicData: [],
                })
            })
    }

    handleGetdata(page, pageSize){ 
        let createTimeStart=this.formRef.current.getFieldValue('createTime')[0].format('YYYY-MM-DD HH:mm:ss')
        let createTimeEnd=this.formRef.current.getFieldValue('createTime')[1].format('YYYY-MM-DD HH:mm:ss')
        let status=this.formRef.current.getFieldValue('status')
        let topicName=this.formRef.current.getFieldValue('topicName') == undefined ? "" : this.formRef.current.getFieldValue('topicName')
        if(page==0)
          page=1;
        let param={type:"get",url:GetErrorList+'?createTimeStart='
                            +createTimeStart+'&createTimeEnd='+createTimeEnd
                            +'&status='+status+'&topicName='+topicName+'&page='+page+'&pageSize='+pageSize};
        AxiosAjax(param)
            .then(res => {
                this.handleShowData(res.data,page,pageSize)
            })
            .catch(err => {
                this.handleShowData([])
            })
    }
    
    handleShowData(getData,page,pageSize){
        console.log(getData)
      let hasData=false;
      if(getData.mes.length>0){
        hasData=true;
      }
      this.setState({
        loading: false,
        data:getData.mes,
        hasData:hasData,
        total:getData.count,
        current: page,
        pageSize:pageSize,
        editId:'',
      })
    }
 


    render() {
        const {  total, pageSize, current,topicData } = this.state;
        return (
            <div>
                  <div className="components-table-demo-control-bar" style={{padding:'10px 10px 10px 10px'}}>
                    <Form  layout="inline"
                        ref={this.formRef} >
                        <Form.Item
                            label="时间"
                            name="createTime"
                            initialValue={[moment().add(-30, 'day'), moment()]}
                        >
                           <RangePicker 
                                showTime 
                                format="YYYY-MM-DD HH:mm:ss" />
                        </Form.Item>
                        <Form.Item
                            label="状态"
                            name="status"
                            initialValue='0'
                        >
                            <Select placeholder="状态">
                                <Option value="0">未处理</Option>
                                <Option value="1">已处理</Option>
                                <Option value="2">忽略</Option>
                            </Select> 
                        </Form.Item>
                        <Form.Item
                            label="主题"
                            name="topicName"
                        >
                            <Select style={{width:'200px'}} placeholder="主题">
                                <Option value="">请选择</Option>
                                {
                                    topicData.map(d => (
                                        <Option value={d.topicName} key={d.topicName} > {d.topicName} </Option>
                                    ))
                                }
                            </Select> 
                        </Form.Item>
                        <Form.Item label=""> 
                            <Button onClick={this.handleSearch} type="primary" style={{ marginBottom: 16 }}>
                            查询  
                            </Button>
                        </Form.Item>
                    </Form>
                    <Table {...this.state}
                                rowKey="id" 
                                pagination={{
                                    showSizeChanger: true,
                                    showQuickJumper: true,
                                    pageSize: parseInt(pageSize),
                                    total: parseInt(total),
                                    current: parseInt(current),
                                    onChange: this.handleChangePage,
                                    onShowSizeChange: this.handleChangePage,
                                    showTotal: ((total) => {
                                        return `共 ${total} 条数据`;
                                    }),
                                }} 
                                columns={this.columns} dataSource={this.state.hasData ? this.state.data : null} 
                        /> 
                </div>
            </div>
        )
            ;
    }
}
 

export default ErrorList;