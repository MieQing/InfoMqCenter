
│  allcomponents.js             路由打开页面组件注入       
│  App.js                       入口JS
│  App.test.js
│  index.css
│  index.js
│  logo.svg 
│  serviceWorker.js
│  
├─axios
│      config.js              接口地址配置文件
│      config_dev.js
│      config_local.js
│      index.js                封装的axios 组件
│      tools.js
│      
├─common
│  │  config.js
│  │  
│  ├─breadcrumbCustom        面包屑
│  │      index.js
│  │      
│  ├─commonfunction         常用方法
│  │      index.js
│  │      
│  ├─headerCustom          首页head头
│  │      index.js
│  │      
│  ├─privateRoute
│  │      index.js
│  │      
│  ├─sidercustom         菜单列表组件
│  │  │  index.js
│  │  │  
│  │  └─components
│  │          sidermenu.js
│  │          
│  └─upload
│          BaseToFile.js    文件转换
│          FileUpload.js    上传组件
│          PictureWall.js   图片组件
│          
├─iconfont
│      demo.css
│      demo_index.html
│      iconfont.css
│      iconfont.eot
│      iconfont.js
│      iconfont.json
│      iconfont.svg
│      iconfont.ttf
│      iconfont.woff
│      iconfont.woff2
│      
├─pages
│  ├─base               系统配置列表页
│  │      index.js        
│  │      
│  ├─commonprocess      快捷入口
│  │  │  index.js
│  │  │  
│  │  └─components     
│  │          add.js
│  │          update.js
│  │          
│  ├─document          制度文档
│  │      adddocument.js
│  │      fileadd.js
│  │      index.js
│  │      updatedocuments.js
│  │       
│  ├─home             首页
│  │  │  index.js
│  │  │  
│  │  └─store
│  │          actionCreators.js
│  │          constants.js
│  │          index.js
│  │          reducer.js
│  │          
│  ├─iconimage       icon logo配置页
│  │      add.js
│  │      index.js
│  │      
│  ├─log
│  │      index.js
│  │      
│  ├─menu           系统配置菜单页
│  │  │  index.js
│  │  │  
│  │  ├─components
│  │  │      menuoperator.js
│  │  │      rightclickmenu.js
│  │  │      
│  │  └─store
│  │          actionCreators.js
│  │          constants.js
│  │          index.js
│  │          reducer.js
│  │          
│  ├─model
│  │      index.js
│  │      
│  ├─newsoperate   新闻配置页
│  │      index.js
│  │      newseditor.js
│  │      
│  ├─pagesconfig   动态图标页面配置
│  │  │  index.js
│  │  │  
│  │  └─components
│  │          configoperator.js
│  │          rightclickmenu.js
│  │          
│  ├─role          角色配置
│  │  │  index.js
│  │  │  
│  │  ├─components
│  │  │      rolecontextchange.js
│  │  │      roleempmap.js
│  │  │      rolemenumap.js
│  │  │      
│  │  └─store
│  │          actionCreators.js
│  │          constants.js
│  │          index.js
│  │          reducer.js
│  │          
│  ├─sso
│  │  └─store
│  │          actionCreators.js
│  │          constants.js
│  │          index.js
│  │          reducer.js
│  │          
│  └─test
│          index.js
│          
├─routes
│      config.js
│      index.js
│      
├─store      store组合
│      index.js
│      reducer.js
│      
└─style
    │  banner.less
    │  button.less
    │  card.less
    │  contextmenu.less
    │  excelin.less
    │  global.less
    │  icons.less
    │  img.less
    │  index.less
    │  login.less
    │  menu.less
    │  modal.less
    │  scroll.less
    │  table.less
    │  utils-border.less
    │  utils-color.less
    │  utils-size.less
    │  utils-spacing.less
    │  utils-text.less
    │  variables.less
    │  
    ├─antd
    │      header.less
    │      index.less
    │      layout.less
    │      menu.less
    │      utils.less
    │      variables.less
    │      
    ├─font
    │      y6oxFxU60dYw9khW6q8jGw.woff2
    │      
    ├─imgs
    │      404.png
    │      installer.png
    │      logo.png
    │      
    ├─lib
    │      animate.css
    │      
    └─theme
            index.js
            theme-danger.json
            theme-grey.json
            theme-info.json
            theme-warn.json
            




安装环境要求：node.js 最好在安装个yarn
拉取完代码之后 输入 yarn install 或者npm install 获取组件
之后执行 yarn start启动
