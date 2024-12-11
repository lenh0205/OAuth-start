package sample.config.authorizationserver.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import sample.config.authorizationserver.entities.User;
import sample.config.authorizationserver.repositories.UserRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@Component
public class JpaUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(JpaUserDetailsService.class);
    private final UserRepository userRepository;
    DataSource dataSource;

    @Autowired
    public JpaUserDetailsService(UserRepository userRepository, DataSource dataSource) {
        this.userRepository = userRepository;
        this.dataSource = dataSource;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("----------> UserName {}", username);

        try (Connection connection = dataSource.getConnection()) {
            // Log connection details
            DatabaseMetaData metaData = connection.getMetaData();
            logger.info("----------> Database Product Name: {}", metaData.getDatabaseProductName());
            logger.info("----------> Database Product Version: {}", metaData.getDatabaseProductVersion());
            logger.info("----------> Driver Name: {}", metaData.getDriverName());
            logger.info("----------> Driver Version: {}", metaData.getDriverVersion());
            logger.info("----------> URL: {}", metaData.getURL());
            logger.info("----------> User: {}", metaData.getUserName());

            if (connection.isClosed()) {
                logger.error("----------> Connection is closed");
            } else {
                logger.info("----------> Connection is open");
            }
        } catch (SQLException e) {
            logger.error("----------> Error while checking database connection", e);
            throw new RuntimeException("Database connection error", e);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.info("----------> User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
//                .roles(user.getRoles().toArray(new String[0]))
                .build();
    }
}
