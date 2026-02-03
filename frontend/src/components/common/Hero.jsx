import React from "react";
import { motion } from "framer-motion";
import styled from "styled-components";
import "bootstrap/dist/css/bootstrap.min.css";
import Navbar from "./Navbar"; // Assuming Navbar is in the same directory or adjust path
import img1 from "../../assets/1.jpg";
import img2 from "../../assets/2.jpg";
import img3 from "../../assets/3.jpg";
import img4 from "../../assets/4.jpg";
import img5 from "../../assets/5.jpg";
import img6 from "../../assets/6.jpg";
import img7 from "../../assets/7.jpg";
import img8 from "../../assets/abc.jpg";

// Images for the horizontal scrolling rail
const railImages = [img1, img2, img3, img4, img5, img6, img7];

// Styled components for Hero
const HeroContainer = styled.section`
  width: 100vw;
  min-height: calc(100vh - 80px);
  padding-top: 80px;
  position: relative;
  overflow: hidden;

  display: flex;
  align-items: center;
  justify-content: center;

  background-image: url(${img8});
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;

  &::before {
    content: "";
    position: absolute;
    inset: 0;
    background: rgba(0, 0, 0, 0.45);
    z-index: 1;
  }

  > * {
    position: relative;
    z-index: 2;
  }
`;

const HeroWrapper = styled.div`
  display: grid;
  grid-template-columns: 1fr 1fr; /* Two equal columns: text left, image right */
  gap: 2rem;
  max-width: 1200px;
  width: 90%;
  padding: 2rem;

  @media (max-width: 768px) {
    grid-template-columns: 1fr; /* Stack on mobile */
    gap: 1rem;
  }
`;

const TextSection = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #1e3a8a; /* Dark blue for contrast */
  z-index: 2;
`;

const RightGallery = styled.div`
  position: absolute;
  right: 60px;
  top: 50%;
  transform: translateY(-50%);

  width: 600px; /* Increased width for a larger bar */
  overflow: hidden;
  z-index: 2;

  /* Fade edges for premium look */
  mask-image: linear-gradient(
    to right,
    transparent,
    black 15%,
    black 85%,
    transparent
  );

  @media (max-width: 768px) {
    display: none; /* Hide on mobile, or adjust as needed */
  }
`;

const ScrollTrack = styled.div`
  display: flex;
  gap: 24px;
  width: max-content;

  animation: scrollX 30s linear infinite;

  &:hover {
    animation-play-state: paused;
  }

  @keyframes scrollX {
    from {
      transform: translateX(0);
    }
    to {
      transform: translateX(-50%);
    }
  }
`;

const ImageCard = styled.div`
  width: 280px; /* Slightly increased width to fit the larger bar */
  height: 320px; /* Further increased height for more vertical size */
  border-radius: 20px;
  overflow: hidden;

  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.45);
  background: #000;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  /* Slight vertical motion */
  animation: floatY 6s ease-in-out infinite;

  @keyframes floatY {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-6px); }
  }
