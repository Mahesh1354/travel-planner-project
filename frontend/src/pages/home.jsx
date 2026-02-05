import React, { useState } from "react";
import { motion } from "framer-motion";
import styled, { keyframes, createGlobalStyle } from "styled-components";
import Hero from "../components/common/Hero.jsx";
import Footer from "../components/common/Footer.jsx";
import Dubai from "../assets/dubai.jpg";
import germany from "../assets/germany.jpg";
import japan from "../assets/japan.jpg";
import canada from "../assets/canada.jpg";
import paris from "../assets/paris.jpg";
import thailand from "../assets/thailand.jpg";

// Global styles (assuming this is needed for the app)
const GlobalStyle = createGlobalStyle`
  * {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
  }

  body {
    font-family: 'Inter', sans-serif;
    line-height: 1.6;
    color: #747070;
    overflow-x: hidden; /* <-- prevent horizontal scrollbar on page */
  }
`;

// Keyframes for animations
const scaleKeyframes = keyframes`
  from, to {
    scale: var(--scale);
  }
  50% {
    scale: 1;
  }
  from {
    translate: var(--offset) 0;
  }
  to {
    translate: calc(var(--offset) * -1) 0;
  }
`;

const fadeKeyframes = keyframes`
  from, to {
    opacity: 0;
  }
  10%, 90% {
    opacity: 1;
  }
`;

// Container for normal page width
const Container = styled.div`
  max-width: 400px;
  margin: 0 auto;
  width: 100%;
  padding: 0;
  min-height: 80%;

  @media (min-width: 1024px) {
    max-width: 2000px;
    padding: 0;
  }

  @media (min-width: 1536px) {
    max-width: 2000px;
  }
`;

// Enhanced styled components for sections
const Section = styled.section`
  padding: 2rem 2rem;
  background: ${props => props.bg || '#ffffff'};
  text-align: center;
  position: relative;
  overflow: hidden;

  @media (min-width: 768px) {
    padding: 3rem 2rem;
  }

  @media (min-width: 1024px) {
    padding: 4rem 2rem;
  }

  &::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: ${props => props.overlay || 'rgba(255, 255, 255, 0.1)'};
    z-index: 0;
  }

  > * {
    position: relative;
    z-index: 1;
  }

  h2 {
    font-size: clamp(1.8rem, 4vw, 2.5rem);
    font-weight: 600;
    margin-bottom: 1.5rem;
    margin-top: -1rem;
    color: #393c42;

    @media (min-width: 1024px) {
      margin-bottom: 2rem;
    }
  }

  h3 {
    font-size: clamp(1.8rem, 4vw, 2.5rem);
    font-weight: 600;
    margin-bottom: 1.5rem;
    margin-top: -4rem;
    color: #393c42;

    @media (min-width: 1024px) {
      margin-bottom: 2rem;
    }
  }
`;

// Grid layout for cards
const ItemsGrid = styled.div`
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;
  margin-top: 2rem;

  @media (min-width: 640px) {
    grid-template-columns: repeat(2, 1fr);
    gap: 2rem;
  }

  @media (min-width: 1024px) {
    grid-template-columns: repeat(3, 1fr);
    gap: 2.5rem;
  }

  @media (min-width: 1536px) {
    grid-template-columns: repeat(4, 1fr);
    gap: 3rem;
  }
`;

// Horizontal scroll container for Popular Destinations
const HorizontalScrollContainer = styled.div`
  display: flex;
  gap: 0.1rem;
  margin-top: 1rem;
  padding-bottom: 1rem;

  overflow-x: auto;
  overflow-y: hidden;

  /* HARD hide scrollbar */
  scrollbar-width: none;        /* Firefox */
  -ms-overflow-style: none;     /* IE / Edge */

  &::-webkit-scrollbar {
    display: none;              /* Chrome / Safari */
  }

  @media (min-width: 1024px) {
    gap: 1.0rem;
  }
`;

