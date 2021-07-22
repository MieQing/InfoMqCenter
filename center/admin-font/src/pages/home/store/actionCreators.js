import * as constants from './constants';


const changeShowRouter = (showRouter) => ({
    type: constants.CHANGE_SHOW_ROUTER,
    homeshowRouter: showRouter,
});

export const showRouter = (showRouter) => {
    return (dispatch) => {
        dispatch(changeShowRouter(showRouter));
    }
}


const changeMenuLsit = (listJson) => ({
    type: constants.CHANGE_MENU_LIST,
    menuList: listJson,
});


export const menuLsit = (listJson) => {
    return (dispatch) => {
        dispatch(changeMenuLsit(listJson));
    }
}

const changeBreadLsit = (breadlist) => ({
    type: constants.CHANGE_BREAD_LIST,
    breadList: breadlist,
});


export const breadLsit = (breadlist) => {
    return (dispatch) => {
        dispatch(changeBreadLsit(breadlist));
    }
}

