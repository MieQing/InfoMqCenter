/*
 * 接口地址配置文件
 */ 
const ServerUrl = 'http://localhost:6900';



export const GetExecutionList=ServerUrl+"/execution/getlist"
export const AddExecution=ServerUrl+"/execution/add"
export const DeleteExecution=ServerUrl+"/execution/delete"
export const GetExecutionById=ServerUrl+"/execution/getone"
export const UpdateExecution=ServerUrl+"/execution/update"
export const GetWorkThread=ServerUrl+"/execution/getworkthread"

export const GetTopicListUrl=ServerUrl+"/topic/getlist"
export const GetTopicInfoUrl=ServerUrl+"/topic/getinfo"
export const AddTopicUrl=ServerUrl+"/topic/add"
export const DeleteTopicUrl=ServerUrl+"/topic/delete"



export const AddGroupUrl=ServerUrl+"/consumer/addgroup"
export const UpdateGroupUrl=ServerUrl+"/consumer/updategroup"
export const GetGroupListUrl=ServerUrl+"/consumer/getgroup"
export const GetConsumerGroupById=ServerUrl+"/consumer/getgroupone"
export const AddConsumerTaskUrl=ServerUrl+"/consumer/addconsumer"
export const GetConsumerTaskListUrl=ServerUrl+"/consumer/getconsumer"
export const DeleteConsumerTaskUrl=ServerUrl+"/consumer/deleteconsumer"
export const GetConsumerTaskByIdUrl=ServerUrl+"/consumer/getconsumerbyid"
export const UpdateConsumerTaskUrl=ServerUrl+"/consumer/updateconsumer"

export const GetErrorList=ServerUrl+"/error/getlist"
export const IgnoreErrorUrl=ServerUrl+"/error/ignore"
export const ErrorReSendUrl=ServerUrl+"/error/resend"


 







