import React, { Component } from 'react';
import { Modal, Form, message, Input, Spin, Divider, Switch } from 'antd';
import { AxiosAjax } from '../../axios/index';
import { GetExecutionById, UpdateExecution } from '../../axios/config'

const FormItem = Form.Item;

const formItemLayout = {
    labelCol: { span: 6 },
    wrapperCol: { span: 15 },
};


class Update extends Component {
    constructor(props) {
        super(props);
        this.onChangeSwitch = this.onChangeSwitch.bind(this);
        this.state = {
            confirmLoading: false,
            loading: false,
            id: this.props.id,
            checked: false,
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
            let param={type:'get',url: GetExecutionById + "?id=" + this.props.id} 
            AxiosAjax(param)
                .then(res => {
                    this.setState({
                        loading: false,
                        checked: res.data.status == 1 ? true : false
                    }, () => {
                        this.formRef.current.setFieldsValue({
                            code: res.data.code,
                            address: res.data.address,
                            name: res.data.name
                        });
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
        const { checked, id } = this.state;

        this.formRef.current.validateFields()
            .then(values => {
                console.log(values)
                let data = {
                    "code": values.code,
                    "address": values.address,
                    "name": values.name,
                    "status": checked ? 1 : 0,
                    id: id
                }
                let param={ url:UpdateExecution,type:"put",data:data}
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
                title={"执行器修改"}
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
                             <Form.Item {...formItemLayout}
                               label="Code"
                               name="code"
                               rules={[{
                                   required: true, message: '请输入Code!', }]}
                            >
                               <Input disabled={true}/>
                            </Form.Item>
                            <Divider />
                            <Form.Item  {...formItemLayout}
                                label="执行器地址"
                                name="address"
                                rules={[{
                                    required: true, message: '请输入执行器地址!',
                                }]}
                            >
                                 <Input disabled={true} />
                            </Form.Item>
                            < Divider />
                            <Form.Item {...formItemLayout}
                                label="名称"
                                name="name"
                                rules={[{
                                    required: true, message: '请输入名称!',
                                }]}
                            >
                                 <Input />
                            </Form.Item>
                            < Divider />
                            <Form.Item {...formItemLayout}
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

export default Update;




