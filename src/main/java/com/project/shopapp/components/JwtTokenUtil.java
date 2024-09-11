package com.project.shopapp.components;

import com.project.shopapp.exception.InvalidParamException;
import com.project.shopapp.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    @Value("${jwt.expiration}")
    private int expiration;// save to an environment variable

    @Value("${jwt.secretKey}")
    private String secretKey;


    public String generateToken(User user) throws Exception {
        //properties => claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("phoneNumber",user.getPhoneNumber());
        //this.generateSecretKey();

        try {
            String token = Jwts.builder()
                    .claims(claims)
                    .subject(user.getPhoneNumber())
                    .expiration(new Date(System.currentTimeMillis()+ expiration*1000L))
                    .signWith(getSignInKey(),Jwts.SIG.HS256)
                    .compact();
            return token;
        }catch (Exception e){
            throw new InvalidParamException("Can't create jwt token,erer"+e.getMessage());
        }


    }

    private SecretKey getSignInKey(){
        byte[] bytes = Decoders.BASE64.decode(secretKey);// mã hóa secretKey thành base64 rồi chuyển thành mảng byte
        //Keys.hmacShaKeyFor(Decoders.BASE64.decode("2hjWaXiNXhPED+tyuF6/hSnRNhX0/IRC0mXWCuK1Bx0="))
        return Keys.hmacShaKeyFor(bytes);//tạo ra đối tượng key được giải mã theo thuật toán HMAC SHA
    }

    private String generateSecretKey(){
        SecureRandom secureRandom= new SecureRandom();
        byte[] keyBytes = new byte[32]; // Tạo mảng byte dài 32 byte (256 bit)
        secureRandom.nextBytes(keyBytes); // Tạo ngẫu nhiên các byte
        String secretKey= Encoders.BASE64.encode(keyBytes); // Mã hóa mảng byte thành chuỗi base64
        return secretKey; // Trả về chuỗi mã hóa
    }


    //tạo đối tương jwtParserBuilder để cấu hình-> cấu hình signature key -> tạo JWtParser để phân tích token
    // sau khi phân tích return Jws<claims>(chứa payload và chữ kí)-> dùng getPayLoad() để lấy payload
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    //cần thuộc tính gì thì truyền vào claimsResolver
    public <T> T extractClaim (String token , Function<Claims,T> claimsResolver){
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);


    }

    public boolean isTokenExpired(String token){

        Date expirationDate = extractClaim(token,Claims::getExpiration);
        return expirationDate.before(new Date()); // hạn ngày trước ngày hiện tại is true
    }

    public String extractPhoneNumber(String token){

        return extractClaim(token,Claims::getSubject);
    }

    //kiểm tra hợp lệ của token username trong token trùng với userdetails khong
    // còn hạn token hay không
    public boolean isValidateToken(String token, UserDetails userDetails){
        String phoneNumber = extractPhoneNumber(token);
        return (phoneNumber.equals(userDetails.getUsername()))
                && !isTokenExpired(token);
    }
}
