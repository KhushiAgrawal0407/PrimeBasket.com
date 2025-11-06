import React from 'react'
import { formatPriceCalc } from '../../utils/formatPrice';

const OrderSummary = ({totalPrice, cart, address, paymentMethod}) => {
    return (
        <div className='container mx-auto px-4'>
            <div className='flex flex-wrap'>
                <div className='w-full lg:w-8/12 pr-4'>
                    <div className='space-y-4'>
                        <div className='p-4 border rounded-lg shadow-sm'>
                            <h2 className='text-2xl font-semibold mb-2'>
                                Billing Address
                            </h2>
                            <p className=''>
                                <strong>Building Name: </strong>
                                {address?.buildingName}
                            </p>
                            <p className=''>
                                <strong>City: </strong>
                                {address?.city}
                            </p>
                            <p className=''>
                                <strong>Street: </strong>
                                {address?.street}
                            </p>
                            <p className=''>
                                <strong>State: </strong>
                                {address?.state}
                            </p>
                            <p className=''>
                                <strong>Pincode: </strong>
                                {address?.pincode}
                            </p>
                            <p className=''>
                                <strong>Country: </strong>
                                {address?.country}
                            </p>
                        </div>
                        <div className='p-4 border rounded-lg shadow-sm'>
                            <h2 className='text-2xl font-semibold mb-2'>
                                Paymnet Method
                            </h2>
                            <p>
                                <strong>Method: </strong>
                                {paymentMethod}
                            </p>
                        </div>
                        <div className='p-4 border round-lg shadow-sm'>
                            <h2 className='text-2xl font-semibold mb-2'>
                                Order Items
                            </h2>
                            <div className='space-y-2'>
                                {cart?.map((item) => (
                                    <div key={item?.productId} className='flex items-center'>
                                        <img 
                                            src={`${import.meta.env.VITE_BACK_END_URL}/images/${
                                                item?.imageUrl
                                            }`}
                                            alt="Product"
                                            className='w-12 h-12 rounded'
                                        />
                                        <div className='text-gray-500'>
                                            <p>{item?.productName}</p>
                                            <p>{item?.quantity}x₹{item?.specialPrice} = ₹{formatPriceCalc(item?.quantity,item?.specialPrice)}</p>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </div>
                </div>
                <div className='w-full lg:w-4/12 mt-4 lg:mt-0'>
                    <div className='border rounded-lg shadow-sm p-4 space-y-4'>
                        <h2 className='text-2xl font-semibold mb-2'>
                            Order Summary
                        </h2>
                        <div className='space-y-2'>
                            <div className='flex justify-between'>
                                <span>Products</span>
                                <span>{formatPriceCalc(totalPrice, 1)}</span>
                            </div>
                            <div className='flex justify-between'>
                                <span>Tax (0%)</span>
                                <span>₹0.0</span>
                            </div>
                            <div className='flex justify-between'>
                                <span>Subtotal</span>
                                <span>{formatPriceCalc(totalPrice, 1)}</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default OrderSummary;