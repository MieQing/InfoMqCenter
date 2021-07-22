import React, { Component } from 'react';
import { Modal, Form, message, Input, Spin, Divider, Select,Typography } from 'antd';
import { AxiosAjax } from '../../axios/index';
import { AddGroupUrl } from '../../axios/config'

const { Text } = Typography;

const formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 15 },
};

const { Option } = Select

class AddGroup extends Component {
    constructor(props) {
        super(props);
        this.state = {
            confirmLoading: false,
            loading: false,
            topicName:this.props.topicName
        };
    }
    
    
    formRef = React.createRef();
 

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
                console.log(values)
                let data = {
                    "topicName": values.topicName,
                    "groupName": values.groupName,
                    "offsetReset": values.offsetReset,
                    "description": values.description,
                    "notifier": values.notifier,
                    "par_assign_strategy":values.par_assign_strategy
                }
                let param={ url:AddGroupUrl,type:"post",data:data}
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
        const { topicName } = this.state;
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
                                label="Topic"
                                name="topicName"
                                initialValue={topicName}
                            >
                                <Input disabled={true} />
                            </Form.Item>
                            < Divider />
                            <Form.Item
                                {...formItemLayout}
                                label="消费者组"
                                name="groupName"
                                rules={[{
                                    required: true, message: '请输入消费者组!',
                                }]}
                            >
                                < Input placeholder="需要英文:kafka.test.group1" />
                            </Form.Item>
                            < Divider />
                            <Form.Item
                                {...formItemLayout}
                                label="OffsetReset"
                                name="offsetReset"
                                rules={[{
                                    required: true, message: '请选择OffsetReset!',
                                }]}
                            >
                                 <Select
                                    showArrow
                                    showSearch
                                    allowClear={true}
                                >
                                    <Option  value='latest' > latest </Option>
                                    <Option  value='earliest' > earliest </Option>
                                    <Option  value='none' > none </Option>
                                </Select>
                            </Form.Item>
                            <Form.Item
                                {...formItemLayout}
                                label="选项提示"
                            >
                               <Text style={{fontSize:'9px'}}>earliest：当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，从头开始消费 </Text><br/>
                                <Text style={{fontSize:'9px'}}>latest：当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，消费新产生的该分区下的数据 </Text><br/>
                                <Text style={{fontSize:'9px'}}>none：topic各分区都存在已提交的offset时，从offset后开始消费；只要有一个分区不存在已提交的offset，则抛出异常 </Text>
                            </Form.Item>
                            < Divider />
                            <Form.Item
                                {...formItemLayout}
                                label="分区策略"
                                name="par_assign_strategy"
                                initialValue='range'
                                rules={[{
                                    required: true, message: '请选择分区策略!',
                                }]}
                            >
                                 <Select
                                    showArrow
                                    showSearch
                                    allowClear={true}
                                >
                                    <Option  value='range' > range </Option>
                                    <Option  value='roundrobin' > roundrobin</Option>
                                    <Option  value='sticky' > sticky</Option>
                                </Select>
                            </Form.Item>
                            < Divider />
                            <Form.Item
                                {...formItemLayout}
                                label="描述"
                                name="description"
                                rules={[{
                                    required: true, message: '请填写对应消费者组描述!',
                                }]}
                            >
                                < Input placeholder="请填写描述信息" />
                            </Form.Item>
                            <Form.Item
                                {...formItemLayout}
                                label="错误通知人"
                                name="notifier"
                                rules={[{
                                    required: true, message: '请填写错误通知人!',
                                }]}
                            >
                                < Input placeholder="请填写错误通知人邮箱，任务处理失败会邮件通知，多人';'隔开" />
                            </Form.Item>
                        </Form>
                    </div>
                </Spin>
            </Modal>
        )
    }
}

export default AddGroup;




