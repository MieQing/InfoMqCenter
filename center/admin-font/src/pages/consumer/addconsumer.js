import React, { Component } from 'react';
import { Modal, Form, message, Input, Spin, Divider, Select,Typography,InputNumber,Switch } from 'antd';
import { AxiosAjax } from '../../axios/index';
import { AddConsumerTaskUrl,GetExecutionList } from '../../axios/config'

const { Text } = Typography;

const limitDecimals=v=>{
    return v.replace(/^(0+)|[^\d]+/g);
}

const formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 15 },
};

const { Option } = Select

class AddConsumer extends Component {
    constructor(props) {
        super(props);
        this.onChangeSwitch = this.onChangeSwitch.bind(this);
        this.state = {
            confirmLoading: false,
            loading: false,
            groupId:this.props.id,
            checked:false,
            selectExecData: []
        };
    }
    
    
    formRef = React.createRef();
 


    componentDidMount() {
        this.handleGetSelect();
    }


    handleGetSelect = () => {
        let param={ url:GetExecutionList,type:"get"}
        AxiosAjax(param)
            .then(res => {
                let data2 = [];
                res.data.map(x => {
                    data2.push({ "text": x.code + '|' + x.address, "value": x.code })
                })
                this.setState({
                    selectExecData: data2
                })
            })
    }

    onChangeSwitch = (checked) => {
        this.setState({
            checked: checked
        })
    }

    handleSaveData = (param) => {
        this.setState({
            confirmLoading: true
        }, () => {
            AxiosAjax(param)
                .then(res => {
                    switch (res.data.code) {
                        case 200:
                            message.success("新增成功", 1, () => {
                                this.props.flushtable();
                                this.handleCancel();
                            });
                            break;
                        default:
                            message.warn(res.data.message);
                            this.setState({
                                confirmLoading: false
                            })
                            break;
                    }

                })
                .catch(err => {
                    message.warn(err);
                    this.setState({
                        confirmLoading: false
                    })
                });
        })
    }


    handleOk = (e) => {
        e.preventDefault(); 
        this.formRef.current.validateFields()
            .then(values => {
                let data = {
                    "groupId": this.state.groupId,
                    "taskCode": values.taskCode,
                    "status": this.state.checked ? 1 : 0,
                    "getType": values.getType,
                    "operateType": values.operateType,
                    "operateUrl": values.operateUrl,
                    "batchNumber": values.batchNumber,
                    "operateOutTime": values.operateOutTime,
                    "hea_int_ms": values.hea_int_ms,
                    "ses_timeout_ms": values.ses_timeout_ms,
                    "max_poll_ms": values.max_poll_ms,
                    "execCode":values.execCode
                }
                let param={ url:AddConsumerTaskUrl,type:"post",data:data}
                this.handleSaveData(param);
            })
            .catch(errorInfo => {
            });
 
    };


    handleCancel = () => {
        this.setState({
            confirmLoading: false
        }, () => {
            this.props.close();
        })
    };


