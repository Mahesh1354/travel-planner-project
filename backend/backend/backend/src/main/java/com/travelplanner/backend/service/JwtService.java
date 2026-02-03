    package com.travelplanner.backend.service;



    import com.travelplanner.backend.config.JwtProperties;
    import com.travelplanner.backend.entity.User;
    import io.jsonwebtoken.Claims;
    import io.jsonwebtoken.Jwts;
    import io.jsonwebtoken.SignatureAlgorithm;
    import io.jsonwebtoken.security.Keys;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.security.Key;
    import java.util.Date;

    @Service
    public class JwtService {

        @Autowired
        private JwtProperties jwtProperties;

        private Key getSigningKey() {
            byte[] keyBytes = jwtProperties.getSecretKey().getBytes();
            return Keys.hmacShaKeyFor(keyBytes);
        }

        public String generateToken(User user) {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + jwtProperties.getExpirationMs());

            return Jwts.builder()
                    .setSubject(user.getEmail())
                    .claim("userId", user.getId())
                    .claim("userType", user.getUserType().name())
                    .claim("fullName", user.getFullName())
                    .setIssuer(jwtProperties.getIssuer())
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        }

        public String getEmailFromToken(String token) {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        }

        public boolean validateToken(String token) {
            try {
                Jwts.parserBuilder()
                        .setSigningKey(getSigningKey())
                        .build()
                        .parseClaimsJws(token);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        public Claims getClaimsFromToken(String token) {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }
    }