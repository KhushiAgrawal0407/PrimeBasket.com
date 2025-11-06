import api from "../../api/api";

export const fetchProducts = (queryString) => async(dispatch) => {
    try {
        dispatch({type:"IS_FETCHING"});
        const { data } = await api.get(`/public/product?${queryString}`);
        dispatch({
            type:"FETCH_PRODUCTS",
            payload: data.content,
            pageNo: data.pageNo,
            pageSize: data.pageSize,
            totalElements: data.totalElements,
            totalPages: data.totalPages,
            lastPage: data.lastPage,
        });
        dispatch({type:"IS_SUCCESS"});
    } catch (error) {
        dispatch({
            type:"IS_ERROR",
            payload:error?.response?.data?.message || "Failed to fetch products",
        })
        console.log(error);
    }
}

// eslint-disable-next-line no-unused-vars
export const fetchCategories = (queryString) => async(dispatch) => {
    try {
        dispatch({type:"CATEGORY_LOADER"});
        const { data } = await api.get(`/public/categories`);
        dispatch({
            type:"FETCH_CATEGORIES",
            payload: data.content,
            pageNo: data.pageNo,
            pageSize: data.pageSize,
            totalElements: data.totalElements,
            totalPages: data.totalPages,
            lastPage: data.lastPage,
        });
        dispatch({ type: "IS_SUCCESS" });
    } catch (error) {
        dispatch({
            type:"IS_ERROR",
            payload:error?.response?.data?.message || "Failed to fetch categories",
        })
        console.log(error);
    }
}

