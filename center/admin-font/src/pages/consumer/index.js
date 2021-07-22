import React, { Component, Fragment } from 'react';
import { Row,Col,List,Typography,Card,Table,Button,Form,Divider,Popconfirm,message } from 'antd';
import { AxiosAjax } from '../../axios/index';
import { GetTopicListUrl,GetGroupListUrl,GetConsumerTaskListUrl,DeleteConsumerTaskUrl} from '../../axios/config';  
import  AddGroup  from './addgroup'
import UpdateGroup from './updategroup'
import AddConsumer from './addconsumer'
import UpdateConsumer from './updateconsumer'

const { Text } = Typography;
const scroll = { y: 900 };
class Consumer extends Component {
    constructor(props) {
        super(props); 
        this.handleGetdata=this.handleGetdata.bind(this); 
        this.selectTopic=this.selectTopic.bind(this);
        this.handleAddGroup=this.handleAddGroup.bind(this);
        this.closeAddGroup=this.closeAddGroup.bind(this);
        this.handleGetGroupdata=this.handleGetGroupdata.bind(this);
        this.handleUpdateGroup=this.handleUpdateGroup.bind(this);
        this.closeUpdateGroup=this.closeUpdateGroup.bind(this);
        this.handleConsumer=this.handleConsumer.bind(this);
        this.handleAddConsumer=this.handleAddConsumer.bind(this);
        this.handleUpdateConsumer=this.handleUpdateConsumer.bind(this);
        this.handleGetTaskdata=this.handleGetTaskdata.bind(this);
        this.handleDeleteTask=this.handleDeleteTask.bind(this);
        this.state={
            data:[],
            selectTopicName:'',
            isAddGroup:false,
            groupData:[],
            hasGroupData:false,
            isUpdateGroup:false,
            selectGroupId:'',
            hasConsumerData:false,
            consumerData:[],
            isAddConsumer:false,
            isUpdateConsumer:false,
            selectConsumerId:'',
        }


        this.columns = [
            {
                title: 'Topic',
                dataIndex: 'topicName',
                key: 'topicName',
                width: 100
            },
            {
                title: '消费者组',
                dataIndex: 'groupName',
                key: 'groupName',
                width: 200
            },
            {
                title: 'OffsetReset',
                dataIndex: 'offsetReset',
                key: 'offsetReset',
                width: 100
            },
            {
                title: '分区策略',
                dataIndex: 'par_assign_strategy',
                key: 'par_assign_strategy',
                width: 100
            },
            {
                title: '描述',
                dataIndex: 'description',
                key: 'description',
                width: 300
            }, 
            {
                title: '通知人',
                dataIndex: 'notifier',
                key: 'notifier',
                width: 300
            },
            {
                title: '操作',
                key: 'Operate',
                render: (text, record) => {
                    return (
                        (record.id) ? (
                            <span>
                                <a onClick={() => this.handleUpdateGroup(record.id)} >
                                    修改
                                </a>
                                < Divider type="vertical" /> <a onClick={() => this.handleConsumer(record.id)} >
                                    消费者维护
                                </a>
                            </span>
                        ) :
                            null
                    )
                }

            }
        ];

        this.consumerColumn = [
            {
                title: '任务编码',
                dataIndex: 'taskCode',
                key: 'taskCode',
                width: 90
            },
            {
                title: '执行节点',
                dataIndex: 'execCode',
                key: 'execCode',
                width: 90
            },
            {
                title: '状态',
                dataIndex: 'status',
                key: 'status',
                width: 70,
                render: (text, record) => {
                    if (text == 1) {
                        return <span > 启用 </span>
                    } else if (text == 0) {
                        return <span style={{color: 'red'}}>禁用 </span>
                    }
                }
            },
            {
                title: '获取方式',
                dataIndex: 'getType',
                key: 'getType',
                width: 90
            },
            {
                title: '调用方式',
                dataIndex: 'operateType',
                key: 'operateType',
                width: 90
            }, 
            {
                title: '调用地址',
                dataIndex: 'operateUrl',
                key: 'operateUrl',
                width: 300
            },
            {
                title: '批消费数',
                dataIndex: 'batchNumber',
                key: 'batchNumber',
                width: 70
            },
            {
                title: '超时时间',
                dataIndex: 'operateOutTime',
                key: 'operateOutTime',
                width: 70
            }, 
            {
                title: 'heartbeat.interval.ms',
                dataIndex: 'hea_int_ms',
                key: 'hea_int_ms',
                width: 120
            },
            {
                title: 'session.timeout.ms',
                dataIndex: 'ses_timeout_ms',
                key: 'ses_timeout_ms',
                width: 120
            },
            {
                title: 'max.poll.interval.ms',
                dataIndex: 'max_poll_ms',
                key: 'max_poll_ms',
                width: 120
            },
            {
                title: '操作',
                key: 'Operate',
                render: (text, record) => {
                    return (
                        (record.id) ? (
                            <span>
                                <a onClick={() => this.handleUpdateConsumer(record.id)} >
                                    修改
                                </a>
                                < Divider type="vertical" />
                                <Popconfirm okText="确定" cancelText="取消" title="确定要删除?" onConfirm={() => this.handleDeleteTask(record.id)}>
                                    <a> 删除 </a>
                                </Popconfirm>
                            </span>
                        ) :
                            null
                    )
                }

            }
        ];
    }

    
     
    componentDidMount() {
        this.handleGetdata()
    }

    handleConsumer=(id)=>{
        this.setState({
            selectGroupId: id,
        },()=>{
          this.handleGetTaskdata();
        })
    }

