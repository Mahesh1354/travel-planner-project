import React from "react";
import { motion } from "framer-motion";
import styled from "styled-components";
import Hero from "../components/common/Hero.jsx";

// Enhanced styled components for sections
const Section = styled.section`
  padding: 5rem 2rem;
  background: ${props => props.bg || '#ffffff'};
  text-align: center;
  position: relative;
  overflow: hidden;

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
    font-size: clamp(2rem, 5vw, 3rem);
    font-weight: 800;
    margin-bottom: 1rem;
    color: #0f172a;
  }

  @media (max-width: 768px) {
    padding: 3rem 1rem;
  }
`;

const ImageGrid = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  margin-top: 3rem;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
`;

const ImageCardGrid = styled(motion.div)`
  position: relative;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  background: linear-gradient(145deg, #ffffff, #f1f5f9);
  transition: all 0.3s ease;
  aspect-ratio: 3 / 2;

  &:hover {
    transform: translateY(-10px) scale(1.05);
    box-shadow: 0 20px 50px rgba(0, 0, 0, 0.25);
  }

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.4s ease;
  }

  &:hover img {
    transform: scale(1.15);
  }

  .overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: linear-gradient(to top, rgba(0,0,0,0.8), transparent);
    color: white;
    padding: 1.5rem;
    font-weight: bold;
    font-size: 1.1rem;
  }