const HorizontalCard = styled(motion.div)`
  background: linear-gradient(140deg, #ffffff, #f1f5f9);
  border-radius: 15px;
  padding: 1.8rem;
  box-shadow: 0 8px 10px rgb(245, 245, 245);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  flex: 0 0 280px;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;

  @media (min-width: 1024px) {
    padding: 2rem;
    flex: 0 0 320px;
  }

  @media (min-width: 1536px) {
    flex: 0 0 350px;
  }

  &:hover {
    box-shadow: 0 16px 40px rgb(224, 222, 222);
    transform: translateY(-1px);
  }

  img {
    width: 100%;
    height: 250px;
    object-fit: cover;
    margin-bottom: 1rem;
    border-radius: 10px;
    transition: transform 0.4s ease;
  }

  &:hover img {
    transform: scale(1.0);
  }

  .image-wrap {
    margin: -1.8rem;
    margin-bottom: 0.8rem;
  }

  .overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: linear-gradient(to top, rgb(255, 255, 255), rgb(255, 255, 255), transparent);
    color: black;
    padding: 1.5rem 1rem 1rem 1rem;
    font-weight: 700;
    font-size: 1.05rem;
    border-radius: 0 0 10px 10px;
    line-height: 1.4;
  }
`;

const Card = styled(motion.div)`
  background: linear-gradient(145deg, #ffffff, #f1f5f9);
  border-radius: 10px;
  padding: 1.5rem;
  box-shadow: 0 8px 24px rgb(192, 179, 179);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  height: 100%;
  display: flex;
  flex-direction: column;

  @media (min-width: 1024px) {
    padding: 2rem;
  }

  &:hover {
    box-shadow: 0 16px 40px rgba(0, 0, 0, 0.2);
    transform: translateY(-8px);
  }

  img {
    width: 100%;
    margin-bottom: 1rem;
    border-radius: 10px;
    transition: transform 0.4s ease;
  }

  &:hover img {
    transform: scale(1.0);
  }

  h5 {
    color: #1e3a8a;
    margin-bottom: 1rem;
    font-size: clamp(1.1rem, 3vw, 1.3rem);
    font-weight: 700;
  }

  p {
    color: #64748b;
    margin-bottom: 1.5rem;
    line-height: 1.6;
    font-size: 0.95rem;
    flex-grow: 1;

    @media (min-width: 1024px) {
      font-size: 1rem;
    }
  }

  .overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
    color: white;
    padding: 1.5rem;
    font-weight: bold;
    font-size: 1.1rem;
    border-radius: 0 0 10px 10px;
  }
`;

const Button = styled(motion.a)`
  display: inline-block;
  background: linear-gradient(45deg,  #384166 0%, #3a4872 );
  color: white;
  padding: 0.75rem 1.5rem;
  border-radius: 50px;
  text-decoration: none;
  font-weight: bold;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  cursor: pointer;
  font-size: 0.95rem;
  width: fit-content;

  @media (min-width: 1024px) {
    padding: 0.85rem 1.8rem;
    font-size: 1rem;
  }

  &:hover {
    box-shadow: 0 8px 20px rgba(236, 72, 153, 0.4);
    transform: translateY(-2px);
  }

  &:active {
    transform: translateY(0);
  }
`;

const DealsContainer = styled.div`
  display: grid;
  grid-template-columns: 1fr;
  gap: 2rem;
  margin-top: 2rem;

  @media (min-width: 768px) {
    grid-template-columns: repeat(2, 1fr);
    gap: 2.5rem;
  }

  @media (min-width: 1024px) {
    gap: 3rem;
  }
`;

