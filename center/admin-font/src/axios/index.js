/**
 *
 */
import {get, post,put,deleteAjax} from './tools';

/**
 * 公用post请求
 * @param type      调用类型 1.get 2.post
 * @param url       接口地址
 * @param data      接口参数
 * @param headers   接口所需header配置
 */
export const AxiosAjax = (axiosParam) => {
    let p = new Promise(function (resolve, reject,) {
        let headersParam = {"Content-Type": "application/json"}//headers对象
        axiosParam["headers"] = headersParam//AxiosParam对象
        if (axiosParam.type === "get") {
            get(axiosParam).then(res => {
                resolve(res)
            })
                .catch(err => {
                    reject(err.message)
                });
        }else if (axiosParam.type === "put") {
            put(axiosParam).then(res => {
                resolve(res)
            })
                .catch(err => {
                    reject(err.message)
                });
        } else if (axiosParam.type === "delete") {
            deleteAjax(axiosParam).then(res => {
                resolve(res)
            })
                .catch(err => {
                    reject(err.message)
                });
        }else {
            post(axiosParam).then(res => {
                resolve(res)
            })
                .catch(err => {
                    reject(err)
                });
        }
    })
    return p;
}


