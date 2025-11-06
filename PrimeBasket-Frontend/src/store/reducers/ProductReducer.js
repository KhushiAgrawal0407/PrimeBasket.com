const initialState={
    product:null,
    categories:null,
    pagination:{},
};

export const productReducer = (state = initialState, action)=>{
    switch (action.type){
        case "FETCH_PRODUCTS":
            return {
                ...state,
                product:action.payload,
                pagination:{
                    ...state.pagination,
                    pageNo: action.pageNo,
                    pageSize: action.pageSize,
                    totalElements: action.totalElements,
                    totalPages: action.totalPages,
                    lastPage: action.lastPage,
                },
            };

        case "FETCH_CATEGORIES":
            return {
                ...state,
                categories:action.payload,
                pagination:{
                    ...state.pagination,
                    pageNo: action.pageNo,
                    pageSize: action.pageSize,
                    totalElements: action.totalElements,
                    totalPages: action.totalPages,
                    lastPage: action.lastPage,
                },
            };
            
        default:
            return state;
    }
};