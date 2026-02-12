package com.stockcontrol.service;

import com.stockcontrol.dto.AuthRequest;
import com.stockcontrol.dto.AuthResponse;
import com.stockcontrol.entity.User;
import com.stockcontrol.exception.ConflictException;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.wildfly.security.password.Password;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.interfaces.BCryptPassword;
import org.wildfly.security.password.util.ModularCrypt;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

@ApplicationScoped
public class AuthService {

    private static final long TOKEN_DURATION_HOURS = 24;

    @Transactional
    public AuthResponse login(AuthRequest request) {
        User user = User.findByUsername(request.username);
        if (user == null || !verifyPassword(request.password, user.password)) {
            throw new RuntimeException("Usuário ou senha inválidos");
        }

        String token = generateToken(user);
        return new AuthResponse(token, user.username, TOKEN_DURATION_HOURS * 3600);
    }

    @Transactional
    public void register(AuthRequest request) {
        User existing = User.findByUsername(request.username);
        if (existing != null) {
            throw new ConflictException("Usuário '" + request.username + "' já existe");
        }

        User user = new User();
        user.username = request.username;
        user.password = hashPassword(request.password);
        user.role = "user";
        user.persist();
    }

    private String generateToken(User user) {
        return Jwt.issuer("stock-control")
                .upn(user.username)
                .groups(Set.of(user.role))
                .claim("username", user.username)
                .expiresAt(Instant.now().plus(Duration.ofHours(TOKEN_DURATION_HOURS)))
                .sign();
    }

    public String hashPassword(String plainPassword) {
        try {
            // Use Elytron's Modular Crypt to generate bcrypt hash
            PasswordFactory factory = PasswordFactory.getInstance(BCryptPassword.ALGORITHM_BCRYPT);
            // Generate a bcrypt hash using a simple approach
            byte[] salt = new byte[16];
            new java.security.SecureRandom().nextBytes(salt);
            BCryptPassword bCryptPassword = (BCryptPassword) factory.generatePassword(
                    new org.wildfly.security.password.spec.EncryptablePasswordSpec(
                            plainPassword.toCharArray(),
                            new org.wildfly.security.password.spec.IteratedSaltedPasswordAlgorithmSpec(10, salt)
                    )
            );
            return ModularCrypt.encodeAsString(bCryptPassword);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar hash da senha", e);
        }
    }

    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        try {
            PasswordFactory factory = PasswordFactory.getInstance(BCryptPassword.ALGORITHM_BCRYPT);
            Password password = factory.translate(ModularCrypt.decode(hashedPassword));
            return factory.verify(password, plainPassword.toCharArray());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar senha", e);
        }
    }
}
