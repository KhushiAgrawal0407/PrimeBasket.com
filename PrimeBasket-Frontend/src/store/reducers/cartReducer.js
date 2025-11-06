// const initialState = {
//     cart : [],
//     totalPrice : 0,
//     cartId : null,
// };

// export const cartReducer = (state = initialState, action) => {
//     switch(action.type){
//         case "ADD_CART":{
//             const productToAdd = action.payload;
//             const existingProduct = state.cart.find(
//                 (item) => item.productId === productToAdd.productId
//             );
//             if(existingProduct){
//                 const updatedCart = state.cart.map((item) => {
//                     if(productToAdd.productId === item.productId){
//                         return productToAdd;
//                     }
//                     else{
//                         return item;
//                     }
//                 });

//                 //localStorage.setItem("cartItems", JSON.stringify(updatedCart));

//                 return{
//                     ...state,
//                     cart: updatedCart,
//                 };
//             }
//             else{
//                 const newCart = [...state.cart, productToAdd];

//                 //localStorage.setItem("cartItems", JSON.stringify(newCart));

//                 return{
//                     ...state,
//                     cart: newCart,
//                 };
//             }
            
//         }

//         case "REMOVE_CART":
//             return{
//                 ...state,
//                 cart: state.cart.filter(
//                     (item) => item.productId !== action.payload.productId
//                 ),
//             };

//         case "GET_USER_CART_PRODUCTS":
//             return {
//                 ...state,
//                 cart: action.payload,
//                 totalPrice: action.totalPrice,
//                 cartId: action.cartId,
//             };

//          case "CLEAR_CART":
//             return { cart:[], totalPrice: 0, cartId: null};

//         default:
//             return state;
//     }
// };

const initialState = {
    cart: [],
    totalPrice: 0,
    cartId: null,
};

export const cartReducer = (state = initialState, action) => {
    switch (action.type) {
        case "ADD_CART": {
            const productToAdd = action.payload;

            const existingProduct = state.cart.find(
                (item) => item.productId === productToAdd.productId
            );

            let updatedCart;
            if (existingProduct) {
                // update the product's quantity/details
                updatedCart = state.cart.map((item) =>
                    item.productId === productToAdd.productId
                        ? { ...item, ...productToAdd }
                        : item
                );
            } else {
                // add new product
                updatedCart = [...state.cart, productToAdd];
            }

            // recalc total price
            const newTotalPrice = updatedCart.reduce(
                (sum, item) => sum + item.price * item.quantity,
                0
            );

            return {
                ...state,
                cart: updatedCart,
                totalPrice: newTotalPrice,
            };
        }

        case "REMOVE_CART": {
            const updatedCart = state.cart.filter(
                (item) => item.productId !== action.payload.productId
            );

            const newTotalPrice = updatedCart.reduce(
                (sum, item) => sum + item.price * item.quantity,
                0
            );

            return {
                ...state,
                cart: updatedCart,
                totalPrice: newTotalPrice,
            };
        }

        case "GET_USER_CART_PRODUCTS":
            console.log("Reducer received this action:", action);
            return {
                ...state,
                cart: action.payload || [],
                totalPrice: action.totalPrice || 0,
                cartId: action.cartId || null,
            };

        case "CLEAR_CART":
            return { cart: [], totalPrice: 0, cartId: null };

        default:
            return state;
    }
};
