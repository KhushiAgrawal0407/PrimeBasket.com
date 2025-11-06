import { bannerImageFour } from "../utils/constants";
import ProductCard from "./shared/ProductCard";

const products = [
    {
        imageUrl: "https://embarkx.com/sample/placeholder.png",
        productName: "iPhone 13 Pro Max",
        description:
          "The iPhone 13 Pro Max offers exceptional performance with its A15 Bionic chip, stunning Super Retina XDR display, and advanced camera features for breathtaking photos.",
        specialPrice: 720,
        price: 780,
    },
    {
        imageUrl: "https://embarkx.com/sample/placeholder.png",
        productName: "Samsung Galaxy S21",
        description:
          "Experience the brilliance of the Samsung Galaxy S21 with its vibrant AMOLED display, powerful camera, and sleek design that fits perfectly in your hand.",
        specialPrice: 699,
        price: 799,
    },
    {
        imageUrl: "https://embarkx.com/sample/placeholder.png",
        productName: "Google Pixel 6",
        description:
          "The Google Pixel 6 boasts cutting-edge AI features, exceptional photo quality, and a stunning display, making it a perfect choice for Android enthusiasts.",
        price: 599,
        specialPrice: 400,
    }
];

const About = () => {
    return(
        <div className="max-w-7xl mx-auto px-4 py-8">
            <h1 className="text-slate-800 text-4xl font-bold text-center mb-12">
                About Us 
            </h1>

            <div className="flex flex-col lg:flex-row justify-between items-center mb-12">
                <div className="w-full md:w-1/2 text-center md:text-left">
                    <p className="text-lg mb-4 leading-relaxed">
                        Welcome to our e-commerce store! We are passionate about bringing you high-quality products at the best possible value. Our journey began with a simple goal: to make shopping easier, more enjoyable, and more reliable for everyone. <br/>
                        At our core, we believe that shopping is more than just buying things—it’s about discovering products you’ll love, delivered with care and backed by trust. Every product we feature is carefully selected to meet our standards of quality, durability, and affordability. <br/>
                        What sets us apart is our commitment to our customers. From browsing to checkout to delivery, we focus on creating a seamless experience where your satisfaction comes first. Our support team is always ready to assist, ensuring that you can shop with confidence and peace of mind. <br/>
                        We also believe in constant improvement—listening to feedback, expanding our product range, and using technology to make your shopping journey smarter and faster.
                    </p>
                </div>
                <div className="w-full md:w-1/2 mb-6 md:mb-0">
                    <img src={bannerImageFour} alt="about us" className="w-full h-auto rounded-lg shadow-lg transform transition-transform duration-300 hover:scale-105"></img>
                </div>
            </div>

            <div className="py-7 space-y-8">
                <h1 className="text-slate-800 text-4xl font-bold text-center">
                    Our Products
                </h1>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {products.map((product,index) => (
                        <ProductCard
                            key={index}
                            imageUrl={product.imageUrl}
                            productName={product.productName}
                            description={product.description}
                            specialPrice={product.specialPrice}
                            price={product.price}
                            about
                        />
                    ))}
                </div>
            </div>
        </div>
    );
};

export default About;