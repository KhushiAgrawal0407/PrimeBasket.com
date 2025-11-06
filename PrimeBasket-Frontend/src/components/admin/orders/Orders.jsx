import React from 'react'
import { FaShoppingCart } from 'react-icons/fa';
import OrderTable from './OrderTable';

const Orders = () => {
    const emptyOrder=true;
  return (
    <div className='pb-6 pt-20'>
        {emptyOrder ? (
            <div className='flex flex-col items-center text-gray-600 py-10'>
                <FaShoppingCart size={50} className='mb-3'/>
                <h2 className='text-2xl font-semibold'>
                    No Orders Placed Yet!
                </h2>
            </div>
        ) : (
            <div>
                <OrderTable/>
            </div>
        )}
    </div>
  )
}

export default Orders