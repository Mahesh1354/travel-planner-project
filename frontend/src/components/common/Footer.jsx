import React from 'react';

const Footer = () => {
  const footerStyle = {
    backgroundColor: '#1a1a1a',
    color: '#ffffff',
    padding: '40px 20px 20px',
    fontFamily: 'Arial, sans-serif'
  };

  const containerStyle = {
    maxWidth: '1200px',
    margin: '0 auto'
  };

  const contentStyle = {
    display: 'flex',
    flexWrap: 'wrap',
    justifyContent: 'space-between',
    marginBottom: '30px',
    gap: '30px'
  };

  const columnStyle = {
    flex: '1',
    minWidth: '200px'
  };

  const headingStyle = {
    fontSize: '18px',
    fontWeight: 'bold',
    marginBottom: '15px',
    color: '#ffffff'
  };

  const listStyle = {
    listStyle: 'none',
    padding: '0',
    margin: '0'
  };

  const listItemStyle = {
    marginBottom: '10px'
  };

  const linkStyle = {
    color: '#b0b0b0',
    textDecoration: 'none',
    fontSize: '14px',
    transition: 'color 0.3s'
  };

  const socialContainerStyle = {
    display: 'flex',
    gap: '15px',
    marginTop: '15px'
  };

  const socialIconStyle = {
    width: '40px',
    height: '40px',
    borderRadius: '50%',
    backgroundColor: '#333',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    cursor: 'pointer',
    transition: 'background-color 0.3s'
  };

  const dividerStyle = {
    borderTop: '1px solid #333',
    margin: '20px 0'
  };

  const bottomStyle = {
    textAlign: 'center',
    fontSize: '14px',
    color: '#888'
  };

  const handleLinkHover = (e) => {
    e.target.style.color = '#ffffff';
  };

  const handleLinkLeave = (e) => {
    e.target.style.color = '#b0b0b0';
  };

  const handleSocialHover = (e) => {
    e.currentTarget.style.backgroundColor = '#555';
  };

  const handleSocialLeave = (e) => {
    e.currentTarget.style.backgroundColor = '#333';
  };

  return (
    <footer style={footerStyle} data-testid="footer">
      <div style={containerStyle}>
        <div style={contentStyle}>
          {/* Company Column */}
          <div style={columnStyle}>
            <h3 style={headingStyle}>Company</h3>
            <ul style={listStyle}>
              <li style={listItemStyle}>
                <a
                  href="#about"
                  style={linkStyle}
                  onMouseEnter={handleLinkHover}
                  onMouseLeave={handleLinkLeave}
                  data-testid="footer-about-link"
                >
                  About Us
                </a>
              </li>
              <li style={listItemStyle}>
                <a
                  href="#careers"
                  style={linkStyle}
                  onMouseEnter={handleLinkHover}
                  onMouseLeave={handleLinkLeave}
                  data-testid="footer-careers-link"
                >
                  Careers
                </a>
              </li>
              <li style={listItemStyle}>
                <a
                  href="#blog"
                  style={linkStyle}
                  onMouseEnter={handleLinkHover}
                  onMouseLeave={handleLinkLeave}
                  data-testid="footer-blog-link"
                >
                  Blog
                </a>
              </li>
              <li style={listItemStyle}>
                <a
                  href="#contact"
                  style={linkStyle}
                  onMouseEnter={handleLinkHover}
                  onMouseLeave={handleLinkLeave}
                  data-testid="footer-contact-link"
                >
                  Contact
                </a>
              </li>
            </ul>
          </div>

          {/* Products Column */}
          <div style={columnStyle}>
            <h3 style={headingStyle}>Products</h3>
            <ul style={listStyle}>
              <li style={listItemStyle}>
                <a
                  href="#features"
                  style={linkStyle}
                  onMouseEnter={handleLinkHover}
                  onMouseLeave={handleLinkLeave}
                  data-testid="footer-features-link"
                >
                  Features
                </a>
              </li>
              <li style={listItemStyle}>
                <a
                  href="#pricing"
                  style={linkStyle}
                  onMouseEnter={handleLinkHover}
                  onMouseLeave={handleLinkLeave}
                  data-testid="footer-pricing-link"
                >
                  Pricing
                </a>
              </li>
              <li style={listItemStyle}>
                <a
                  href="#demo"
                  style={linkStyle}
                  onMouseEnter={handleLinkHover}
                  onMouseLeave={handleLinkLeave}
                  data-testid="footer-demo-link"
                >
                  Demo
                </a>
              </li>
              <li style={listItemStyle}>
                <a
                  href="#updates"
                  style={linkStyle}
                  onMouseEnter={handleLinkHover}
                  onMouseLeave={handleLinkLeave}
                  data-testid="footer-updates-link"
                >
                  Updates
                </a>
              </li>
            </ul>
          </div>

          {/* Resources Column */}
          <div style={columnStyle}>
            <h3 style={headingStyle}>Resources</h3>
            <ul style={listStyle}>
              <li style={listItemStyle}>
                <a
                  href="#docs"
                  style={linkStyle}
                  onMouseEnter={handleLinkHover}
                  onMouseLeave={handleLinkLeave}
                  data-testid="footer-docs-link"
                >
                  Documentation
                </a>
              </li>
              <li style={listItemStyle}>
                <a
                  href="#support"
                  style={linkStyle}
                  onMouseEnter={handleLinkHover}
                  onMouseLeave={handleLinkLeave}
                  data-testid="footer-support-link"
                >
                  Support
                </a>
              </li>
              <li style={listItemStyle}>
                <a
                  href="#faq"
                  style={linkStyle}
                  onMouseEnter={handleLinkHover}
                  onMouseLeave={handleLinkLeave}
                  data-testid="footer-faq-link"
                >
                  FAQ
                </a>
              </li>
              <li style={listItemStyle}>
                <a
                  href="#community"
                  style={linkStyle}
                  onMouseEnter={handleLinkHover}
                  onMouseLeave={handleLinkLeave}
                  data-testid="footer-community-link"
                >
                  Community
                </a>
              </li>
            </ul>
          </div>

          {/* Legal Column */}
          <div style={columnStyle}>
            <h3 style={headingStyle}>Legal</h3>
            <ul style={listStyle}>
              <li style={listItemStyle}>
                <a
                  href="#privacy"
                  style={linkStyle}
                  onMouseEnter={handleLinkHover}
                  onMouseLeave={handleLinkLeave}
                  data-testid="footer-privacy-link"
                >
                  Privacy Policy
                </a>
              </li>
              <li style={listItemStyle}>
                <a
                  href="#terms"
                  style={linkStyle}
                  onMouseEnter={handleLinkHover}
                  onMouseLeave={handleLinkLeave}
                  data-testid="footer-terms-link"
                >
                  Terms of Service
                </a>
              </li>
              <li style={listItemStyle}>
                <a
                  href="#cookies"
                  style={linkStyle}
                  onMouseEnter={handleLinkHover}
                  onMouseLeave={handleLinkLeave}
                  data-testid="footer-cookies-link"
                >
                  Cookie Policy
                </a>
              </li>
            </ul>

            {/* Social Media Icons */}
            <div style={socialContainerStyle}>
              <div
                style={socialIconStyle}
                onMouseEnter={handleSocialHover}
                onMouseLeave={handleSocialLeave}
                data-testid="footer-facebook-icon"
              >
                <span style={{ fontSize: '18px' }}>f</span>
              </div>
              <div
                style={socialIconStyle}
                onMouseEnter={handleSocialHover}
                onMouseLeave={handleSocialLeave}
                data-testid="footer-twitter-icon"
              >
                <span style={{ fontSize: '18px' }}>t</span>
              </div>
              <div
                style={socialIconStyle}
                onMouseEnter={handleSocialHover}
                onMouseLeave={handleSocialLeave}
                data-testid="footer-linkedin-icon"
              >
                <span style={{ fontSize: '18px' }}>in</span>
              </div>
              <div
                style={socialIconStyle}
                onMouseEnter={handleSocialHover}
                onMouseLeave={handleSocialLeave}
                data-testid="footer-instagram-icon"
              >
                <span style={{ fontSize: '18px' }}>ig</span>
              </div>
            </div>
          </div>
        </div>

        <div style={dividerStyle}></div>

        <div style={bottomStyle} data-testid="footer-copyright">
          <p style={{ margin: '0' }}>
            &copy; {new Date().getFullYear()} Your Company. All rights reserved.
          </p>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
