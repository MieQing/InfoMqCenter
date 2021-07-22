import React, { Component } from 'react';
import { Modal, Form, message, Input, Spin} from 'antd';
import { AxiosAjax } from '../../axios/index';
import { GetConsumerGroupById, UpdateGroupUrl } from '../../axios/config'

const FormItem = Form.Item;

const formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 15 },
};


class UpdateGroup extends Component {
    constructor(props) {
        super(props);
        this.onChangeSwitch = this.onChangeSwitch.bind(this);
        this.state = {
            confirmLoading: false,
            loading: false,
            id: this.props.id,
        };
    }

    formRef = React.createRef();

    onChangeSwitch = (checked) => {
        this.setState({
            checked: checked
        })
    }

    componentDidMount() {
        this.setState({
            loading: true
        }, () => {
            let param={type:'get',url: GetConsumerGroupById + "?id=" + this.props.id} 
            AxiosAjax(param)
                .then(res => {
                    this.formRef.current.setFieldsValue({
                        description: res.data.description,
                        notifier: res.data.notifier,
                    });
                    this.setState({
                        loading: false
                    })
                })
                .catch(err => {
                    this.setState({
                        loading: false
                    })
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
                            message.success("修改成功", 1, () => {
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
        const { id } = this.state;

        this.formRef.current.validateFields()
            .then(values => {
                let data = {
                    "description": values.description,
                    "notifier": values.notifier,
                    id: id
                }
                let param={ url:UpdateGroupUrl,type:"put",data:data}
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
                width={700}
                title={"消费者组修改"}
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

export default UpdateGroup;




