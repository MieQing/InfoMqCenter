import React, { Component } from 'react';
import { Modal, Form, message, Input, Spin, Divider, Switch } from 'antd';
import { AxiosAjax } from '../../axios/index';
import { AddExecution } from '../../axios/config'

const formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 15 },
};


class Add extends Component {
    constructor(props) {
        super(props);
        this.onChangeSwitch = this.onChangeSwitch.bind(this);
        this.state = {
            confirmLoading: false,
            loading: false
        };
    }
    
    
    formRef = React.createRef();

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
        const { checked } = this.state;  
        this.formRef.current.validateFields()
            .then(values => {
                console.log(values)
                let data = {
                    "code": values.code,
                    "address": values.address,
                    "name": values.name,
                    "status": checked ? 1 : 0
                }
                let param={ url:AddExecution,type:"post",data:data}
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
        const { checked } = this.state;
        return (
            <Modal
                okText={"确定"}
                cancelText={"取消"}
                width={700}
                title={"执行器创建"}
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
                                label="Code"
                                name="code"
                                rules={[{
                                    required: true, message: '请输入Code!', }]}
                            >
                                <Input />
                            </Form.Item>
                            < Divider />
                            <Form.Item
                                {...formItemLayout}
                                label="执行器地址"
                                name="address"
                                rules={[{
                                    required: true, message: '请输入执行器地址!',
                                }]}
                            >
                                < Input />
                            </Form.Item>
                            < Divider />
                            <Form.Item
                                {...formItemLayout}
                                label="名称"
                                name="name"
                                rules={[{
                                    required: true, message: '请输入名称!',
                                }]}
                            >
                                 < Input />
                            </Form.Item>
                            < Divider />
                            <Form.Item
                                {...formItemLayout}
                                label="状态"
                            >
                                <Switch
                                    onChange={this.onChangeSwitch}
                                    checked={checked}
                                        />
                            </Form.Item>
                        </Form>
                    </div>
                </Spin>
            </Modal>
        )
    }
}

export default Add;