`;

const Card = styled(motion.div)`
  background: linear-gradient(145deg, #ffffff, #f1f5f9);
  border-radius: 15px;
  padding: 2rem;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  height: 100%;

  &:hover {
    transform: scale(1.05);
    box-shadow: 0 16px 40px rgba(0, 0, 0, 0.2);
  }

  h5 {
    color: #1e3a8a;
    margin-bottom: 1rem;
    font-size: 1.3rem;
    font-weight: 700;
  }

  p {
    color: #64748b;
    margin-bottom: 1.5rem;
    line-height: 1.6;
  }
`;

const Button = styled(motion.a)`
  display: inline-block;
  background: linear-gradient(45deg, #ec4899, #8b5cf6);
  color: white;
  padding: 0.75rem 1.5rem;
  border-radius: 50px;
  text-decoration: none;
  font-weight: bold;
  transition: all 0.3s ease;
  border: none;
  cursor: pointer;

  &:hover {
    transform: scale(1.1);
    box-shadow: 0 8px 20px rgba(236, 72, 153, 0.4);
  }

  &:active {
    transform: scale(0.98);
  }
`;

const AppPromoContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2rem;

  h2 {
    color: #ffffff;
    margin-bottom: 0.5rem;
  }

  .app-promo-text {
    color: rgba(255, 255, 255, 0.9);
    font-size: 1.1rem;
    margin-bottom: 1rem;
  }

  .app-buttons {
    display: flex;
    gap: 1rem;
    justify-content: center;
    flex-wrap: wrap;

    button {
      background: rgba(255, 255, 255, 0.95);
      color: #1e3a8a;
      border: none;
      padding: 0.9rem 2rem;
      border-radius: 50px;
      font-weight: 700;
      cursor: pointer;
      transition: all 0.3s ease;
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);

      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 12px 32px rgba(0, 0, 0, 0.3);
      }

      &:active {
        transform: translateY(-1px);
      }
    }
  }
`;

const Footer = styled.footer`
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  color: #e2e8f0;
  padding: 3rem 2rem;
  text-align: center;
  margin-top: 2rem;

  .footer-content {
    max-width: 1200px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 2rem;
    margin-bottom: 2rem;

    .footer-section {
      text-align: left;

      h4 {
        color: #ffffff;
        margin-bottom: 1rem;
        font-weight: 700;
      }

      ul {
        list-style: none;
        padding: 0;

        li {
          margin-bottom: 0.5rem;

          a {
            color: rgba(226, 232, 240, 0.7);
            text-decoration: none;
            transition: color 0.3s ease;

            &:hover {
              color: #60a5fa;
            }
          }
        }
      }

      p {
        color: rgba(226, 232, 240, 0.7);
        line-height: 1.6;
      }
    }
  }

  .footer-bottom {
    border-top: 1px solid rgba(226, 232, 240, 0.1);
    padding-top: 2rem;
    color: rgba(226, 232, 240, 0.6);

    p {
      margin: 0;
    }
  }

  @media (max-width: 768px) {
    padding: 2rem 1rem;

    .footer-content {
      grid-template-columns: 1fr;
      gap: 1.5rem;

      .footer-section {
        text-align: center;
      }
    }
  }
`;

function Home() {
  // Enhanced animation variants
  const itemVariants = {
    hidden: { opacity: 0, y: 50 },
    visible: { 
      opacity: 1, 
      y: 0, 
      transition: { duration: 0.8, ease: "easeOut" } 
    },
  };

  const bounceVariants = {
    hidden: { scale: 0, opacity: 0 },
    visible: { 
      scale: 1, 
      opacity: 1,
      transition: { type: "spring", stiffness: 300, damping: 20 } 
    },
  };

  const staggerContainer = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: { staggerChildren: 0.2, delayChildren: 0.1 },
    },
  };

  return (
    <>
      <Hero />

      {/* Viral Spots Section */}
      <Section bg="#ffffff" overlay="rgba(255, 255, 255, 0)">
        <motion.h2 variants={itemVariants} initial="hidden" whileInView="visible" viewport={{ once: true }}>
          Viral Spots to Flex On
        </motion.h2>
        <ImageGrid>
          {[
            { img: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?auto=format&fit=crop&q=80&w=400', title: 'Paris Glow-Up' },
            { img: 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&q=80&w=400', title: 'Tokyo Vibes' },
            { img: 'https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&q=80&w=400', title: 'NYC Energy' },
          ].map((item, index) => (
            <ImageCardGrid key={index} variants={bounceVariants} initial="hidden" whileInView="visible" viewport={{ once: true }}>
              <img src={item.img} alt={item.title} loading="lazy" />
              <div className="overlay">{item.title}</div>
            </ImageCardGrid>
          ))}
        </ImageGrid>
      </Section>

      {/* Why Us Section */}
      <Section bg="#ffffff" overlay="rgba(255, 255, 255, 0)">
        <motion.h2 variants={itemVariants} initial="hidden" whileInView="visible" viewport={{ once: true }}>
          Why MyTrip? Because We're That Cool
        </motion.h2>
        <motion.div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '2rem', marginTop: '3rem' }} variants={staggerContainer} initial="hidden" whileInView="visible" viewport={{ once: true }}>
          <Card variants={bounceVariants} whileHover={{ scale: 1.1 }}>
            <h5>Best Prices Ever</h5>
            <p>Guaranteed low-key savings on every booking. We match or beat any competitor's price.</p>
            <Button href="#" whileHover={{ scale: 1.05 }}>Learn More</Button>
          </Card>
          <Card variants={bounceVariants} whileHover={{ scale: 1.1 }}>
            <h5>24/7 Squad</h5>
            <p>Our support team is always here to help. Chat, call, or email anytime.</p>
            <Button href="#" whileHover={{ scale: 1.05 }}>Contact Us</Button>
          </Card>
          <Card variants={bounceVariants} whileHover={{ scale: 1.1 }}>
            <h5>Secure AF</h5>
            <p>Safe and secure transactions for peace of mind. Your data is protected.</p>
            <Button href="#" whileHover={{ scale: 1.05 }}>See Details</Button>
          </Card>
        </motion.div>
      </Section>

      {/* Quick Search Tabs */}
      <Section bg="#ffffff" overlay="rgba(255, 255, 255, 0)">
        <motion.h2 variants={itemVariants} initial="hidden" whileInView="visible" viewport={{ once: true }}>
          Quick Search
        </motion.h2>
        <motion.div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '2rem', marginTop: '3rem' }} variants={staggerContainer} initial="hidden" whileInView="visible" viewport={{ once: true }}>
          <Card variants={bounceVariants}>
            <h5>Flights</h5>
            <p>Find the best flight deals instantly.</p>
            <Button href="/flights" whileHover={{ scale: 1.1 }}>Search Flights</Button>
          </Card>
          <Card variants={bounceVariants}>
            <h5>Hotels</h5>
            <p>Book your perfect stay with ease.</p>
            <Button href="/hotels" whileHover={{ scale: 1.1 }}>Search Hotels</Button>
          </Card>
          <Card variants={bounceVariants}>
            <h5>Packages</h5>
            <p>All-inclusive travel packages await.</p>
            <Button href="/packages" whileHover={{ scale: 1.1 }}>View Packages</Button>
          </Card>
          <Card variants={bounceVariants}>
            <h5>Activities</h5>
            <p>Explore local experiences and fun.</p>
            <Button href="/activities" whileHover={{ scale: 1.1 }}>Find Activities</Button>
          </Card>
        </motion.div>
      </Section>

      {/* Popular Destinations */}
      <Section bg="#ffffff" overlay="rgba(255, 255, 255, 0)">
        <motion.h2 variants={itemVariants} initial="hidden" whileInView="visible" viewport={{ once: true }}>
          Popular Destinations
        </motion.h2>
        <ImageGrid>
          {[
            { img: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?auto=format&fit=crop&q=80&w=400', title: 'Paris - The City of Light' },
            { img: 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&q=80&w=400', title: 'Tokyo - Modern Magic' },
            { img: 'https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&q=80&w=400', title: 'New York - The Big Apple' },
          ].map((item, index) => (
            <ImageCardGrid key={index} variants={bounceVariants} initial="hidden" whileInView="visible" viewport={{ once: true }}>
              <img src={item.img} alt={item.title} loading="lazy" />
              <div className="overlay">{item.title}</div>
            </ImageCardGrid>
          ))}
        </ImageGrid>
      </Section>

      {/* Top Deals */}
      <Section bg="#ffffff" overlay="rgba(255, 255, 255, 0)">
        <motion.h2 variants={itemVariants} initial="hidden" whileInView="visible" viewport={{ once: true }}>
          Top Deals
        </motion.h2>
        <motion.div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '2rem', marginTop: '3rem' }} variants={staggerContainer} initial="hidden" whileInView="visible" viewport={{ once: true }}>
          <Card variants={bounceVariants}>
            <h5>Flight to Dubai</h5>
            <p>Starting from $299 – Book now and save big on your next adventure!</p>
            <Button href="/flights" whileHover={{ scale: 1.1 }}>Book Now</Button>
          </Card>
          <Card variants={bounceVariants}>
            <h5>Hotel in Bali</h5>
            <p>Starting from $199/night – Amazing views and luxury awaits!</p>
            <Button href="/hotels" whileHover={{ scale: 1.1 }}>Book Now</Button>
          </Card>
        </motion.div>
      </Section>

      {/* Why Choose Us */}
      <Section bg="#ffffff" overlay="rgba(255, 255, 255, 0)">
        <motion.h2 variants={itemVariants} initial="hidden" whileInView="visible" viewport={{ once: true }}>
          Why Choose Us
        </motion.h2>
        <motion.div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '2rem', marginTop: '3rem' }} variants={staggerContainer} initial="hidden" whileInView="visible" viewport={{ once: true }}>
          <Card variants={bounceVariants}>
            <h5>Best Prices</h5>
            <p>We guarantee the lowest prices on the market with price match guarantee.</p>
          </Card>
          <Card variants={bounceVariants}>
            <h5>24/7 Support</h5>
            <p>Customer support available anytime, anywhere for your convenience.</p>
          </Card>
          <Card variants={bounceVariants}>
            <h5>Secure Booking</h5>
            <p>Safe and secure transactions with encrypted payment for peace of mind.</p>
          </Card>
        </motion.div>
      </Section>

      {/* Travel Packages */}
      <Section bg="#ffffff" overlay="rgba(255, 255, 255, 0)">
        <motion.h2 variants={itemVariants} initial="hidden" whileInView="visible" viewport={{ once: true }}>
          Travel Packages
        </motion.h2>
        <ImageGrid>
          {[
            { img: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?auto=format&fit=crop&q=80&w=400', title: 'Europe Tour - 7 days' },
            { img: 'https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&q=80&w=400', title: 'Asia Adventure - 10 days' },
            { img: 'https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&q=80&w=400', title: 'America Explorer - 14 days' },
          ].map((item, index) => (
            <ImageCardGrid key={index} variants={bounceVariants} initial="hidden" whileInView="visible" viewport={{ once: true }}>
              <img src={item.img} alt={item.title} loading="lazy" />
              <div className="overlay">{item.title}</div>
            </ImageCardGrid>
          ))}
        </ImageGrid>
      </Section>

    </>
  );
}

export default Home;