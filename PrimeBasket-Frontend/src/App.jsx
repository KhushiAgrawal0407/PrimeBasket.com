import './App.css'
import Home from './components/home/home'
import Products from './components/product/Products'
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import Navbar from './components/shared/Navbar'
import About from './components/About'
import Contact from './components/Contact'
import { Toaster } from 'react-hot-toast'
import React, { useEffect } from 'react'
import Cart from './components/cart/Cart'
import Login from './components/auth/Login'
import PrivateRoute from './components/PrivateRoute'
import Register from './components/auth/Register'
import Checkout from './components/checkout/Checkout'
import PaymentConfirmation from './components/checkout/PaymentConfirmation'
import { useDispatch } from 'react-redux'
import { getUserCart } from './store/actions'
import AdminLayout from './components/admin/AdminLayout'
import Dashboard from './components/admin/dashboard/Dashboard'
import Category from './components/admin/categories/Category'
import Sellers from './components/admin/sellers/Sellers'
import AdminProducts from './components/admin/products/AdminProducts'
import Orders from './components/admin/orders/Orders'

function App() {
  const dispatch = useDispatch();

    useEffect(() => {
        dispatch(getUserCart());
    }, [dispatch]);


  return (
    <React.Fragment>
      <Router>
        <Navbar/>
        <Routes>
          <Route path='/' element = {<Home/>} />
          <Route path='/products' element = {<Products/>} />
          <Route path = '/about' element={<About/>} />
          <Route path = '/contact' element={<Contact/>} />
          <Route path = '/cart' element={<Cart/>} />
          <Route path='/' element={<PrivateRoute />}>
            <Route path = '/checkout' element={<Checkout/>} />
            <Route path = '/order-confirm' element={<PaymentConfirmation/>} />
          </Route>
          <Route path='/' element={<PrivateRoute publicPage/>}>
            <Route path = '/login' element={<Login/>} />
            <Route path = '/register' element={<Register/>} />
          </Route>
          <Route path='/' element={<PrivateRoute adminOnly/>}>
            <Route path = '/admin' element={<AdminLayout/>}>
              <Route path='' element={<Dashboard/>}/>
              <Route path='order' element={<Orders/>}/>
              <Route path='categories' element={<Category/>}/>
              <Route path='sellers' element={<Sellers/>}/>
              <Route path='products' element={<AdminProducts/>}/>
            </Route>
          </Route>
        </Routes>
      </Router>
      <Toaster position='bottom-center'/>
    </React.Fragment>
  )
}

export default App