`;

const Badge = styled(motion.span)`
  display: inline-block;
  background: linear-gradient(45deg, #fbbf24, #f59e0b); /* Gold gradient */
  color: #1e3a8a;
  padding: 0.5rem 1rem;
  border-radius: 50px;
  font-weight: bold;
  font-size: 0.9rem;
  text-transform: uppercase;
  margin-bottom: 1.5rem;
  box-shadow: 0 4px 12px rgba(251, 191, 36, 0.5);
`;

const Heading = styled(motion.h1)`
  font-family: 'Poppins', sans-serif;
  font-size: 4.5rem;
  font-weight: 800;
  line-height: 1.1;
  margin-bottom: 1rem;
  background: linear-gradient(45deg, #1e3a8a, #fbbf24); /* Blue to gold */
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;

  @media (max-width: 768px) {
    font-size: 3rem;
  }
`;

const Subtext = styled(motion.p)`
  font-family: 'Poppins', sans-serif;
  font-size: 1.25rem;
  font-weight: 400;
  line-height: 1.6;
  margin-bottom: 2rem;
  opacity: 0.9;
  max-width: 600px;

  @media (max-width: 768px) {
    font-size: 1rem;
  }
`;

const SearchBar = styled(motion.div)`
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50px;
  padding: 1rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  margin-bottom: 2rem;
  max-width: 500px;

  @media (max-width: 768px) {
    flex-direction: column;
    gap: 0.5rem;
  }
`;

const SearchInput = styled.input`
  border: none;
  outline: none;
  flex: 1;
  font-size: 1rem;
  padding: 0.5rem;
  background: transparent;
  color: #333;
`;

const SearchButton = styled(motion.button)`
  background: linear-gradient(45deg, #ec4899, #8b5cf6); /* Pink to purple */
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 50px;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(236, 72, 153, 0.4);
`;

const ButtonGroup = styled.div`
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;

  @media (max-width: 768px) {
    justify-content: center;
  }
`;

const PrimaryButton = styled(motion.button)`
  background: linear-gradient(45deg, #fbbf24, #f59e0b);
  color: #1e3a8a;
  border: none;
  padding: 1rem 2rem;
  border-radius: 50px;
  font-weight: 700;
  font-size: 1.1rem;
  cursor: pointer;
  box-shadow: 0 8px 20px rgba(251, 191, 36, 0.4);
`;

const SecondaryButton = styled(motion.button)`
  background: transparent;
  color: #1e3a8a;
  border: 2px solid #1e3a8a;
  padding: 1rem 2rem;
  border-radius: 50px;
  font-weight: 700;
  font-size: 1.1rem;
  cursor: pointer;
`;

function Hero() {
  // Animation variants
  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: { duration: 0.8, staggerChildren: 0.2 },
    },
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 30 },
    visible: { opacity: 1, y: 0, transition: { duration: 0.6, ease: "easeOut" } },
  };

  const bounceVariants = {
    hidden: { scale: 0 },
    visible: { scale: 1, transition: { type: "spring", stiffness: 300, damping: 20 } },
  };

  return (
    <>
      <Navbar />
      <HeroContainer>
        <HeroWrapper>
          {/* Text Section */}
          <TextSection>
            <motion.div variants={containerVariants} initial="hidden" animate="visible">
              <Badge variants={bounceVariants}> New: Lit Summer Deals 2026</Badge>
              <Heading variants={itemVariants}>Plan Your Dream Trip </Heading>
              <Subtext variants={itemVariants}>
                Flights, stays, vibes & more — all in one spot. Level up your vacay game with MyTrip! 🚀
              </Subtext>
              <SearchBar variants={itemVariants}>
                <SearchInput placeholder="Where to? " />
                <SearchButton whileHover={{ scale: 1.1 }} whileTap={{ scale: 0.9 }}>
                  Search Vibes 
                </SearchButton>
              </SearchBar>
              <ButtonGroup>
                <PrimaryButton variants={bounceVariants} whileHover={{ scale: 1.05 }} animate={{ scale: [1, 1.02, 1], transition: { duration: 2, repeat: Infinity } }}>
                  Explore Trips 🌟
                </PrimaryButton>
                <SecondaryButton variants={bounceVariants} whileHover={{ scale: 1.05 }}>
                  Create Plan 💡
                </SecondaryButton>
              </ButtonGroup>
            </motion.div>
          </TextSection>
        </HeroWrapper>

        {/* Horizontal Auto-Scrolling Image Rail */}
        <RightGallery>
          <ScrollTrack>
            {railImages.concat(railImages).map((img, index) => (
              <ImageCard key={index}>
                <img src={img} alt={`Rail Image ${index + 1}`} />
              </ImageCard>
            ))}
          </ScrollTrack>
        </RightGallery>
      </HeroContainer>
    </>
  );
}

export default Hero;