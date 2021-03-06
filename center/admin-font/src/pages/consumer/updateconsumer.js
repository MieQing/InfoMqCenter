import React, { Component } from 'react';
import { Modal, Form, message, Input, Spin, Divider, Select,Typography,InputNumber,Switch } from 'antd';
import { AxiosAjax } from '../../axios/index';
import { GetConsumerTaskByIdUrl,UpdateConsumerTaskUrl,GetExecutionList } from '../../axios/config'

const { Text } = Typography;

const limitDecimals=v=>{
    return v.replace(/^(0+)|[^\d]+/g);
}

const formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 15 },
};

const { Option } = Select

class UpdateConsumer extends Component {
    constructor(props) {
        super(props);
        this.onChangeSwitch = this.onChangeSwitch.bind(this);
        this.handleGetSelect = this.handleGetSelect.bind(this);
        this.state = {
            confirmLoading: false,
            loading: false,
            checked:false,
            selectExecData:[]
        };
    }
    
    
    formRef = React.createRef();
 
    componentDidMount() {
        this.setState({
            loading: true
        }, () => {
            let param={type:'get',url: GetConsumerTaskByIdUrl + "?id=" + this.props.id} 
            AxiosAjax(param)
                .then(res => {
                    this.setState({
                        loading: false,
                        checked: res.data.status == 1 ? true : false
                    }, () => {
                        this.formRef.current.setFieldsValue({
                            taskCode: res.data.taskCode,
                            getType: res.data.getType,
                            operateType: res.data.operateType,
                            operateUrl: res.data.operateUrl,
                            batchNumber: res.data.batchNumber,
                            operateOutTime: res.data.operateOutTime,
                            operateOutTime: res.data.operateOutTime,
                            hea_int_ms: res.data.hea_int_ms,
                            ses_timeout_ms: res.data.ses_timeout_ms,
                            max_poll_ms: res.data.max_poll_ms,
                            execCode:res.data.execCode
                        });
                        this.handleGetSelect();
                    })
                })
                .catch(err => {
                    this.setState({
                        loading: false
                    })
                })
        })
    }

    onChangeSwitch = (checked) => {
        this.setState({
            checked: checked
        })
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

    handleSaveData = (param) => {
        this.setState({
            confirmLoading: true
        }, () => {
            AxiosAjax(param)
                .then(res => {
                    switch (res.data.code) {
                        case 200:
                            message.success("????????????", 1, () => {
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
                    "id": this.props.id,
                    "groupId": this.props.groupId,
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
                let param={ url:UpdateConsumerTaskUrl,type:"put",data:data}
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
        const { checked ,selectExecData} = this.state;
        return (
            <Modal
                okText={"??????"}
                cancelText={"??????"}
                width={1200}
                title={"??????????????????"}
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
                                label="????????????"
                                name="taskCode"
                            >
                                <Input disabled={true}  />
                            </Form.Item>
                            <Form.Item
                                {...formItemLayout}
                                label="????????????code"
                                name="execCode"
                                rules={[{
                                    required: true, message: '?????????????????????!', }]}
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
                                label="??????"
                            >
                                <Switch
                                    onChange={this.onChangeSwitch}
                                    checked={checked}
                                        />
                            </Form.Item>
                            <Form.Item
                                {...formItemLayout}
                                label="????????????"
                                name="getType"
                                rules={[{
                                    required: true, message: '?????????????????????!',
                                }]}
                            >
                                 <Select
                                    showArrow
                                    showSearch
                                    allowClear={true}
                                >
                                    <Option  value='2' > ?????? </Option>
                                    <Option  value='1' > ??????????????????????????????????????????????????? </Option>
                                </Select>
                            </Form.Item>
                            <Form.Item
                                {...formItemLayout}
                                label="????????????"
                                name="operateType"
                                rules={[{
                                    required: true, message: '?????????????????????!',
                                }]}
                            >
                                 <Select
                                    showArrow
                                    showSearch
                                    allowClear={true}
                                >
                                    <Option  value='restful' > RESTful?????? </Option>
                                    <Option  value='spring.cloud.eureka' > Spring.Cloud.Eureka</Option>
                                </Select>
                            </Form.Item>
                            <Form.Item
                                {...formItemLayout}
                                label="????????????"
                                name="operateUrl"
                                rules={[{
                                    required: true, message: '?????????????????????!',
                                }]}
                            >
                                <Input  />
                            </Form.Item>
                            <Form.Item  {...formItemLayout}
                                label="????????????"
                                name="batchNumber"
                                help="?????????????????????message???"
                                rules={[]}
                            >
                               <InputNumber min={1}   formatter={limitDecimals} parser={limitDecimals}   step={1} />
                            </Form.Item>
                            < Divider />
                            <Form.Item  {...formItemLayout}
                                label="????????????(ms)"
                                name="operateOutTime"
                                help="??????????????????"
                                rules={[]}
                            >
                               <InputNumber min={1000}   formatter={limitDecimals} parser={limitDecimals}   step={1} />
                            </Form.Item>
                            <Form.Item  {...formItemLayout}
                                label="heartbeat.interval.ms(ms)"
                                name="hea_int_ms"
                                help="????????????????????????broker??????????????????consumer????????????????????????????????????????????????????????????rebalance?????????????????????consumer??????????????????rebalance??????????????????????????????????????????"
                                rules={[]}
                            >
                               <InputNumber min={1000}   formatter={limitDecimals} parser={limitDecimals}   step={1} />
                            </Form.Item>
                            <Form.Item  {...formItemLayout}
                                label="session.timeout.ms(ms)"
                                name="ses_timeout_ms"
                                help="?????????broker????????????????????????consumer???????????????????????????????????????10???"
                                rules={[]}
                            >
                               <InputNumber min={1000}   formatter={limitDecimals} parser={limitDecimals}   step={1} />
                            </Form.Item>
                            <Form.Item  {...formItemLayout}
                                label="max.poll.interval.ms(ms)"
                                name="max_poll_ms"
                                help=" ????????????poll????????????????????????????????????broker??????????????????consumer??????????????????????????????????????????????????????rebalance???????????????????????????consumer??????"
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

export default UpdateConsumer;