function Home() {
  // Gentle animation variants
  const itemVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: { opacity: 1, y: 0, transition: { duration: 1, ease: "easeOut" } },
  };

  const fadeVariants = {
    hidden: { opacity: 0 },
    visible: { opacity: 1, transition: { duration: 1, ease: "easeOut" } },
  };

  const staggerContainer = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: { staggerChildren: 0.1, delayChildren: 0.1 },
    },
  };

  const destinationsData = [
    { img: Dubai, title: 'Dubai - Desert Luxury' },
    { img: japan, title: 'Tokyo - Modern Magic' },
    { img: thailand, title: 'Bangkok - Vibrant Culture' },
    { img: canada, title: 'Toronto - Urban Bliss' },
    { img: germany, title: 'Berlin - Historic Beauty' },
    { img: paris, title: 'Paris - City of Light' },
  ];

  const [hoveredIndex, setHoveredIndex] = useState(null);

  const categories = [
    { name: 'Cruises', image: 'https://images.unsplash.com/photo-1544551763-46a013bb70d5?auto=format&fit=crop&w=400&q=80' },
    { name: 'Hiking', image: 'https://images.unsplash.com/photo-1551632811-561732d1e306?auto=format&fit=crop&w=400&q=80' },
    { name: 'Airbirds', image: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?auto=format&fit=crop&w=400&q=80' },
    { name: 'Wildlife', image: 'https://images.unsplash.com/photo-1505142468610-359e7d316be0?auto=format&fit=crop&w=400&q=80' },
    { name: 'Walking', image: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?auto=format&fit=crop&w=400&q=80' }
  ];

  return (
    <>
      <GlobalStyle />  {/* Apply global styles */}
      <Hero />

      {/* Popular Destinations - Enhanced with Horizontal Scroll */}
      <Section bg="#f0f7f7" overlay="rgba(255, 255, 255, 0)">
        <motion.h2 variants={itemVariants} initial="hidden" whileInView="visible" viewport={{ once: true }}>
          Explore the World
        </motion.h2>
        <Container>
          <motion.div
            variants={fadeVariants}
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true }}
          >
            <HorizontalScrollContainer>
              {destinationsData.map((item, index) => (
                <HorizontalCard key={index} variants={itemVariants} whileHover={{ y: -1 }} style={{ position: 'relative' }}>
                  <img src={item.img} alt={item.title} loading="lazy" />
                  <div className="overlay">{item.title}</div>
                </HorizontalCard>
              ))}
            </HorizontalScrollContainer>
          </motion.div>
        </Container>
      </Section>

      {/* Top Deals */}
      <Section bg="#f0f7f7">
        <Container>
          <h3>Top Deals</h3>
          <motion.div
            variants={staggerContainer}
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true, amount: 0.2 }}
          >
            <DealsContainer>
              <Card variants={itemVariants} whileHover={{ y: -8 }}>
                <h5>Flight to Dubai</h5>
                <p>Starting from $299</p>
                <p>
                  Book now and save big on your next adventure!
                </p>
                <Button as="a" href="#" whileHover={{ scale: 1.05 }}>
                  Book Now
                </Button>
              </Card>

              <Card variants={itemVariants} whileHover={{ y: -8 }}>
                <h5>Hotel in Bali</h5>
                <p>Starting from $199/night</p>
                <p>
                  Amazing views and luxury awaits!
                </p>
                <Button as="a" href="#" whileHover={{ scale: 1.05 }}>
                  Book Now
                </Button>
              </Card>
            </DealsContainer>
          </motion.div>
        </Container>
      </Section>

      {/* Travel Packages */}
      <section className="bg-light py-5">
        <div className="container">
          <h2 className="text-center mb-5 fw-bold text-dark">Tour Categories</h2>
          <div className="row justify-content-center g-4">
            {categories.map((category, index) => (
              <div key={index} className="col-6 col-md-4 col-lg-2 d-flex justify-content-center">
                <div
                  className="card border-0 shadow-sm"
                  style={{
                    width: '200px',
                    transform: hoveredIndex === index ? 'scale(1.05)' : 'rotate(2deg)',
                    transition: 'transform 0.3s ease, box-shadow 0.3s ease',
                    boxShadow: hoveredIndex === index ? '0 10px 30px rgba(0,0,0,0.2)' : '0 4px 15px rgba(0,0,0,0.1)',
                    cursor: 'pointer'
                  }}
                  onMouseEnter={() => setHoveredIndex(index)}
                  onMouseLeave={() => setHoveredIndex(null)}
                >
                  <img
                    src={category.image}
                    className="card-img-top rounded-3"
                    alt={category.name}
                    style={{
                      height: '150px',
                      objectFit: 'cover',
                      transform: hoveredIndex === index ? 'scale(1.1)' : 'none',
                      transition: 'transform 0.3s ease'
                    }}
                  />
                  <div className="card-body text-center p-3">
                    <h5 className="card-title fw-semibold mb-2">{category.name}</h5>
                    <a href="#" className="text-muted small text-decoration-none">Read More</a>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>
      {/* Why Us Section */}
      <Section bg="#f4fcfc">
        <Container>
          <h2>Designed for the Way You Travel</h2>
          <motion.div
            variants={staggerContainer}
            initial="hidden"
            whileInView="visible"
            viewport={{ once: true, amount: 0.2 }}
          >
            <ItemsGrid>
              <Card variants={itemVariants} whileHover={{ y: -8 }}>
                <h5>Best Prices Ever</h5>
                <p>
                  Guaranteed low-key savings on every booking. We match or beat
                  any competitor's price.
                </p>
                <Button as="a" href="#" whileHover={{ scale: 1.05 }}>
                  Learn More
                </Button>
              </Card>

              <Card variants={itemVariants} whileHover={{ y: -8 }}>
                <h5>24/7 Squad</h5>
                <p>
                  Our support team is always here to help. Chat, call, or email
                  anytime.
                </p>
                <Button as="a" href="#" whileHover={{ scale: 1.05}}>
                  Contact Us
                </Button>
              </Card>

              <Card variants={itemVariants} whileHover={{ y: -8 }}>
                <h5>Secure AF</h5>
                <p>
                  Safe and secure transactions for peace of mind. Your data is
                  protected.
                </p>
                <Button as="a" href="#" whileHover={{ scale: 1.05 }}>
                  See Details
                </Button>
              </Card>
            </ItemsGrid>
          </motion.div>
        </Container>
      </Section><br />
      <section bg="#f0f7f7">
 <div className="col-lg-12 d-flex justify-content-center position-relative">
   
        <div className="card-body p-4 p-lg-3">
          <div className="row align-items-center">
            {/* Left Column */}
            <div className="col-lg-6 mb-4 mb-lg-0">
              <h2 className="display-5 fw-bold text-dark mb-3">About Us</h2>
              <p className="lead text-muted mb-2" style={{ fontSize: '0.95rem' }}>
                Embark on unforgettable journeys with us, where every destination tells a story and every experience creates lasting memories. Our passion for travel drives us to curate adventures that inspire and connect people from all walks of life.
              </p>
              <p className="text-muted mb-2" style={{ fontSize: '0.95rem' }}>
                From serene beaches to bustling cities, we believe in exploring the world with curiosity and respect, fostering a deeper appreciation for diverse cultures and breathtaking landscapes.
              </p><br /><br/>
              <button className="btn rounded-pill px-4 py-2 fw-semibold text-white" style={{ backgroundColor: '#2d324a',  fontSize: '0.85rem' }}>
                More about
              </button>
            </div>

            {/* Right Column */}
            <div className="col-lg-6 position-relative">
              <div className="position-relative">
                <img
                  src="https://images.unsplash.com/photo-1506905925346-21bda4d32df4?auto=format&fit=crop&w=500&q=80"
                  alt="Travel destination 1"
                  className="img-fluid rounded-3 shadow"
                  style={{ transform: 'rotate(-5deg)', width: '80%', height: 'auto' }}
                />
                <img
                  src="https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=500&q=80"
                  alt="Travel destination 2"
                  className="img-fluid rounded-3 shadow position-absolute top-50 start-50 translate-middle"
                  style={{ transform: 'rotate(5deg) translate(-50%, -50%)', width: '80%', height: 'auto', zIndex: 1 }}
                />
              </div>
              {/* Dotted Curved Path with Paper Plane */}
              <svg
                width="200"
                height="150"
                className="position-absolute top-0 end-0"
                style={{ zIndex: 2 }}
                viewBox="0 0 200 150"
              >
                <path
                  d="M10 140 Q50 50 150 20"
                  fill="none"
                  stroke="#3b3e5b"
                  strokeWidth="2"
                  strokeDasharray="5,5"
                  strokeLinecap="round"
                />
                <text x="160" y="15" fontSize="20" fill="#2d324a">✈</text>
              </svg>
            </div>
          </div>

          {/* Bottom Stats Row */}
          <div className="row mt-5 text-center">
            <div className="col-6 col-md-3 mb-3">
              <h4 className="fw-bold text-primary mb-1">10,000+</h4>
              <p className="text-muted small mb-0">Happy Travelers</p>
            </div>
            <div className="col-6 col-md-3 mb-3">
              <h4 className="fw-bold text-primary mb-1">50+</h4>
              <p className="text-muted small mb-0">Destinations</p>
            </div>
            <div className="col-6 col-md-3 mb-3">
              <h4 className="fw-bold text-primary mb-1">98%</h4>
              <p className="text-muted small mb-0">Satisfaction Rate</p>
            </div>
            <div className="col-6 col-md-3 mb-3">
              <h4 className="fw-bold text-primary mb-1">15+</h4>
              <p className="text-muted small mb-0">Years Experience</p>
            </div>
          </div>
        </div>
      </div>

</section><br /><br />
      <Footer />
    </>
  );
}

export default Home;