    render() {
        const { checked,selectExecData } = this.state;
        return (
            <Modal
                okText={"确定"}
                cancelText={"取消"}
                width={1200}
                title={"消费者组创建"}
                visible={true}
                onOk={this.handleOk}
                confirmLoading={this.state.confirmLoading}
                onCancel={this.handleCancel}
            >
                <Spin
                    spinning={this.state.loading} >
                    <div>
                        <Form
                            ref={this.formRef} >
                            <Form.Item
                                {...formItemLayout}
                                label="任务编码"
                                name="taskCode"
                                rules={[{
                                    required: true, message: '请输入任务编码!', }]}
                            >
                                <Input  />
                            </Form.Item>
                            <Form.Item
                                {...formItemLayout}
                                label="执行节点code"
                                name="execCode"
                                rules={[{
                                    required: true, message: '请选择执行节点!', }]}
                            >
                                 <Select
                                    showArrow
                                    showSearch
                                    allowClear={true}
                                >
                                    {
                                        selectExecData.map(d => (
                                            <Option value={d.value} key={d.value} > {d.text} </Option>
                                        ))
                                    }
                                </Select>
                            </Form.Item>
                            <Form.Item
                                {...formItemLayout}
                                label="状态"
                            >
                                <Switch
                                    onChange={this.onChangeSwitch}
                                    checked={checked}
                                        />
                            </Form.Item>
                            <Form.Item
                                {...formItemLayout}
                                label="获取方式"
                                name="getType"
                                rules={[{
                                    required: true, message: '请选择获取方式!',
                                }]}
                            >
                                 <Select
                                    showArrow
                                    showSearch
                                    allowClear={true}
                                >
                                    <Option  value='2' > 批量 </Option>
                                    <Option  value='1' > 单条（兼容老版本系统，建议勿使用） </Option>
                                </Select>
                            </Form.Item>
                            <Form.Item
                                {...formItemLayout}
                                label="调用方式"
                                name="operateType"
                                rules={[{
                                    required: true, message: '请选择调用方式!',
                                }]}
                            >
                                 <Select
                                    showArrow
                                    showSearch
                                    allowClear={true}
                                >
                                    <Option  value='restful' > RESTful接口 </Option>
                                    <Option  value='spring.cloud.eureka' > Spring.Cloud.Eureka</Option>
                                </Select>
                            </Form.Item>
                            <Form.Item
                                {...formItemLayout}
                                label="调用地址"
                                name="operateUrl"
                                rules={[{
                                    required: true, message: '请填写调用地址!',
                                }]}
                            >
                                <Input  />
                            </Form.Item>
                            <Form.Item  {...formItemLayout}
                                label="批消费数"
                                initialValue={1}
                                name="batchNumber"
                                help="每次消费拉取的message数"
                                rules={[]}
                            >
                               <InputNumber min={1}   formatter={limitDecimals} parser={limitDecimals}   step={1} />
                            </Form.Item>
                            < Divider />
                            <Form.Item  {...formItemLayout}
                                label="超时时间(ms)"
                                initialValue={30000}
                                name="operateOutTime"
                                help="调用超时时间"
                                rules={[]}
                            >
                               <InputNumber min={1000}   formatter={limitDecimals} parser={limitDecimals}   step={1} />
                            </Form.Item>
                            <Form.Item  {...formItemLayout}
                                label="heartbeat.interval.ms(ms)"
                                initialValue={1000}
                                name="hea_int_ms"
                                help="心跳时间，服务端broker通过心跳确认consumer是否故障，如果发现故障，就会通过心跳下发rebalance的指令给其他的consumer通知他们进行rebalance操作，这个时间可以稍微短一点"
                                rules={[]}
                            >
                               <InputNumber min={1000}   formatter={limitDecimals} parser={limitDecimals}   step={1} />
                            </Form.Item>
                            <Form.Item  {...formItemLayout}
                                label="session.timeout.ms(ms)"
                                initialValue={10000}
                                name="ses_timeout_ms"
                                help="服务端broker多久感知不到一个consumer心跳就认为他故障了，默认是10秒"
                                rules={[]}
                            >
                               <InputNumber min={1000}   formatter={limitDecimals} parser={limitDecimals}   step={1} />
                            </Form.Item>
                            <Form.Item  {...formItemLayout}
                                label="max.poll.interval.ms(ms)"
                                initialValue={30000}
                                name="max_poll_ms"
                                help=" 如果两次poll操作间隔超过了这个时间，broker就会认为这个consumer处理能力太弱，会将其踢出消费组，触发rebalance，将分区分配给别的consumer消费"
                                rules={[]}
                            >
                               <InputNumber min={1000}   formatter={limitDecimals} parser={limitDecimals}   step={1} />
                            </Form.Item>
                        </Form>
                    </div>
                </Spin>
            </Modal>
        )
    }
}

export default AddConsumer;