    handleGetdata() {
        let param={type:"get",url:GetTopicListUrl};
        AxiosAjax(param)
            .then(res => {
                this.setState({
                    data: res.data,
                })
            })
            .catch(err => {
                this.setState({
                    data: [],
                })
            })
    }

    handleGetGroupdata(){
        let param={type:"get",url:GetGroupListUrl+'?topicName='+this.state.selectTopicName};
        AxiosAjax(param)
            .then(res => {
                this.setState({
                    groupData: res.data,
                    hasGroupData: res.data.length > 0 ? true : false
                })
            })
            .catch(err => {
                this.setState({
                    groupData: [],
                    hasGroupData: false
                })
            })
    }

    handleGetTaskdata(){
        let param={type:"get",url:GetConsumerTaskListUrl+'?groupId='+this.state.selectGroupId};
        AxiosAjax(param)
            .then(res => {
                this.setState({
                    consumerData: res.data,
                    hasConsumerData: res.data.length > 0 ? true : false
                })
            })
            .catch(err => {
                this.setState({
                    consumerData: [],
                    hasConsumerData: false
                })
            })
    }


    selectTopic=(topicName)=>{
        this.setState({
            selectTopicName:topicName,
            selectGroupId:'',
        },()=>{
            this.handleGetGroupdata();
        })
    }

    handleUpdateGroup=(id)=>{
        this.setState({
            isUpdateGroup:true,
            selectGroupId:id,
        })
    }

    
    closeUpdateGroup=()=>{
        this.setState({
            isUpdateGroup:false,
        })
    }

    closeAddGroup=()=>{
        this.setState({
            isAddGroup:false,
        })
    }

    closeUpdateConsumer=()=>{
        this.setState({
            isUpdateConsumer:false,
        })
    }
    
    closeAddConsumer=()=>{
        this.setState({
            isAddConsumer:false,
        })
    }

    
    handleAddGroup=()=>{
        this.setState({
            isAddGroup:true,
        })
    }

    handleUpdateConsumer=(id)=>{
        this.setState({
            isUpdateConsumer:true,
            selectConsumerId:id,
        })
    }

    handleAddConsumer=()=>{
        this.setState({
            isAddConsumer:true,
        })
    }


    handleDeleteTask = (id) => {
        let param={type:'delete',url:DeleteConsumerTaskUrl+"?id="+id} 
        AxiosAjax(param)
            .then(res => {
                switch (res.data.code) {
                    case 200:
                        message.success("删除成功", 1);
                        this.handleGetTaskdata();
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


    render() {
         const{ data,selectTopicName,isAddGroup,groupData,hasGroupData
            ,selectGroupId,isUpdateGroup,hasConsumerData
            ,consumerData,isAddConsumer,isUpdateConsumer,selectConsumerId }=this.state
        return (
             <Fragment>
                 {isAddGroup?<AddGroup topicName={selectTopicName} close={this.closeAddGroup} flushtable={this.handleGetGroupdata} />:null}
                 {isUpdateGroup?<UpdateGroup id={selectGroupId} close={this.closeUpdateGroup} flushtable={this.handleGetGroupdata} />:null}
                 {isAddConsumer?<AddConsumer id={selectGroupId} close={this.closeAddConsumer} flushtable={this.handleGetTaskdata} />:null}
                 {isUpdateConsumer?<UpdateConsumer id={selectConsumerId} groupId={selectGroupId}  close={this.closeUpdateConsumer} flushtable={this.handleGetTaskdata} />:null}
                <Row  gutter={{ xs: 8, sm: 16, md: 24, lg: 32 }} style={{height:'96%',backgroundColor:'white'}}> 
                    <Col span={4} > 
                        <List style={{height:'100%'}}
                            header={<div>Topic列表</div>}
                            bordered  
                            dataSource={data}
                            renderItem={item => (
                                <List.Item>
                                    <Text type={item.status==1?'success':'error'}>[{item.status==1?'可用':'未同步Kafka'}]</Text><a style={{color:'blue'}} onClick={()=>this.selectTopic(item.topicName)}>{item.topicName}</a>
                                </List.Item>
                                 
                            )}
                        /> 
                    </Col>
                    <Col span={20}>
                      <Card title='消费者维护' style={{height:'100%'}}>
                          {
                              selectTopicName==''?null:
                                <Fragment>
                                     <Divider orientation="left">消费者组维护</Divider>
                                     <Text type="warning">消费者组一但被创建不可被删除！！！具体任务启动后，Kafka将会存在对应消费者组信息，修改只能更新描述以及错误通知人</Text>
                                    <Form
                                        layout="inline" >
                                        <Form.Item
                                            label="" >
                                            <Button onClick={this.handleAddGroup} type="primary" style={{marginBottom: 16}}>
                                                新增消费者组
                                            </Button>
                                        </Form.Item>
                                    </Form>
                                    < Table    
                                        rowKey="id"
                                        columns={this.columns}
                                        dataSource={hasGroupData ? groupData : null}
                                    />
                                    {
                                        selectGroupId==''?null:
                                        <Fragment>
                                             <Divider orientation="left">消费者维护</Divider>
                                             <Form
                                                layout="inline" >
                                                <Form.Item
                                                    label="" >
                                                    <Button onClick={this.handleAddConsumer} type="primary" style={{marginBottom: 16}}>
                                                        新增消费者
                                                    </Button>
                                                </Form.Item>
                                            </Form>
                                             < Table    
                                                rowKey="id"
                                                columns={this.consumerColumn}
                                                dataSource={hasConsumerData ? consumerData : null}
                                            />
                                        </Fragment>
                                       
                                    }
                               </Fragment>
                          }
                        
                      </Card> 
                    </Col>
                   
                </Row>
             </Fragment>
        )
    }
}
 

export default Consumer;