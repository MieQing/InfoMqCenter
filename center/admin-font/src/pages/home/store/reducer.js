import {fromJS} from 'immutable';
import * as constants from './constants';

const defaultState = fromJS({
    homeshowRouter: true,
    menuList: sessionStorage.getItem("MenuList"),
    breadList: sessionStorage.getItem("BreadList"),
});

const changeShowRouter = (state, action) => {
    return state.merge({
        homeshowRouter: action.showRouter
    });
};


const changeMenuLsit = (state, action) => {
    return state.merge({
        menuList: action.menuList
    });
};

const changeBreadLsit = (state, action) => {
    return state.merge({
        breadList: action.breadList
    });
};


export default (state = defaultState, action) => {
    switch (action.type) {
        case constants.CHANGE_SHOW_ROUTER:
            return changeShowRouter(state, action);
        case constants.CHANGE_MENU_LIST:
            return changeMenuLsit(state, action);
        case constants.CHANGE_BREAD_LIST:
            return changeBreadLsit(state, action);
        default:
            return state;
    }
}