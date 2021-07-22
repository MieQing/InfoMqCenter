/**
 * http通用工具函数
 */
import axios from 'axios';


/**
 * 公用get请求
 * @param url       接口地址
 * @param headers   接口所需header配置
 */

export const get = (axiosParam) => {
    let p = new Promise(function (resolve, reject) {
        axios.get(axiosParam.url, {headers: axiosParam.headers})
            .then(res => {
                resolve(res)
            })
            .catch(err => {
                reject(err.message)
            });
    })
    return p;
}
/**
 * 公用post请求
 * @param url       接口地址
 * @param data      接口参数
 */
export const post = (axiosParam) => {
    let p = new Promise(function (resolve, reject) {
        axios.post(axiosParam.url, axiosParam.data, {headers: axiosParam.headers})
            .then(res => {
                resolve(res)
            })
            .catch(err => {
                reject(err.message)
            });
    })
    return p;
}

/**
 * 公用put请求
 * @param url       接口地址
 * @param data      接口参数
 */
 export const put = (axiosParam) => {
    let p = new Promise(function (resolve, reject) {
        axios.put(axiosParam.url, axiosParam.data, {headers: axiosParam.headers})
            .then(res => {
                resolve(res)
            })
            .catch(err => {
                reject(err.message)
            });
    })
    return p;
}


/**
 * 公用delete请求
 * @param url       接口地址
 * @param data      接口参数
 */
 export const deleteAjax = (axiosParam) => {
    let p = new Promise(function (resolve, reject) {
        axios.delete(axiosParam.url, axiosParam.data, {headers: axiosParam.headers})
            .then(res => {
                resolve(res)
            })
            .catch(err => {
                reject(err.message)
            });
    })
    return p;
}

