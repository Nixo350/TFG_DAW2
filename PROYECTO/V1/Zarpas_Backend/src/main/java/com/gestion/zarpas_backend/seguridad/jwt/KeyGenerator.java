package com.gestion.zarpas_backend.seguridad.jwt;

import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

public class KeyGenerator {
    public static void main(String[] args) {
        // Genera una clave segura de 256 bits (32 bytes) para HS256
        Key key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        // Codifica la clave en Base64 para que pueda ser almacenada como una cadena
        String base64Key = Encoders.BASE64.encode(key.getEncoded());
        System.out.println("Tu clave JWT generada (Base64):");
        System.out.println(base64Key);
    }
}