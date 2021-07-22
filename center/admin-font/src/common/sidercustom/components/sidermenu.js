import React from 'react';
import { Menu } from 'antd';
import { Link } from 'react-router-dom';

const renderMenuItem = item => ( // item.route 菜单单独跳转的路由
    <Menu.Item
        key={item.key==""?item.title:item.key}
    >
        <Link to={(item.route || item.key) + (item.query || '')}>  
            <span className="nav-text">{item.title}</span>
        </Link>
    </Menu.Item>
);

const renderSubMenu = item => ( 
    <Menu.SubMenu
        key={item.key==""?item.title:item.key}
        title={
            <span>
                <span className="nav-text">{item.title}</span>
            </span>
        }
    >
        {item.subs.map(item => renderMenuItem(item))}
    </Menu.SubMenu>
);

export default ({ menus, ...props }) => (
    <Menu {...props}>
        {menus && menus.map(item => 
            item.subs ? renderSubMenu(item) : renderMenuItem(item)
        )}
    </Menu>
);