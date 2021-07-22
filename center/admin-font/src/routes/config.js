export default {
    menus: [ // 菜单相关路由
        { key: '/execution', title: '执行器管理', component: 'Execution' },
        { key: '/topic', title: 'Topic管理', component: 'Topic' },
        { key: '/consumer', title: '消费者管理', component: 'Consumer' },
        { key: '/error', title: '错误消息', component: 'ErrorList' },
    ],
    others: [
        // { key: '/changePassword', title: '密码修改', component: 'ChangePassword' }
    ] // 非菜单相关路由
}