export const addToCart = (data, qty=1, toast) =>
    (dispatch, getState) => {

        // find the product
        const { product } = getState().products;
        const getProduct = product.find(
            (item) => item.productId === data.productId
        );

        //check for stocks
        const isQuantityExist = getProduct.quantity >= qty;

        //if in stock -> add
        if(isQuantityExist){
            dispatch({
                type:"ADD_CART",
                payload : {
                    ...data,
                    quantity:qty,
                },
            });
            toast.success(`${data?.productName} added to the cart`)
            const updatedCart = getState().carts.cart;
            dispatch(createOrUpdateUserCart(updatedCart));  
        }
        else{
            //error
            toast.error("Out of stock")
        }
    }

    export const increaseCartQuantity = (data, toast, currentQuantity, setCurrentQuantity) => 
         async (dispatch, getState) => {
            // find the product
            const { product } = getState().products;
            const getProduct = product.find(
                (item) => item.productId === data.productId
            );

            const isQuantityExist = getProduct.quantity >= currentQuantity+1;

            if(isQuantityExist){
                const newQuantity = currentQuantity+1;
                setCurrentQuantity(newQuantity);

                dispatch({
                    type: "ADD_CART",
                    payload: {...data, quantity:newQuantity}
                });
                const updatedCart = getState().carts.cart;
                await dispatch(createOrUpdateUserCart(updatedCart));
            }else{
                toast.error("Quantity reached to limit.")
            }
        }

    export const decreaseCartQuantity = 
    (data, newQuantity) => (dispatch, getState) => {
        dispatch({
            type: "ADD_CART",
            payload: {...data, quantity: newQuantity},
        });
        const updatedCart = getState().carts.cart;
        dispatch(createOrUpdateUserCart(updatedCart));
    }

    export const removeFromCart = 
    (data, toast) => (dispatch, getState) => {
        dispatch({
            type: "REMOVE_CART",
            payload: data,
        });
        toast.success(`${data.productName} removed from the cart`);
        const updatedCart = getState().carts.cart;
        dispatch(createOrUpdateUserCart(updatedCart));
    }

    export const authenticateSignInUser = (sendData, toast, reset, navigate, setLoader) => async(dispatch) => {
        try{
            setLoader(true);
            const {data} = await api.post("/auth/signin", sendData); 
            dispatch({type: "LOGIN_USER", payload: data});
            dispatch(getUserCart());
            dispatch(getUserAddresses());
            localStorage.setItem("auth", JSON.stringify(data));
            reset();
            dispatch({ type: "IS_SUCCESS" });
            toast.success("Login successful!");
            navigate("/")
        }catch(error){
            console.log(error);
            toast.error(error?.response?.data?.message || "Internal Server Error");
        }finally{
            setLoader(false);
        }
    }

    
    export const registerNewUSer = (sendData, toast, reset, navigate, setLoader) => async(dispatch) => {
        try{
            setLoader(true);
            const {data} = await api.post("/auth/signup", sendData); 
            reset();
            toast.success(data?.message||"User registered successfully!");
            dispatch({ type: "IS_SUCCESS" });
            navigate("/login")
        }catch(error){
            console.log(error);
            toast.error(error?.response?.data?.message || error?.response?.data?.password || "Internal Server Error");
        }finally{
            setLoader(false);
        }
    };

    export const logoutUser = (navigate) => (dispatch) => {
        dispatch({type:"LOG_OUT"});
        dispatch({type: "CLEAR_CART"});
        localStorage.removeItem("auth");
        navigate("/login");
    };

    export const addUpdateUserAddress = (sendData, toast, addressID, setOpenAddressModal) => async (dispatch, getState) => {
        dispatch({type:"BUTTON_LOADER"});
        try{
            if(!addressID){
                const {data} = await api.post("/addresses", sendData);
            }
            else{
                await api.put(`/addresses/${addressID}`, sendData);
            }
            dispatch(getUserAddresses());
            toast.success("Address saved successfully!");
            dispatch({type:"IS_SUCCESS"})
        }catch(error){
            console.log(error);
            toast.error(error?.respons?.data?.message || "Internal Server Error");
            dispatch({type:"IS_ERROR", payload:null});
        }finally{
            setOpenAddressModal(false);
        }
    };

    export const getUserAddresses = () => async(dispatch, getState) => {
        try {
            dispatch({type:"IS_FETCHING"});
            const { data } = await api.get(`/users/addresses`);
            dispatch({type:"USER_ADDRESS", payload:data});
            dispatch({type:"IS_SUCCESS"});
        } catch (error) {
            console.log(error);
            dispatch({
                type:"IS_ERROR",
                payload:error?.response?.data?.message || "Failed to fetch user's addresses",
            });
        }
    }

    export const selectUserCheckoutAddress = (address) => {
        return {
            type: "SELECT_CHECKOUT_ADDRESS",
            payload: address,
        }
    }

    export const deleteUserAddress = (toast, addressID, setOpenDeleteModal) => async (dispatch, getState) => {
        try {
            dispatch({type:"BUTTON_LOADER"});
            await api.delete(`/addresses/${addressID}`);
            dispatch({type:"IS_SUCCESS"});
            dispatch(getUserAddresses());
            dispatch(clearCheckoutAddress());
            toast.success("Address deleted successfully!");
        } catch (error) {
            console.log(error);
            dispatch({
                type:"IS_ERROR",
                payload:error?.response?.data?.message || "Some Error Occured",
            });
        } finally{
            setOpenDeleteModal(false);
        }
    };

    export const clearCheckoutAddress = () => {
        return {
            type:"REMOVE_CHECKOUT_ADDRESS",
        };
    };

    export const addPaymentMethod = (method) => {
        return {
            type: "ADD_PAYMENT_METHOD",
            payload: method,
        };
    };

    export const createOrUpdateUserCart = (sendCartItems) => async (dispatch, getState) => {
        try {
            dispatch({ type: "IS_FETCHING" });
            const {data} = await api.post('/cart/create', sendCartItems);
            dispatch({
                type: "GET_USER_CART_PRODUCTS",
                payload: data.productDTO,
                totalPrice: data.totalPrice,
                cartId: data.cartId
            });
            dispatch({ type: "IS_SUCCESS" });
        } catch (error) {
            console.log(error);
            dispatch({ 
                type: "IS_ERROR",
                payload: error?.response?.data?.message || "Failed to create cart items",
            });
        }
    };

    export const getUserCart = () => async (dispatch, getState) => {
        try {
            dispatch({ type: "IS_FETCHING" });
            const { data } = await api.get('/carts/users/cart');
            
            dispatch({
                type: "GET_USER_CART_PRODUCTS",
                payload: data.productDTO,
                totalPrice: data.totalPrice,
                cartId: data.cartId
            })
            dispatch({ type: "IS_SUCCESS" });
        } catch (error) {
            console.log(error);
            dispatch({ 
                type: "IS_ERROR",
                payload: error?.response?.data?.message || "Failed to fetch cart items",
            });
        }
    };

    export const createStripePaymentSecret = (totalPrice) => async(dispatch, getState) => {
        try{
            dispatch({type: "IS_FETCHING"});
            const {data} = await api.post("/order/stripe-client-secret", {
                "amount":Number(totalPrice)*100,
                "currency":"usd"
            });
            dispatch({
                type:"CLIENT_SECRET",
                payload: data,
            })
            dispatch({type: "IS_SUCCESS"});
        }catch(error){
            console.log(error);
        }
    };

    export const stripePaymentConfirmation = (sendData, setErrorMessage, setLoading, toast) => async(dispatch, getState) => {
        try{
            dispatch({type: "IS_FETCHING"});
            const response = await api.post("/orders/users/payment/online", sendData);
            if(response.data){
                dispatch({ type:"REMOVE_CLIENT_SECRET_ADDRESS" });
                dispatch({type:"CLEAR_CART"});
            
                toast.success("Order Accepted!");
            }
            else{
                setErrorMessage("Payment Failed. Please try again")
            }
            
            
        }catch(error){
            console.log(error);
            setErrorMessage("Payment Failed. Please try again")
        }
    };

    export const analyticsAction = () => async (dispatch,getState) => {
        try{
            dispatch({type: "IS_FETCHING"});
            const { data } = await api.get(`/admin/app/analytics`);
            dispatch({
                type: "FETCH_ANALYTICS",
                payload: data,
            });
            dispatch({type: "IS_SUCCESS"});
        } catch(error){
            dispatch({
                type: "IS_ERROR",
                payload: error?.response?.data?.message || "Failed to fetch analytics data",
            });
        }
    }