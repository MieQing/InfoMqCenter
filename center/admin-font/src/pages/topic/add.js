import React, { Component } from 'react';
import { Modal, Form, message, Input, Spin,InputNumber } from 'antd';
import { AxiosAjax } from '../../axios/index';
import { AddTopicUrl } from '../../axios/config'
 
const formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 15 },
};

const limitDecimals=v=>{
    return v.replace(/^(0+)|[^\d]+/g);
}


class AddTopic extends Component {
    constructor(props) {
        super(props);
        this.state = {
            confirmLoading: false,
            loading: false
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
            let data = {
                "topicName": values.topicName,
                "description": values.description,
                "partition": values.partition,
                "replicationFactor": values.replicationFactor,
            }
            let param={ url:AddTopicUrl,type:"post",data:data}
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
        return (
            <Modal
                okText={"确定"}
                cancelText={"取消"}
                width={800}
                title={"Topic创建"}
                visible={true}
                onOk={this.handleOk}
                confirmLoading={this.state.confirmLoading}
                onCancel={this.handleCancel}
            >
                <Spin
                    spinning={this.state.loading} >
                    <div>
                        <Form
                            ref={this.formRef}  >
                            <Form.Item  {...formItemLayout}
                                label="Topic"
                                name="topicName"
                                help="对应Kafka中指定的topic,如kafka.test"
                                rules={[{
                                    required: true, message: '请输入Topic!', }]}
                            >
                                <Input />
                            </Form.Item>
                            <Form.Item  {...formItemLayout}
                                label="描述"
                                name="description"
                                help="对应主题的实际业务应用描述"
                                rules={[{
                                    required: true, message: '请输入描述!', }]}
                            >
                                <Input />
                            </Form.Item>
                            <Form.Item  {...formItemLayout}
                                label="分区数"
                                initialValue={1}
                                name="partition"
                                help="创建主题时的分区数"
                                rules={[]}
                            >
                               <InputNumber min={1}   formatter={limitDecimals} parser={limitDecimals}   step={1} />
                            </Form.Item>
                            <Form.Item  {...formItemLayout}
                                label="副本数"
                                initialValue={1}
                                name="replicationFactor"
                                help="创建主题时的副本数，如果你的kafka只有1台机器，此处只能填1，如果多机环境建议>=2"
                                rules={[]}
                            >
                                <InputNumber min={1} formatter={limitDecimals} parser={limitDecimals}     step={1} />
                            </Form.Item>
                        </Form>
                    </div>
                </Spin>
            </Modal>
        )
    }
}
 
export default AddTopic;




